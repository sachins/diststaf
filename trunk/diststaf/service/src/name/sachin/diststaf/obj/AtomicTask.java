package name.sachin.diststaf.obj;

import static name.sachin.diststaf.service.DistStafConstants.*;

import java.io.File;

import org.apache.log4j.Logger;

import name.sachin.diststaf.service.DistStafConstants.AlgorithmType;
import name.sachin.diststaf.service.DistStafConstants.ResourceType;
import name.sachin.diststaf.service.wrapper.FileSystem;
import name.sachin.diststaf.service.wrapper.Process;
import name.sachin.diststaf.util.TaskMonitor;

import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class AtomicTask {

	private static final Logger LOG = Logger.getLogger(AtomicTask.class);

	private Long id;

	private String name;

	private Resource resource;

	private String algorithm;

	private AlgorithmType algorithmType;

	private String dataFilename; // Optional

	private String arguments; // Optional

	private TaskStatus status;

	private ResultStatus resultStatus;
	
	public AtomicTask(String name, Resource resource, String algorithm,
			AlgorithmType algorithmType, String dataFilename, String arguments) {
		this.name = name;
		this.algorithm = algorithm;
		this.algorithmType = algorithmType;
		this.dataFilename = dataFilename;
		this.arguments = arguments;
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "[AtomicTask name:" + name + ",resource:" + resource
				+ ",algorithm:" + algorithm + ",algorithmType:" + algorithmType
				+ ",dataFilename:" + dataFilename + ",arguments:" + arguments
				+ ",status:" + status + ",resultStatus:" + resultStatus + "]";
	}

	public STAFResult execute(RequestInfo reqInfo, STAFHandle handle,
			String jobName) {
		String result = "";
		String dirName = resource.getName() + "-" + jobName + "-" + name;
		String workDir = "{STAF/Env/TEMP}{STAF/Config/Sep/File}" + dirName;
		FileSystem fsLocal = new FileSystem(handle);

		Process procResource = new Process(resource.getName(), handle);
		FileSystem fsResource = new FileSystem(resource.getName(), handle);

		if (ResourceType.MACHINE == resource.getType()) {
			LOG.info("Algorithm is received for resource: " + resource);

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
					fsLocal.copyFileToMachineToDirectory(f.getAbsolutePath(),
							resource.getName(), workDir);
					if (data != null)
						fsLocal
								.copyFileToMachineToDirectory(data
										.getAbsolutePath(), resource.getName(),
										workDir);
					result = procResource.startInBackground("java -jar "
							+ f.getName()
							+ (arguments == null ? "" : " " + arguments),
							workDir + "{STAF/Config/Sep/File}stdout.txt",
							workDir + "{STAF/Config/Sep/File}stderr.txt",
							workDir);
				} else {
					return new STAFResult(ALGORITHM_NOT_FILE,
							"Algorithm is not a file for task:" + this);
				}
			} else {
				result = procResource.startInBackground(algorithm
						+ (arguments == null ? "" : " " + arguments), workDir
						+ "{STAF/Config/Sep/File}stdout.txt", workDir
						+ "{STAF/Config/Sep/File}stderr.txt", workDir);
			}
		}
		this.status = TaskStatus.RUNNING;
		this.resultStatus = ResultStatus.NOTRECEIVED;
		new TaskMonitor(this, result, dirName, procResource, fsResource)
				.start();
		return new STAFResult(STAFResult.Ok, result);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}

	public String getDataFilename() {
		return dataFilename;
	}

	public void setDataFilename(String dataFilename) {
		this.dataFilename = dataFilename;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

}
