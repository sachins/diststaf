package name.sachin.diststaf.obj;

import static name.sachin.diststaf.service.DistStafConstants.*;

import java.io.File;

import org.apache.log4j.Logger;

import name.sachin.diststaf.service.DistStafConstants.ProgramType;
import name.sachin.diststaf.service.DistStafConstants.NodeType;
import name.sachin.diststaf.service.wrapper.FileSystem;
import name.sachin.diststaf.service.wrapper.Process;
import name.sachin.diststaf.util.TaskMonitor;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class AtomicTask {

	private static final Logger LOG = Logger.getLogger(AtomicTask.class);

	private Long id;

	private String name;

	private Node node;

	private String program;

	private ProgramType programType;

	private String dataFilename; // Optional

	private String arguments; // Optional

	private TaskStatus status;

	private ResultStatus resultStatus;

	private long startTime;

	private long endTime;

	public AtomicTask(String name, Node node, String program,
			ProgramType programType, String dataFilename, String arguments) {
		this.name = name;
		this.program = program;
		this.programType = programType;
		this.dataFilename = dataFilename;
		this.arguments = arguments;
		this.node = node;
	}

	@Override
	public String toString() {
		return "[AtomicTask name:" + name + ",node:" + node
				+ ",program:" + program + ",programType:" + programType
				+ ",dataFilename:" + dataFilename + ",arguments:" + arguments
				+ ",status:" + status + ",resultStatus:" + resultStatus + "]";
	}

	public STAFResult execute(RequestInfo reqInfo, STAFHandle handle,
			String jobName) {
		String result = "";
		String dirName = node.getName() + "-" + jobName + "-" + name;
		String workDir = "{STAF/Env/TEMP}{STAF/Config/Sep/File}" + dirName;
		FileSystem fsLocal = new FileSystem(handle);

		Process procNode = new Process(node.getName(), handle);
		FileSystem fsNode = new FileSystem(node.getName(), handle);
		this.startTime = System.currentTimeMillis();
		if (NodeType.MACHINE == node.getType()) {
			LOG.info("Program is received for node: " + node);

			if (ProgramType.JAR.compareTo(programType) == 0) {
				File f = new File(program);
				File data = null;
				if (dataFilename != null)
					data = new File(dataFilename);
				LOG.info("Program File info:" + f.getAbsolutePath());
				if (fsNode.dirExists(workDir)) {
					try {
						fsNode.deleteEntry(workDir, null, true, true);
					} catch (STAFException e) {
						LOG.error("STAFException received", e);
						return new STAFResult(e.rc, e.getLocalizedMessage());
					}
				}
				try {
					fsNode.createDirectory(workDir, true, true);
				} catch (STAFException e1) {
					LOG.error("STAFException received", e1);
					return new STAFResult(e1.rc, e1.getLocalizedMessage());
				}
				if (f.isFile()) {
					try {
						fsLocal
								.copyFileToMachineToDirectory(f
										.getAbsolutePath(), node.getName(),
										workDir);
						if (data != null)
							fsLocal.copyFileToMachineToDirectory(data
									.getAbsolutePath(), node.getName(),
									workDir);
					} catch (STAFException e) {
						LOG.error("STAFException received", e);
						return new STAFResult(e.rc, e.getLocalizedMessage());
					}
					try {
						result = procNode.startInBackground("java -jar "
								+ f.getName()
								+ (arguments == null ? "" : " " + arguments),
								workDir + "{STAF/Config/Sep/File}stdout.txt",
								workDir + "{STAF/Config/Sep/File}stderr.txt",
								workDir);
					} catch (STAFException e) {
						LOG.error("STAFException", e);
						return new STAFResult(e.rc, e.getLocalizedMessage());
					}
				} else {
					return new STAFResult(PROGRAM_NOT_FILE,
							"Program is not a file for task:" + this);
				}
			} else {
				try {
					result = procNode.startInBackground(program
							+ (arguments == null ? "" : " " + arguments),
							workDir + "{STAF/Config/Sep/File}stdout.txt",
							workDir + "{STAF/Config/Sep/File}stderr.txt",
							workDir);
				} catch (STAFException e) {
					LOG.error("STAFException", e);
					return new STAFResult(e.rc, e.getLocalizedMessage());
				}
			}
		}
		this.status = TaskStatus.RUNNING;
		this.resultStatus = ResultStatus.NOTRECEIVED;
		new TaskMonitor(this, result, dirName, procNode, fsNode)
				.start();
		return new STAFResult(STAFResult.Ok, result);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public ProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(ProgramType programType) {
		this.programType = programType;
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

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

}
