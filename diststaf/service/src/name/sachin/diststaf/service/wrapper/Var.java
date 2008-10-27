package name.sachin.diststaf.service.wrapper;

import static name.sachin.diststaf.service.DistStafConstants.*;
import name.sachin.diststaf.exception.DistStafException;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public class Var extends StafService {

	private static Logger LOG = Logger.getLogger(Var.class);

	public Var() {
		super();
	}

	public Var(String stafHost, STAFHandle stafHandle) {
		super(stafHost, stafHandle);
	}

	public Var(STAFHandle stafHandle) {
		super(stafHandle);
	}

	public String getVar(VarPoolType poolType, String varName)
			throws DistStafException {
		String req = "get";
		if (poolType != null) {
			req += " " + poolType;
		}
		req += " var " + varName;
		try {
			LOG.info(this + " - Sending request:" + req);
			return stafHandle.submit(stafHost, VAR_SRV_NAME, req);
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	public String getSystemVar(String varName) {
		return getVar(VarPoolType.SYSTEM, varName);
	}

	public void setVar(VarPoolType poolType, String varName, String varValue)
			throws DistStafException {
		String req = "set";
		if (poolType != null) {
			req += " " + poolType;
		}
		req += " var " + varName + "=" + varValue;
		try {
			LOG.info(this + " - Sending request:" + req);
			stafHandle.submit(stafHost, VAR_SRV_NAME, req);
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	public void deleteVar(VarPoolType poolType, String varName)
			throws DistStafException {
		String req = "delete";
		if (poolType != null) {
			req += " " + poolType;
		}
		req += " var " + varName;
		try {
			LOG.info(this + " - Sending request:" + req);
			stafHandle.submit(stafHost, VAR_SRV_NAME, req);
		} catch (STAFException se) {
			throw new DistStafException(se);
		}
	}

	@Override
	protected String getServiceName() {
		return VAR_SRV_NAME;
	}

}
