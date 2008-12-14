package name.sachin.diststaf.obj;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import static name.sachin.diststaf.service.DistStafConstants.*;

public class Job {
	private static final Logger LOG = Logger.getLogger(Job.class);

	private String name;

	private JobStatus status;

	private List<AtomicTask> tasks;
	
	private long startTime;
	
	private long endTime;

	public Job(String name) {
		this.name = name;
		this.tasks = new ArrayList<AtomicTask>();
	}

	public List<AtomicTask> getTasks() {
		return this.tasks;
	}

	public AtomicTask findTask(String taskName) {
		for (AtomicTask eachTask : tasks) {
			if (eachTask.getName().equalsIgnoreCase(taskName)) {
				return eachTask;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List execute(RequestInfo reqInfo, STAFHandle handle) {
		LOG.debug("Executing " + this);
		List resultList = new ArrayList();
		startTime = System.currentTimeMillis();
		for (AtomicTask t : tasks) {
			LOG.debug("Sending execute command to " + t);
			STAFResult response = t.execute(reqInfo, handle, name);
			if (STAFResult.Ok == response.rc) {
				LOG.debug("Received handle id " + response.result + " for "
						+ t.getName());
				resultList.add(t.getName() + "=>" + response.result);
			} else {
				LOG.error("Error executing task:" + t
						+ ". Received error code:" + response.rc);
				resultList.add(t.getName() + "=> Error:" + response.rc);
			}
		}
		return resultList;
	}
	

	public boolean addTask(AtomicTask task) {
		return tasks.add(task);
	}

	public String toString() {
		return "[Job name:" + name + ",status:" + status + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

}
