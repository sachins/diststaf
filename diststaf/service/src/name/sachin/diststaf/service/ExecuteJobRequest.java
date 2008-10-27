package name.sachin.diststaf.service;

import java.util.List;

import name.sachin.diststaf.obj.Job;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import static name.sachin.diststaf.service.DistStafConstants.*;

public class ExecuteJobRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(ExecuteJobRequest.class);

	public ExecuteJobRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}
	
	@Override
	protected String getRequestName() {
		return "executejob";
	}

	@SuppressWarnings("unchecked")
	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(EXECUTEJOB_TRUST_LEVEL,
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

		Job job = service.findJob(jobName);

		if (job == null) {
			return new STAFResult(JOB_DOESNT_EXIST, "Job with name:[" + jobName
					+ "] doesn't exist");
		}

		List resultList;
		try {
			resultList = job.execute(reqInfo);
		} catch (STAFException e) {
			LOG.error("Failed to execute the job " + job, e);
			return new STAFResult(EXECUTEJOB_FAILED, "Failed to execute Job "
					+ job);
		}
		return new STAFResult(STAFResult.Ok, resultList.toString());
	}

	@Override
	protected String helpString() {
		return "EXECUTEJOB <Existing Job Name>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing ExecuteJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized ExecuteJobRequest Parser Successfully");

	}

}
