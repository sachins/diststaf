package name.sachin.diststaf.service;

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
import com.ibm.staf.STAFUtil;

public class DistStafServiceLoadTest {

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
		// srvLocal.remove("diststaf");
	}

	@Before
	public void setUp() throws Exception {
		stafHandle = new STAFHandle("DistStafTest");
	}

	@After
	public void tearDown() throws Exception {
		stafHandle.unRegister();
	}

	// This is a load test of four concurrent requests on one machine
	@Test
	public void testConcurrentRequests() throws STAFException {
		String loadTestHost = "sachins-linux";
		int concurrentRequests = 1;
		File algorithm = new File("target/jar/randomdata.jar");
		stafHandle.submit(distStafHost, "diststaf", "addjob testjob");
		stafHandle.submit(distStafHost, "diststaf", "addresource "
				+ loadTestHost);
		for (int i = 0; i < concurrentRequests; i++) {
			String taskname = "task" + i;
			stafHandle.submit(distStafHost, "diststaf", "addtask " + taskname
					+ " jobname testjob resourcename " + loadTestHost
					+ " algorithm "
					+ STAFUtil.wrapData(algorithm.getAbsolutePath())
					+ " algorithmtype JAR arguments \"random.dat 1048576 5\"");
		}
		String result = stafHandle.submit(distStafHost, "diststaf",
				"executejob testjob");
		System.out.println("Result:" + result);
	}

}
