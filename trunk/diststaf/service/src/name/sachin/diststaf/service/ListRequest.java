package name.sachin.diststaf.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.sachin.diststaf.obj.Node;
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

	private static STAFMapClassDefinition listJobMapClass;

	static {
		listJobMapClass = new STAFMapClassDefinition(
				"STAF/Service/DistStaf/ListJob");
		listJobMapClass.addKey("name", "Name");
		listJobMapClass.addKey("status", "Status");
		listJobMapClass.addKey("tasks", "Tasks");
	}

	private static STAFMapClassDefinition listNodeMapClass;

	static {
		listNodeMapClass = new STAFMapClassDefinition(
				"STAF/Service/DistStaf/ListNode");
		listNodeMapClass.addKey("name", "Name");
		listNodeMapClass.addKey("type", "Type");
	}

	public ListRequest(DistStafService distStafSrv) {
		this.service = distStafSrv;
		initParser();
	}

	@SuppressWarnings("unchecked")
	@Override
	public STAFResult handle(RequestInfo reqInfo) {
		LOG.debug("Received request: " + reqInfo.request);
		STAFResult trustResult = STAFUtil.validateTrust(DistStafConstants.LIST_TRUST_LEVEL,
				service.getServiceName(), getRequestName(),
				service.getLocalMachineName(), reqInfo);
		LOG.debug("Trust result:[" + trustResult.rc + "," + trustResult.result
				+ "]");
		if (trustResult.rc != STAFResult.Ok)
			return trustResult;
		STAFCommandParseResult parsedRequest = parser.parse(reqInfo.request);
		int jobsOption = parsedRequest.optionTimes("jobs");
		int nodesOption = parsedRequest.optionTimes("nodes");

		STAFMarshallingContext mc = new STAFMarshallingContext();

		// Create an empty result list to contain the result

		List resultList = new ArrayList();

		// Add job entries to the result list

		if (jobsOption > 0) {
			return processListJob(mc, resultList);
		} else if (nodesOption > 0) {
			return processListNode(mc, resultList);
		} else {
			return new STAFResult(STAFResult.InvalidRequestString);
		}

	}

	@SuppressWarnings("unchecked")
	private STAFResult processListNode(STAFMarshallingContext mc,
			List resultList) {
		mc.setMapClassDefinition(listNodeMapClass);
		for (Node eachNode : service.getNodeList()) {
			Map resultMap = listNodeMapClass.createInstance();
			resultMap.put("name", eachNode.getName());
			resultMap.put("type", eachNode.getType());
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
			resultMap.put("status", eachJob.getStatus());
			resultMap.put("tasks", eachJob.getTasks());
			resultList.add(resultMap);
		}
		mc.setRootObject(resultList);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	@Override
	protected void initParser() {
		LOG.debug("Initializing ListRequest Parser");
		parser = new STAFCommandParser();
		parser.addOption(getRequestName(), 1, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption("JOBS", 1, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption("NODES", 1, STAFCommandParser.VALUENOTALLOWED);
		LOG.debug("Initialized ListRequest Parser Successfully");
	}

	@Override
	protected String getRequestName() {
		return "LIST";
	}

	@Override
	protected String helpString() {
		return "LIST JOBS | NODES";
	}

}
