package name.sachin.diststaf.service;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public abstract class AbstractStafRequest {
	
	protected STAFCommandParser parser;
	
	protected DistStafService service;
	
	protected abstract void initParser();
	
	public abstract STAFResult handle(RequestInfo reqInfo);
	
	protected abstract String helpString();
	
	protected abstract String getRequestName();
	
}
