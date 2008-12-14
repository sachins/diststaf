package name.sachin.diststaf.service;

import static name.sachin.diststaf.service.DistStafConstants.*;

import name.sachin.diststaf.obj.AtomicTask;
import name.sachin.diststaf.obj.Job;
import name.sachin.diststaf.obj.Node;
import name.sachin.diststaf.service.DistStafConstants.AlgorithmType;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class AddTaskRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(AddTaskRequest.class);

	public AddTaskRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		return "ADDTASK";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(ADDTASK_TRUST_LEVEL,
				service.getServiceName(), getRequestName(), service
						.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;
		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);

		STAFResult res;

		// Resolve task name
		res = STAFUtil.resolveRequestVar(parsedRequest
				.optionValue(getRequestName()), service.getStafHandle(),
				reqInfo.requestNumber);
		if (res.rc != STAFResult.Ok)
			return res;
		String taskName = res.result;

		// Resolve job name
		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("jobname"),
				service.getStafHandle(), reqInfo.requestNumber);
		if (res.rc != STAFResult.Ok)
			return res;
		String jobName = res.result;

		// Make sure job with given name exists
		Job job = service.findJob(jobName);
		if (job == null) {
			return new STAFResult(JOB_DOESNT_EXIST, "Job with name:[" + jobName
					+ "] doesn't exist");
		}
		// Make sure task with the given name already doesn't exist in the job
		if (job.findTask(taskName) != null) {
			return new STAFResult(TASK_ALREADY_ASSIGNED, "Task:[" + taskName
					+ "] already assigned to Job:[" + jobName + "]");
		}

		// Resolve node name
		res = STAFUtil.resolveRequestVar(parsedRequest
				.optionValue("nodename"), service.getStafHandle(),
				reqInfo.requestNumber);
		if (res.rc != STAFResult.Ok)
			return res;
		String nodeName = res.result;

		// Make sure node with given name exists
		Node node = service.findNode(nodeName);
		if (node == null) {
			return new STAFResult(NODE_DOESNT_EXIST, "Node with name:["
					+ nodeName + "] doesn't exist");
		}

		// Resolve algorithm name
		res = STAFUtil.resolveRequestVar(
				parsedRequest.optionValue("algorithm"),
				service.getStafHandle(), reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;

		String algorithm = res.result;

		// Resolve algorithm type
		res = STAFUtil.resolveRequestVar(parsedRequest
				.optionValue("algorithmtype"), service.getStafHandle(),
				reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;

		AlgorithmType algorithmType = AlgorithmType.valueOf(res.result
				.toUpperCase());

		// Resolve data file name
		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("data"),
				service.getStafHandle(), reqInfo.requestNumber);
		String dataFilename = null;
		if (res.rc == STAFResult.Ok && !"".equals(res.result)) {
			dataFilename = res.result;
			LOG.debug("Task data file name:" + dataFilename);
		}

		// Resolve arguments
		res = STAFUtil.resolveRequestVar(
				parsedRequest.optionValue("arguments"),
				service.getStafHandle(), reqInfo.requestNumber);
		String arguments = null;
		if (res.rc == STAFResult.Ok && !"".equals(res.result)) {
			arguments = res.result;
			LOG.debug("Task Arguments:" + arguments);
		}

		// Create atomic task instance and add it to the job
		AtomicTask task = new AtomicTask(taskName, node, algorithm,
				algorithmType, dataFilename, arguments);

		if (!job.addTask(task)) {
			LOG.error("Failed to add task:" + task);
			return new STAFResult(ADD_TASK_FAILED, "Failed to add task:" + task);
		}

		return new STAFResult(STAFResult.Ok);
	}

	@Override
	protected String helpString() {
		return getRequestName()
				+ " <Task name> JOBNAME <Job name> NODENAME <Node name> "
				+ "ALGORITHM <Algorithm in the form of JAR/Command> "
				+ "ALGORITHMTYPE <COMMAND | JAR> "
				+ "[DATA <Input Data for algorithm>] "
				+ "[ARGUMENTS <Arguments for algorithm>]";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddTaskRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("JOBNAME", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("NODENAME", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("ALGORITHM", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("ALGORITHMTYPE", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("DATA", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("ARGUMENTS", 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOptionNeed(getRequestName(), "JOBNAME");
		parser.addOptionNeed(getRequestName(), "NODENAME");
		parser.addOptionNeed(getRequestName(), "ALGORITHM");
		parser.addOptionNeed(getRequestName(), "ALGORITHMTYPE");
		LOG.debug("Initialized AddTaskRequest Parser Successfully");

	}

}
