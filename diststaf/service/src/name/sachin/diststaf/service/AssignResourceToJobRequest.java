package name.sachin.diststaf.service;

import name.sachin.diststaf.obj.Job;
import name.sachin.diststaf.obj.Resource;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import static name.sachin.diststaf.service.DistStafConstants.*;

public class AssignResourceToJobRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger
			.getLogger(AssignResourceToJobRequest.class);


	public AssignResourceToJobRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		return "ASSIGNRESOURCETOJOB";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(
				ASSIGNRESOURCETOJOB_TRUST_LEVEL, service.getServiceName(),
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

		String resName = res.result;

		Resource resource = service.findResource(resName);

		if (resource == null) {
			return new STAFResult(RESOURCE_DOESNT_EXIST, "Resource with name:["
					+ resName + "] doesn't exist");
		}

		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("job"),
				service.getStafHandle(), reqInfo.requestNumber);

		String jobName = res.result;

		Job job = service.findJob(jobName);

		if (job == null) {
			return new STAFResult(JOB_DOESNT_EXIST, "Job with name:[" + jobName
					+ "] doesn't exist");
		}

		if (job.findResource(resName) != null) {
			return new STAFResult(RESOURCE_ALREADY_ASSIGNED, "Resource:["
					+ resName + "] already assigned to Job:[" + jobName + "]");
		}

		if (!job.getResources().add(resource)) {
			return new STAFResult(ASSIGN_RESOURCE_TO_JOB_FAILED,
					"Failed to assign resource:[" + resName + "] to job [");
		}

		return new STAFResult(STAFResult.Ok);
	}

	@Override
	protected String helpString() {
		return getRequestName() + " <Resource name> JOB <Job Name>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AssignResourceToJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("JOB", 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized AssignResourceToJobRequest Parser Successfully");
	}

}
