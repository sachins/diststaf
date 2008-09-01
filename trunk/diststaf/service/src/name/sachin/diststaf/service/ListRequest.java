package name.sachin.diststaf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.sachin.diststaf.obj.Resource;
import name.sachin.diststaf.obj.Job;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFMapClassDefinition;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class ListRequest extends AbstractStafRequest {

	private static final Logger LOG = Logger.getLogger(ListRequest.class);

	private static final int LIST_TRUST_LEVEL = 1;

	private static STAFMapClassDefinition listJobMapClass;

	static {
		listJobMapClass = new STAFMapClassDefinition(
				"STAF/Service/DistStaf/ListJob");
		listJobMapClass.addKey("name", "Name");
		listJobMapClass.addKey("algorithm", "Algorithm");
		listJobMapClass.addKey("status", "Status");
	}

	private static STAFMapClassDefinition listResourceMapClass;

	static {
		listResourceMapClass = new STAFMapClassDefinition(
				"STAF/Service/DistStaf/ListResource");
		listResourceMapClass.addKey("name", "Name");
		listResourceMapClass.addKey("type", "Type");
	}

	public ListRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@SuppressWarnings("unchecked")
	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(LIST_TRUST_LEVEL,
				service.getServiceName(), getRequestName(),
				service.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;
		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);
		int jobsOption = parsedRequest.optionTimes("jobs");
		int resourcesOption = parsedRequest.optionTimes("resources");

		STAFMarshallingContext mc = new STAFMarshallingContext();

		// Create an empty result list to contain the result

		List resultList = new ArrayList();

		// Add job entries to the result list

		if (jobsOption > 0) {
			return processListJob(mc, resultList);
		} else if (resourcesOption > 0) {
			return processListResource(mc, resultList);
		} else {
			return new STAFResult(STAFResult.InvalidRequestString);
		}

	}

	@SuppressWarnings("unchecked")
	private STAFResult processListResource(STAFMarshallingContext mc,
			List resultList) {
		mc.setMapClassDefinition(listResourceMapClass);
		for (Resource eachResource : service.getResourceList()) {
			Map resultMap = listResourceMapClass.createInstance();
			resultMap.put("name", eachResource.getName());
			resultMap.put("type", eachResource.getType());
			resultList.add(resultMap);
		}
		mc.setRootObject(resultList);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	@SuppressWarnings("unchecked")
	private STAFResult processListJob(STAFMarshallingContext mc, List resultList) {
		mc.setMapClassDefinition(listJobMapClass);
		for (Job eachJob : service.getJobList()) {
			Map resultMap = listJobMapClass.createInstance();
			resultMap.put("name", eachJob.getName());
			resultMap.put("algorithm", eachJob.getAlgorithm());
			resultMap.put("status", eachJob.getStatus());
			resultList.add(resultMap);
		}
		mc.setRootObject(resultList);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing AddJobRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption("JOBS", 1, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption("RESOURCES", 1, STAFCommandParser.VALUENOTALLOWED);
		LOG.debug("Initialized AddJobRequest Parser Successfully");
	}

	@Override
	protected String getRequestName() {
		return "LIST";
	}

	@Override
	protected String helpString() {
		return "LIST JOBS | RESOURCES";
	}

}
