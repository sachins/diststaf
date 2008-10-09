package name.sachin.diststaf.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

import name.sachin.diststaf.service.DistStafConstants.JobStatus;
import name.sachin.diststaf.service.DistStafConstants.ResourceType;
import name.sachin.diststaf.service.wrapper.FileSystem;
import name.sachin.diststaf.service.wrapper.Process;
import static name.sachin.diststaf.service.DistStafConstants.*;

public class Job {
	private static final Logger LOG = Logger.getLogger(Job.class);

	private String name;

	private String algorithm;

	private AlgorithmType algorithmType;

	private List<Resource> resources;

	private JobStatus status;

	private String result;

	private String dataFilename;

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

	public Job(String name, String algorithm, AlgorithmType algorithmType,
			String dataFilename) {
		super();
		this.name = name;
		this.algorithm = algorithm;
		this.algorithmType = algorithmType;
		this.dataFilename = dataFilename;
		this.resources = new ArrayList<Resource>();
	}

	public Job(String name, String algorithm, AlgorithmType algorithmType) {
		this(name, algorithm, algorithmType, null);
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
		for (Resource r : resources) {
			if (ResourceType.MACHINE == r.getType()) {
				LOG.info("Algorithm is received for resource: " + r);
				STAFHandle handle = new STAFHandle(r.getName());
				Process proc = new Process(r.getName(), handle);
				if (AlgorithmType.JAR.compareTo(algorithmType) == 0) {
					FileSystem fs = new FileSystem();
					File f = new File(algorithm);
					File data = null;
					if (dataFilename != null)
						data = new File(dataFilename);

					LOG.info("File info:" + f.getAbsolutePath());
					if (f.isFile()) {
						fs.copyFileToMachineToDirectory(f.getAbsolutePath(), r
								.getName(), "{STAF/Env/TEMP}");
						if (data != null)
							fs.copyFileToMachineToDirectory(data
									.getAbsolutePath(), r.getName(),
									"{STAF/Env/TEMP}");
						Map resultMap = proc
								.start("java -jar {STAF/Env/TEMP}{STAF/Config/Sep/File}"
										+ f.getName()
										+ (data == null ? ""
												: " {STAF/Env/TEMP}{STAF/Config/Sep/File}"
														+ data.getName()));
						resultList.add(resultMap);
					} else {
						throw new STAFException(ALGORITHM_NOT_FILE,
								"Algorithm argument is not a file / doesn't exist.");
					}
				} else {
					Map resultMap = proc.start(this.getAlgorithm());
					resultList.add(resultMap);
				}

			}
		}
		return resultList;
	}

	public String toString() {
		return "[Job name:"
				+ name
				+ ",algorithm:"
				+ algorithm
				+ ",algorithmType:"
				+ algorithmType
				+ (dataFilename == null ? "]" : ",dataFilename:" + dataFilename
						+ "]");
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

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

}
