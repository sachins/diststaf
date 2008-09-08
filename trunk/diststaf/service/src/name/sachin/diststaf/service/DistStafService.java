package name.sachin.diststaf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import name.sachin.diststaf.obj.Resource;
import name.sachin.diststaf.obj.Job;
import name.sachin.diststaf.service.wrapper.DistStafConstants.ResourceType;

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

	private String lineSep;

	private List<Job> jobs;

	private List<Resource> resources;

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
		String help = "DISTSTAF Service Help" + lineSep;
		for (AbstractStafRequest eachReqHandler : requestHandlers) {
			help += eachReqHandler.helpString() + lineSep;
		}
		help += "HELP" + lineSep;
		return new STAFResult(STAFResult.Ok, help);
	}

	protected boolean addJob(Job job) {
		synchronized (jobs) {
			return jobs.add(job);
		}
	}
	
	protected boolean addResource(Resource resource) {
		synchronized (resources) {
			return resources.add(resource);
		}
	}

	protected List<Job> getJobList() {
		synchronized (jobs) {
			return jobs;
		}
	}

	protected List<Resource> getResourceList() {
		synchronized (resources) {
			return resources;
		}
	}

	protected boolean jobNameExists(String jobName) {
		return findJob(jobName) != null;
	}
	
	protected boolean resourceNameExists(String resName, ResourceType type) {
		return findResource(resName, type) != null;
	}

	protected Resource findResource(String resName, ResourceType type) {
		for (Resource eachResource : resources) {
			if (eachResource.getName().equalsIgnoreCase(resName) && eachResource.getType() == type) {
				return eachResource;
			}
		}
		return null;
	}
	
	protected Resource findResource(String resName) {
		for (Resource eachResource : resources) {
			if (eachResource.getName().equalsIgnoreCase(resName)) {
				return eachResource;
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
		resources = new ArrayList<Resource>();

		initRequestHandlers();
		STAFResult res = new STAFResult();

		// Resolve the line separator variable for the local machine

		res = STAFUtil.resolveInitVar("{STAF/Config/Sep/Line}", stafHandle);

		if (res.rc != STAFResult.Ok)
			return res;

		lineSep = res.result;

		// Resolve the machine name variable for the local machine

		res = STAFUtil.resolveInitVar("{STAF/Config/Machine}", stafHandle);

		if (res.rc != STAFResult.Ok)
			return res;

		localMachineName = res.result;

		return new STAFResult(STAFResult.Ok);
	}

	private void initRequestHandlers() {
		requestHandlers = new ArrayList<AbstractStafRequest>();
		requestHandlers.add(new AddJobRequest(this));
		requestHandlers.add(new AddResourceRequest(this));
		requestHandlers.add(new ListRequest(this));
		requestHandlers.add(new DeleteJobRequest(this));
		requestHandlers.add(new AssignResourceToJobRequest(this));
		requestHandlers.add(new ExecuteJobRequest(this));
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
		// TODO Auto-generated method stub
		return new STAFResult();
	}

	protected boolean removeJob(String jobName) {
		return getJobList().remove(findJob(jobName));
	}

}
