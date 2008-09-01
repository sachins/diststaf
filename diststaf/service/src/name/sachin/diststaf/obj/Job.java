package name.sachin.diststaf.obj;

import java.util.ArrayList;
import java.util.List;
import name.sachin.diststaf.service.wrapper.DistStafConstants.JobStatus;

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
	
	public void execute() {
		for(Resource r : resources) {
			//TODO: distribute job equally among resources
			//with provided input
		}
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
