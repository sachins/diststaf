package name.sachin.diststaf.service;

import static name.sachin.diststaf.service.DistStafConstants.*;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class DeleteNodeRequest extends AbstractStafRequest {
	
	private static final Logger LOG = Logger.getLogger(DeleteNodeRequest.class);
	
	public DeleteNodeRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		return "DELETENODE";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(DELETE_NODE_TRUST_LEVEL,
				service.getServiceName(), getRequestName(), service
						.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;

		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);

		STAFResult res;

		res = STAFUtil.resolveRequestVar(parsedRequest
				.optionValue(getRequestName()), service.getStafHandle(),
				reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;

		String nodeName = res.result;

		return service.removeNode(nodeName) ? new STAFResult(STAFResult.Ok)
				: new STAFResult(DELETE_NODE_FAILED, "Failed to delete node");
	}

	@Override
	protected String helpString() {
		return getRequestName() + " <Node name>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing DeleteNodeRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized DeleteNodeRequest Parser Successfully");
	}

}
