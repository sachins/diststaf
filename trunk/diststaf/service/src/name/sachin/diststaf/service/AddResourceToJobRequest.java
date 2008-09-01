package name.sachin.diststaf.service;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class AddResourceToJobRequest extends AbstractStafRequest {

	public AddResourceToJobRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@Override
	protected String getRequestName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String helpString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initParser() {
		// TODO Auto-generated method stub

	}

}
