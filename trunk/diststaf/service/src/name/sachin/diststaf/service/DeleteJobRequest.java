package name.sachin.diststaf.service;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import static name.sachin.diststaf.service.DistStafConstants.*;

public class DeleteJobRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(DeleteJobRequest.class);

	public DeleteJobRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(DELETE_JOB_TRUST_LEVEL,
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

		String jobName = res.result;

		return service.removeJob(jobName) ? new STAFResult(STAFResult.Ok)
				: new STAFResult(DELETE_JOB_FAILED, "Failed to delete job");
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized AddJobRequest Parser Successfully");
	}

	@Override
	protected String getRequestName() {
		return "DELETEJOB";
	}

	@Override
	protected String helpString() {
		return getRequestName() + " <Job name>";
	}

}
