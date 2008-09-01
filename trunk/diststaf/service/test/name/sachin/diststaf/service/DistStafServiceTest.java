package name.sachin.diststaf.service;

import static org.junit.Assert.*;

import java.io.File;

import name.sachin.diststaf.service.wrapper.Service;
import name.sachin.diststaf.service.wrapper.DistStafConstants.ServiceLibraryType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public class DistStafServiceTest {
	
	private static Service srvLocal;
	
	private STAFHandle stafHandle;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		srvLocal = new Service();
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

	@Test
	public void testHelp() throws STAFException {
		String response = stafHandle.submit("local", "diststaf", "help");
		response.contains("");
	}
	
	@Test
	public void testAddJob() {
		
		fail("Not yet implemented");
	}

	@Test
	public void testGetJobList() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetClusterList() {
		fail("Not yet implemented");
	}

	@Test
	public void testJobNameExists() {
		fail("Not yet implemented");
	}

	@Test
	public void testAssociateJobWithCluster() {
		fail("Not yet implemented");
	}

}
