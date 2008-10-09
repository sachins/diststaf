package name.sachin.diststaf.service;

import static org.junit.Assert.*;

import java.io.File;

import name.sachin.diststaf.service.DistStafConstants.ServiceLibraryType;
import name.sachin.diststaf.service.wrapper.Service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;

public class DistStafServiceTest {

	private static Service srvLocal;

	private STAFHandle stafHandle;

	private static final String distStafHost = "local";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		srvLocal = new Service();
		if (srvLocal.list().contains("diststaf")) {
			srvLocal.remove("diststaf");
		}
		File execute = new File("target/jar/diststaf.jar");
		srvLocal.add("diststaf", ServiceLibraryType.JSTAF, execute);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		srvLocal.remove("diststaf");
	}

	@Before
	public void setUp() throws Exception {
		stafHandle = new STAFHandle("DistStafTest");
	}

	@After
	public void tearDown() throws Exception {
		stafHandle.unRegister();
	}

	// @Test
	public void testHelp() throws STAFException {
		String response = stafHandle.submit(distStafHost, "diststaf", "help");
		response.contains("DISTSTAF Help");
	}

	public void testExecute() throws STAFException {
		stafHandle.submit(distStafHost, "diststaf",
				"addjob testjob algorithm dir");
		// stafHandle.submit(distStafHost, "diststaf", "addresource local type
		// machine");
		// stafHandle.submit(distStafHost, "diststaf", "assignresourcetojob
		// local job testjob");
		// stafHandle.submit(distStafHost, "diststaf", "addresource
		// sachins-linux type machine");
		// stafHandle.submit(distStafHost, "diststaf", "assignresourcetojob
		// sachins-linux job testjob");
		stafHandle.submit(distStafHost, "diststaf",
				"addresource 192.168.2.6 type machine");
		stafHandle.submit(distStafHost, "diststaf",
				"assignresourcetojob 192.168.2.6 job testjob");
		String result = stafHandle.submit(distStafHost, "diststaf",
				"executejob testjob");
		System.out.println("Result:" + result);
		assertTrue(result.contains("staf"));
		// fail("Not yet implemented");
	}

	@Test
	public void testExecuteJar() throws STAFException {
		stafHandle
				.submit(
						distStafHost,
						"diststaf",
						"addjob testjob algorithm \"C:/Documents and Settings/sachins/workspace/diststaf/target/jar/sample.jar\" algorithmtype jar data \"C:/Documents and Settings/sachins/workspace/diststaf/service/sampledata.txt\"");
		stafHandle.submit(distStafHost, "diststaf",
				"addresource local type machine");
		stafHandle.submit(distStafHost, "diststaf",
				"assignresourcetojob local job testjob");
		stafHandle.submit(distStafHost, "diststaf",
				"addresource sachins-linux type machine");
		stafHandle.submit(distStafHost, "diststaf",
				"assignresourcetojob sachins-linux job testjob");
		String result = stafHandle.submit(distStafHost, "diststaf",
				"executejob testjob");
		System.out.println("Result:" + result);
	}
}
