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

	public Process() throws DistStafException {
		super();
	}

	public Process(STAFHandle stafHandle) {
		super(stafHandle);
	}

	@SuppressWarnings("unchecked")
	public Map start(String command) {
		try {
			LOG.info(this + " - Sending request: start");
			String req = "start shell command " + STAFUtil.wrapData(command);
			req += " wait stderrtostdout returnstdout";
			String result = stafHandle.submit(stafHost, getServiceName(), req);
			STAFMarshallingContext mc = STAFMarshallingContext
					.unmarshall(result);
			if (mc.getRootObject() instanceof String) {
				HashMap<String, String> resultMap = new HashMap<String, String>();
				resultMap.put("result", (String) mc.getRootObject());
				return resultMap;
			}
			return (Map) mc.getRootObject();
		} catch (STAFException e) {
			throw new DistStafException(e);
		}
	}

	public String startInBackground(String command, String stdoutFile,
			String stderrFile, String workDir) {
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

		try {
			LOG.info(this + " - Request generated: " + req);
			String result = stafHandle.submit(stafHost, getServiceName(), req);
			return result;
		} catch (STAFException e) {
			throw new DistStafException(e);
		}
	}

	@Override
	protected String getServiceName() {
		return PROCESS_SRV_NAME;
	}

}
