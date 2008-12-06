package name.sachin.diststaf.service;

import name.sachin.diststaf.obj.AtomicTask;
import name.sachin.diststaf.obj.Job;

import org.apache.log4j.Logger;

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

		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("taskname"),
				service.getStafHandle(), reqInfo.requestNumber);

		String taskName = null;
		AtomicTask task = null;
		if (res.rc == STAFResult.Ok && !"".equals(res.result)) {
			taskName = res.result;
			LOG.info("Task name resolved:" + taskName);
			task = job.findTask(taskName);
			if (task == null) {
				return new STAFResult(TASK_DOESNT_EXIST, "Task with name:["
						+ taskName + "] doesn't exist");
			}
		}

		if (task != null) {
			return task.execute(reqInfo, service.getStafHandle(), jobName);
		}
		String result = job.execute(reqInfo, service.getStafHandle()).toString();
		return new STAFResult(STAFResult.Ok, result);
	}

	@Override
	protected String helpString() {
		return "EXECUTEJOB <Existing Job Name> [TASKNAME <Existing Task Name in the Job>]";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing ExecuteJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("taskname", 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized ExecuteJobRequest Parser Successfully");

	}

}
