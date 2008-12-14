package name.sachin.diststaf.service.wrapper;

import static name.sachin.diststaf.service.DistStafConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import name.sachin.diststaf.exception.DistStafException;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFUtil;

public class Process extends StafService {

	private static Logger LOG = Logger.getLogger(Process.class);

	public Process(String stafHost, STAFHandle stafHandle) {
		super(stafHost, stafHandle);
	}

	public Process() throws STAFException {
		super();
	}

	public Process(STAFHandle stafHandle) {
		super(stafHandle);
	}

	@SuppressWarnings("unchecked")
	public Map start(String command) throws STAFException {
		LOG.info(this + " - Sending request: start");
		String req = "start shell command " + STAFUtil.wrapData(command);
		req += " wait stderrtostdout returnstdout";
		String result = stafHandle.submit(stafHost, getServiceName(), req);
		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(result);
		if (mc.getRootObject() instanceof String) {
			HashMap<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("result", (String) mc.getRootObject());
			return resultMap;
		}
		return (Map) mc.getRootObject();
	}

	public String startInBackground(String command, String stdoutFile,
			String stderrFile, String workDir) throws STAFException {
		LOG.info(this + " - Sending request: start");
		String req = "start shell command " + STAFUtil.wrapData(command);
		if (workDir != null) {
			req += " workdir " + STAFUtil.wrapData(workDir);
		}
		if (stdoutFile != null) {
			req += " stdout " + STAFUtil.wrapData(stdoutFile);
		}
		if (stderrFile != null) {
			req += " stderr " + STAFUtil.wrapData(stderrFile);
		}

		LOG.info(this + " - Request generated: " + req);
		String result = stafHandle.submit(stafHost, getServiceName(), req);
		return result;
	}

	// Compatible with STAF v2 and v3
	@SuppressWarnings("unchecked")
	public boolean isComplete(String handleId) throws STAFException {
		LOG.info(this + " - Checking STAF verison on " + stafHost);
		String stafVersion = stafHandle.submit(stafHost, "misc", "version");
		LOG.debug(this + " - STAF Version found:" + stafVersion);
		String processInfo = stafHandle.submit(stafHost, getServiceName(),
				"query handle " + handleId);
		LOG.debug("Process info:" + processInfo);
		if (stafVersion.startsWith("2.")) {
			String rcString = "RC        :";
			int rcIndexStart = processInfo.indexOf(rcString)
					+ rcString.length();
			LOG.debug("rcIndexStart:" + rcIndexStart);
			int rcIndexEnd = processInfo.indexOf('\n', rcIndexStart);
			LOG.debug("rcIndexEnd:" + rcIndexEnd);
			if (rcIndexStart == rcIndexEnd) {
				return false;
			}
			String rc = processInfo.substring(rcIndexStart, rcIndexEnd);
			LOG.debug("Result code got:" + rc);
			try {
				Integer.parseInt(rc.trim());
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else if (stafVersion.startsWith("3.")) {
			STAFMarshallingContext mc = STAFMarshallingContext
					.unmarshall(processInfo);
			Map<String, String> pInfo = (Map<String, String>) mc
					.getRootObject();
			if (pInfo.get("rc").equals("<None>")) {
				return false;
			} else {
				return true;
			}
		} else {
			throw new DistStafException("Unknown STAF Version");
		}
	}

	@Override
	protected String getServiceName() {
		return PROCESS_SRV_NAME;
	}

}
