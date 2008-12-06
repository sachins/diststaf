package name.sachin.diststaf.util;

import org.apache.log4j.Logger;

import name.sachin.diststaf.obj.AtomicTask;
import name.sachin.diststaf.service.DistStafService;
import name.sachin.diststaf.service.DistStafConstants.ResultStatus;
import name.sachin.diststaf.service.DistStafConstants.TaskStatus;
import name.sachin.diststaf.service.wrapper.FileSystem;
import name.sachin.diststaf.service.wrapper.Process;

public class TaskMonitor extends Thread {

	private static final Logger LOG = Logger.getLogger(TaskMonitor.class);

	private AtomicTask task;
	private String handleId;
	private Process procSrv;
	private FileSystem fsSrv;
	private String dirName;

	public TaskMonitor(AtomicTask task, String handleId, String dirName,
			Process procSrv, FileSystem fsSrv) {
		this.task = task;
		this.handleId = handleId;
		this.procSrv = procSrv;
		this.fsSrv = fsSrv;
		this.dirName = dirName;
	}

	@Override
	public void run() {
		while (true) {
			if (procSrv.isComplete(handleId)) {
				LOG.info("Completed Task: " + task);
				task.setStatus(TaskStatus.DONE);
				String workDir = "{STAF/Env/TEMP}{STAF/Config/Sep/File}"
						+ dirName;
				LOG.info("Copying directory:" + workDir);
				long startTime = System.currentTimeMillis();
				LOG.info("START TIME Copying results for " + task.getName()
						+ ":" + startTime);
				fsSrv.copyDirectory(workDir, DistStafService.SERVICE_DATA_DIR
						+ "{STAF/Config/Sep/File}" + dirName, null, true);
				long endTime = System.currentTimeMillis();
				LOG.info("END TIME Copyting results for " + task.getName()
						+ ":" + endTime);
				LOG
						.info("Total time taken in milliseconds for copying results for "
								+ task.getName() + ":" + (endTime - startTime));
				task.setResultStatus(ResultStatus.RECEIVED);
				return;
			} else if (DistStafService.STOP_THREADS) {
				return;
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOG.warn("Thread sleep interrupted.", e);
				}
			}
		}
	}
}
