package name.sachin.diststaf.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import name.sachin.diststaf.service.DistStafConstants.JobStatus;
import name.sachin.diststaf.service.DistStafConstants.ResourceType;
import name.sachin.diststaf.service.wrapper.FileSystem;
import name.sachin.diststaf.service.wrapper.Process;
import name.sachin.diststaf.service.wrapper.Var;
import name.sachin.diststaf.service.wrapper.FileSystem.ChildrenOption;
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
	public List execute(RequestInfo reqInfo) throws STAFException {
		List resultList = new ArrayList();
		Var var = new Var();
		String thisHost = var.getSystemVar("STAF/Config/Machine");
		String workDir = "{STAF/Env/TEMP}{STAF/Config/Sep/File}" + thisHost
				+ "-" + name;
		FileSystem fs = new FileSystem();
		for (Resource r : resources) {
			if (ResourceType.MACHINE == r.getType()) {
				LOG.info("Algorithm is received for resource: " + r);
				STAFHandle handle = new STAFHandle(r.getName());
				Process procResource = new Process(r.getName(), handle);
				FileSystem fsResource = new FileSystem(r.getName(), handle);
				if (AlgorithmType.JAR.compareTo(algorithmType) == 0) {
					File f = new File(algorithm);
					File data = null;
					if (dataFilename != null)
						data = new File(dataFilename);
					LOG.info("Algorithm File info:" + f.getAbsolutePath());
					if (fsResource.dirExists(workDir)) {
						fsResource.deleteEntry(workDir, null, true, true);
					}
					fsResource.createDirectory(workDir, true, true);
					if (f.isFile()) {
						fs.copyFileToMachineToDirectory(f.getAbsolutePath(), r
								.getName(), workDir);
						if (data != null)
							fs.copyFileToMachineToDirectory(data
									.getAbsolutePath(), r.getName(), workDir);
						String result = procResource.startInBackground(
								"java -jar "
										+ f.getName()
										+ (data == null ? "" : " "
												+ data.getName()), workDir
										+ "{STAF/Config/Sep/File}stdout.txt",
								workDir + "{STAF/Config/Sep/File}stderr.txt",
								workDir);
						resultList.add(result);
					} else {
						throw new STAFException(ALGORITHM_NOT_FILE,
								"Algorithm argument is not a file / doesn't exist.");
					}
				} else {
					Map resultMap = procResource.start(this.getAlgorithm());
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
