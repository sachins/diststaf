package name.sachin.diststaf.service;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

import name.sachin.diststaf.obj.Resource;
import name.sachin.diststaf.service.wrapper.DistStafConstants.ResourceType;

public class AddResourceRequest extends AbstractStafRequest {
	
	private static final int ADDRESOURCE_TRUST_LEVEL = 3;
	
	private static final Logger LOG = Logger.getLogger(AddResourceRequest.class);

	private static final int RESOURCE_EXISTS = 4010;

	private static final int ADD_RESOURCE_FAILED = 4011;
	
	public AddResourceRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}
	

	@Override
	protected String getRequestName() {
		return "ADDRESOURCE";
	}

	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(ADDRESOURCE_TRUST_LEVEL,
				service.getServiceName(), getRequestName(), service
						.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;
		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);

		STAFResult res;

		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue(getRequestName()),
				service.getStafHandle(), reqInfo.requestNumber);

		if (res.rc != STAFResult.Ok)
			return res;
		
		String resName = res.result;
		
		res = STAFUtil.resolveRequestVar(parsedRequest.optionValue("type"),
				service.getStafHandle(), reqInfo.requestNumber);
		ResourceType type = ResourceType.valueOf(res.result.toUpperCase());
		
		if(service.resourceNameExists(resName, type)) {
			return new STAFResult(RESOURCE_EXISTS, "Resource with name:[" + resName
					+ "] and type:[" + type + "] already exists");
		}
		if (res.rc != STAFResult.Ok)
			return res;
		
		Resource newResource = new Resource(resName, type);
		if (!service.addResource(newResource))
			return new STAFResult(ADD_RESOURCE_FAILED, "Failed to add resource:["
					+ resName + "]");
		return new STAFResult(STAFResult.Ok);
	}

	@Override
	protected String helpString() {
		return getRequestName()
		+ " <Resource name> TYPE <Types MACHINE>";
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddResourceRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption("TYPE", 1, STAFCommandParser.VALUEREQUIRED);
		LOG.debug("Initialized AddResourceRequest Parser Successfully");
	}

}
