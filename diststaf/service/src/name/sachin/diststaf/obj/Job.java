package name.sachin.diststaf.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

import name.sachin.diststaf.service.wrapper.Process;
import name.sachin.diststaf.service.wrapper.DistStafConstants.JobStatus;
import name.sachin.diststaf.service.wrapper.DistStafConstants.ResourceType;

public class Job {
	private String name;

	private String algorithm;

	private List<Resource> resources;
	
	private JobStatus status;
	
	private String result;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Job(String name, String algorithm) {
		super();
		this.name = name;
		this.algorithm = algorithm;
		this.resources = new ArrayList<Resource>();
	}

	public List<Resource> getResources() {
		return this.resources;
	}
	
	public Resource findResource(String resName) {
		for (Resource eachResource : resources) {
			if (eachResource.getName().equalsIgnoreCase(resName)) {
				return eachResource;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List execute() throws STAFException {
		List resultList = new ArrayList();
		for(Resource r : resources) {
			if(ResourceType.MACHINE == r.getType()) {
				STAFHandle handle = new STAFHandle(r.getName());
				Process proc = new Process(r.getName(), handle);
				Map resultMap = proc.start(this.getAlgorithm());
				resultList.add(resultMap);
			}
		}
		return resultList;
	}

	public String toString() {
		return "[Job name:" + name + ",algorithm:" + algorithm + "]";
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

}
