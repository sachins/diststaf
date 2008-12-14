package name.sachin.diststaf.service;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import name.sachin.diststaf.obj.Node;
import static name.sachin.diststaf.service.DistStafConstants.*;

public class AddNodeRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger
			.getLogger(AddNodeRequest.class);

	public AddNodeRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		return "ADDNODE";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(
				ADDNODE_TRUST_LEVEL, service.getServiceName(),
				getRequestName(), service.getLocalMachineName(), reqInfo);
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

		// res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("type"),
		// service.getStafHandle(), reqInfo.requestNumber);
		// NodeType type = NodeType.valueOf(res.result.toUpperCase());

		NodeType type = NodeType.MACHINE;

		if (service.nodeNameExists(nodeName, type)) {
			return new STAFResult(NODE_EXISTS, "Node with name:["
					+ nodeName + "] and type:[" + type + "] already exists");
		}
		if (res.rc != STAFResult.Ok)
			return res;

		Node newNode = new Node(nodeName, type);
		if (!service.addNode(newNode))
			return new STAFResult(ADD_NODE_FAILED,
					"Failed to add node:[" + nodeName + "]");
		return new STAFResult(STAFResult.Ok);
	}

	@Override
	protected String helpString() {
		return getRequestName() + " <Node name>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddNodeRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("TYPE", 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized AddNodeRequest Parser Successfully");
	}

}
