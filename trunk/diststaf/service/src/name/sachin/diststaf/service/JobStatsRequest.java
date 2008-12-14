package name.sachin.diststaf.service;

import java.util.ArrayList;
import java.util.List;

import name.sachin.diststaf.obj.AtomicTask;
import name.sachin.diststaf.obj.Job;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import static name.sachin.diststaf.service.DistStafConstants.*;

public class JobStatsRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(JobStatsRequest.class);

	public JobStatsRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		return "JOBSTATS";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
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
			return new STAFResult(JOB_DOESNT_EXIST, "Job with name " + jobName
					+ " doesn't exist");
		}

		String rsp = "Job description:" + job;
		List<AtomicTask> finishedTasks = new ArrayList<AtomicTask>();
		List<AtomicTask> unfinishedTasks = new ArrayList<AtomicTask>();
		long totalTime = 0;
		boolean jobFinished = true;
		for (AtomicTask t : job.getTasks()) {
			if (TaskStatus.DONE.equals(t.getStatus())) {
				long timeTaken = t.getEndTime() - t.getStartTime();
				if (timeTaken > 0) {
					finishedTasks.add(t);
					totalTime += timeTaken;
				} else {
					unfinishedTasks.add(t);
					t.setStatus(TaskStatus.FAILED);
				}
			} else {
				unfinishedTasks.add(t);
				jobFinished = false;
			}
		}
		if (!finishedTasks.isEmpty()) {
			rsp += "\n\nFinished tasks:" + finishedTasks;
			rsp += "\n\n Average time to finish each task:"
					+ (totalTime / finishedTasks.size());
		}
		if (!unfinishedTasks.isEmpty()) {
			rsp += "\n\nUnfinished tasks:" + unfinishedTasks;
		}
		if (jobFinished)
			job.setStatus(JobStatus.FINISHED);
		return new STAFResult(STAFResult.Ok, rsp);
	}

	@Override
	protected String helpString() {
		return getRequestName() + " <Job name>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized AddJobRequest Parser Successfully");
	}

}
