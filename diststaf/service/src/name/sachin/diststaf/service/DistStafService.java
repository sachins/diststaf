package name.sachin.diststaf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import name.sachin.diststaf.obj.Node;
import name.sachin.diststaf.obj.Job;
import name.sachin.diststaf.service.DistStafConstants.NodeType;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;

/**
 * @author Sachin
 * 
 */
public class DistStafService implements STAFServiceInterfaceLevel30 {

	private static final Logger LOG = Logger.getLogger(DistStafService.class);

	private String serviceName;

	private STAFHandle stafHandle;

	private String localMachineName = "";

	public static String LINE_SEP;

	public static String FILE_SEP;
	
	public static String SERVICE_DATA_DIR;
	
	public static boolean STOP_THREADS = false;

	private List<Job> jobs;

	private List<Node> nodes;

	private List<AbstractStafRequest> requestHandlers;

	public String getLocalMachineName() {
		return localMachineName;
	}

	public void setLocalMachineName(String localMachineName) {
		this.localMachineName = localMachineName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public STAFHandle getStafHandle() {
		return stafHandle;
	}

	public void setStafHandle(STAFHandle stafHandle) {
		this.stafHandle = stafHandle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.staf.service.STAFServiceInterfaceLevel30#acceptRequest(com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo)
	 */
	public STAFResult acceptRequest(RequestInfo reqInfo) {
		String lowerRequest = reqInfo.request.toLowerCase();
		StringTokenizer requestTokenizer = new StringTokenizer(lowerRequest);
		String request = requestTokenizer.nextToken();

		// Call the appropriate method to handle the command

		if ("help".equals(request))
			return handleHelp(reqInfo);

		for (AbstractStafRequest eachReqHandler : requestHandlers) {
			if (eachReqHandler.getRequestName().equalsIgnoreCase(request)) {
				return eachReqHandler.handle(reqInfo);
			}
		}
		return new STAFResult(STAFResult.InvalidRequestString,
				"Unknown DistStaf Request: " + lowerRequest);

	}

	private STAFResult handleHelp(RequestInfo reqInfo) {
		String help = "DISTSTAF Service Help" + LINE_SEP;
		for (AbstractStafRequest eachReqHandler : requestHandlers) {
			help += eachReqHandler.helpString() + LINE_SEP;
		}
		help += "HELP" + LINE_SEP;
		return new STAFResult(STAFResult.Ok, help);
	}

	protected boolean addJob(Job job) {
		synchronized (jobs) {
			return jobs.add(job);
		}
	}

	protected boolean addNode(Node node) {
		synchronized (nodes) {
			return nodes.add(node);
		}
	}

	protected List<Job> getJobList() {
		synchronized (jobs) {
			return jobs;
		}
	}

	protected List<Node> getNodeList() {
		synchronized (nodes) {
			return nodes;
		}
	}

	protected boolean jobNameExists(String jobName) {
		return findJob(jobName) != null;
	}

	protected boolean nodeNameExists(String nodeName, NodeType type) {
		return findNode(nodeName, type) != null;
	}

	protected Node findNode(String nodeName, NodeType type) {
		for (Node eachNode : nodes) {
			if (eachNode.getName().equalsIgnoreCase(nodeName)
					&& eachNode.getType() == type) {
				return eachNode;
			}
		}
		return null;
	}

	protected Node findNode(String nodeName) {
		for (Node eachNode : nodes) {
			if (eachNode.getName().equalsIgnoreCase(nodeName)) {
				return eachNode;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.staf.service.STAFServiceInterfaceLevel30#init(com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo)
	 */
	public STAFResult init(InitInfo initInfo) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Trying to register DistStaf service");
		}
		try {
			serviceName = initInfo.name;
			stafHandle = new STAFHandle("STAF/Service/" + serviceName);
		} catch (STAFException e) {
			LOG.error("Failed to register service", e);
			return new STAFResult(STAFResult.STAFRegistrationError, e
					.toString());
		}

		jobs = new ArrayList<Job>();
		nodes = new ArrayList<Node>();

		initRequestHandlers();
		STAFResult res = new STAFResult();

		// Resolve the line separator variable for the local machine

		res = STAFUtil.resolveInitVar("{STAF/Config/Sep/Line}", stafHandle);

		if (res.rc != STAFResult.Ok)
			return res;

		LINE_SEP = res.result;

		res = STAFUtil.resolveInitVar("{STAF/Config/Sep/File}", stafHandle);

		if (res.rc != STAFResult.Ok)
			return res;

		FILE_SEP = res.result;

		// Resolve the machine name variable for the local machine

		res = STAFUtil.resolveInitVar("{STAF/Config/Machine}", stafHandle);

		if (res.rc != STAFResult.Ok)
			return res;

		localMachineName = res.result;

		SERVICE_DATA_DIR = initInfo.writeLocation + FILE_SEP + "service" + FILE_SEP
				+ serviceName.toLowerCase();

		File dir = new File(SERVICE_DATA_DIR);

		if (!dir.exists()) {
			dir.mkdirs();
		}

		return new STAFResult(STAFResult.Ok);
	}

	private void initRequestHandlers() {
		requestHandlers = new ArrayList<AbstractStafRequest>();
		requestHandlers.add(new AddJobRequest(this));
		requestHandlers.add(new AddNodeRequest(this));
		requestHandlers.add(new AddTaskRequest(this));
		requestHandlers.add(new ListRequest(this));
		requestHandlers.add(new DeleteJobRequest(this));
		requestHandlers.add(new DeleteNodeRequest(this));
		requestHandlers.add(new ExecuteJobRequest(this));
		requestHandlers.add(new JobStatsRequest(this));
	}

	protected Job findJob(String jobName) {
		for (Job eachJob : jobs) {
			if (eachJob.getName().equalsIgnoreCase(jobName)) {
				return eachJob;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.staf.service.STAFServiceInterfaceLevel30#term()
	 */
	public STAFResult term() {
		try {
			STOP_THREADS = true;
			stafHandle.unRegister();
		} catch (STAFException ex) {
			return new STAFResult(STAFResult.STAFRegistrationError, ex
					.toString());
		}
		return new STAFResult(STAFResult.Ok);
	}

	protected boolean removeJob(String jobName) {
		return getJobList().remove(findJob(jobName));
	}

	protected boolean removeNode(String nodeName) {
		
		return getNodeList().remove(findNode(nodeName));
	}

}
