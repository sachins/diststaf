package name.sachin.diststaf.service.wrapper;

import static name.sachin.diststaf.service.DistStafConstants.STAF_LOCAL_HOST;

import org.apache.log4j.Logger;

import name.sachin.diststaf.exception.DistStafException;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public abstract class StafService {
	
	private static final Logger LOG = Logger.getLogger(StafService.class);

	protected STAFHandle stafHandle;

	protected String stafHost;

	public StafService() {
		this.stafHost = STAF_LOCAL_HOST;
		try {
			this.stafHandle = new STAFHandle(getServiceName());
		} catch (STAFException se) {
			LOG.error("STAFException Received", se);
			throw new DistStafException(se);
		}
	}

	public StafService(String stafHost, STAFHandle stafHandle) {
		this.stafHost = stafHost;
		this.stafHandle = stafHandle;
	}

	public StafService(STAFHandle stafHandle) {
		this.stafHost = STAF_LOCAL_HOST;
		this.stafHandle = stafHandle;
	}

	@Override
	public String toString() {
		return getServiceName() + " for " + stafHost;
	}

	protected abstract String getServiceName();

}
