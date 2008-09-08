package name.sachin.diststaf.service;

import org.apache.log4j.Logger;

import name.sachin.diststaf.obj.Job;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import static name.sachin.diststaf.service.wrapper.DistStafConstants.*;

public class AddJobRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(AddJobRequest.class);

	public AddJobRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	protected void initParser() {
		LOG.debug("Initializing AddJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("ALGORITHM", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("DATA", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOptionNeed("ADDJOB", "ALGORITHM");
		LOG.debug("Initialized AddJobRequest Parser Successfully");
	}

	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(ADDJOB_TRUST_LEVEL,
				service.getServiceName(), getRequestName(), service
						.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;
		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);

		STAFResult res;

		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue(getRequestName()),
				service.getStafHandle(), reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;

		String jobName = res.result;

		if (service.jobNameExists(jobName))
			return new STAFResult(JOB_EXISTS, "Job with name:[" + jobName
					+ "] already exists");

		res = STAFUtil.resolveRequestVar(
				parsedRequest.optionValue("algorithm"),
				service.getStafHandle(), reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;

		String algorithm = res.result;

		Job newJob = new Job(jobName, algorithm);
		if (!service.addJob(newJob))
			return new STAFResult(ADD_JOB_FAILED, "Failed to add job:["
					+ jobName + "]");
		return new STAFResult(STAFResult.Ok);
	}

	@Override
	protected String helpString() {

		return getRequestName()
				+ " <Job name> ALGORITHM <Algorithm in the form of JAR/Binary/Command> "
				+ "[DATA <Input Data to algorithm>]";
	}

	@Override
	protected String getRequestName() {
		return "ADDJOB";
	}

}
