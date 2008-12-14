package name.sachin.diststaf.service;

import java.io.File;
import java.math.BigInteger;

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

	private String[] nodes = { "mehta", "tte-ra-2", "sachins-linux",
			"ath-lintools-1", "cai-lintools-1", "hbo3-lintools-1",
			"mad-lintools-1", "mgate-lintools-1", "mit-lintools-1",
			"mtv-lintools-1", "nan-lintools-1", "nbc-lintools-1",
			"pax-lintools-1", "pbs-lintools-1", "pop-lintools-1",
			"rio-lintools-1", "sun-lintools-1", "tnt-lintools-1",
			"wgn-lintools-1", "wgn-lintools-2", "xyz-lintools-1",
			"xyz-lintools-2", "ztv-lintools-1", "ztv-lintools-2" };

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

	// This is a load unit test for IO Bound program.
	@Test
	public void testIOBoundProgram() throws STAFException {
		File program = new File("target/jar/randomdata.jar");
		stafHandle.submit(distStafHost, "diststaf", "addjob testjob");
		int totalNodes = 1;
		int concurrentRequests = 1;

		for (int r = 0; r < totalNodes; r++) {
			stafHandle.submit(distStafHost, "diststaf", "addnode "
					+ nodes[r]);

			for (int i = 0; i < concurrentRequests; i++) {
				String taskname = "task" + i;
				stafHandle
						.submit(
								distStafHost,
								"diststaf",
								"addtask "
										+ taskname
										+ " jobname testjob nodename "
										+ nodes[r]
										+ " program "
										+ STAFUtil.wrapData(program
												.getAbsolutePath())
										+ " programtype JAR arguments \"random.dat 1048576 5\"");
			}
		}
		String result = stafHandle.submit(distStafHost, "diststaf",
				"executejob testjob");
		System.out.println("Result:" + result);
	}

	// This is a load unit test for CPU Bound program.
	//@Test
	public void testCPUBoundProgram() throws STAFException {

		File program = new File("target/jar/primenum.jar");
		stafHandle.submit(distStafHost, "diststaf", "addjob testjob");

		int totalNodes = 1;
		int concurrentRequests = 1;
		BigInteger startNum = new BigInteger("1000000000");
		BigInteger endNum = new BigInteger("1000050000");
		BigInteger range = endNum.add(startNum.negate());

		BigInteger[] divisionAndRemainder = range.divideAndRemainder(BigInteger
				.valueOf(totalNodes * concurrentRequests));
		BigInteger increment = divisionAndRemainder[0];
		if (divisionAndRemainder[1].compareTo(BigInteger.ZERO) > 0) {
			increment = divisionAndRemainder[0].add(BigInteger.ONE);
		}

		for (int r = 0; r < totalNodes; r++) {
			stafHandle.submit(distStafHost, "diststaf", "addnode "
					+ nodes[r]);
		}
		int taskNum = 0;
		for (int i = 0; i < concurrentRequests; i++) {
			for (int r = 0; r < totalNodes; r++) {
				String taskname = "task" + taskNum++;
				BigInteger taskEndNum = startNum.add(increment).add(
						BigInteger.ONE.negate());
				if (taskEndNum.compareTo(endNum) > 0) {
					taskEndNum = endNum;
				}
				String arguments = startNum + " " + taskEndNum;
				stafHandle.submit(distStafHost, "diststaf", "addtask "
						+ taskname + " jobname testjob nodename "
						+ nodes[r] + " program "
						+ STAFUtil.wrapData(program.getAbsolutePath())
						+ " programtype JAR arguments \"" + arguments + "\"");
				startNum = taskEndNum.add(BigInteger.ONE);
			}
		}
		String result = stafHandle.submit(distStafHost, "diststaf",
				"executejob testjob");
		System.out.println("Result:" + result);

	}

}
