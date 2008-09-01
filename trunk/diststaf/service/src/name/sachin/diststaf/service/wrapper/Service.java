package name.sachin.diststaf.service.wrapper;

import java.io.File;
import java.util.List;
import java.util.Map;

import name.sachin.diststaf.exception.DistStafException;

import org.apache.log4j.Logger;

import static name.sachin.diststaf.service.wrapper.DistStafConstants.*;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;

public class Service extends StafService {

	private static Logger LOG = Logger.getLogger(Service.class);

	public Service(String stafHost, STAFHandle stafHandle) {
		super(stafHost, stafHandle);
	}

	public Service() throws DistStafException {
		super();
	}

	public Service(STAFHandle stafHandle) {
		super(stafHandle);
	}

	@SuppressWarnings("unchecked")
	public List<Map> list() throws DistStafException {
		try {
			LOG.info(this + " - Sending request: list");
			String result = stafHandle.submit(stafHost, SERVICE_SRV_NAME,
					"list");
			STAFMarshallingContext mc = STAFMarshallingContext
					.unmarshall(result);
			return (List<Map>) mc.getRootObject();
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	public void add(String srvName, ServiceLibraryType library, File execute) {
		try {
			LOG.info(this + " - Sending request: add");
			String req = "add service " + srvName;
			if (library != null)
				req += " " + library;
			if (execute != null)
				req += " " + execute.getAbsolutePath();
			String result = stafHandle.submit(stafHost, SERVICE_SRV_NAME, req);
			if (result.length() > 0) {
				throw new DistStafException(result);
			}
			LOG.info(this + " - Successfully Added service " + srvName);
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	public void remove(String srvName) {
		try {
			LOG.info(this + " - Sending request: remove");
			String req = "remove service " + srvName;
			String result = stafHandle.submit(stafHost, SERVICE_SRV_NAME, req);
			if (result.length() > 0) {
				throw new DistStafException(result);
			}
			LOG.info(this + " - Successfully Removed service " + srvName);
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	@Override
	protected String getServiceName() {
		return SERVICE_SRV_NAME;
	}
}
