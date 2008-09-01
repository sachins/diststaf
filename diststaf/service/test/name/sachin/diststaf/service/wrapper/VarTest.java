package name.sachin.diststaf.service.wrapper;

import static org.junit.Assert.*;

import name.sachin.diststaf.service.wrapper.DistStafConstants.VarPoolType;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ibm.staf.STAFHandle;

public class VarTest {

	private Var srvLocal;

	private Var srvSachin;

	private STAFHandle stafHandle;

	@Before
	public void setUp() throws Exception {
		stafHandle = new STAFHandle("VarTest");
		srvLocal = new Var(stafHandle);
		srvSachin = new Var("sachin.name", stafHandle);
	}

	@After
	public void tearDown() throws Exception {
		stafHandle.unRegister();
		srvLocal = null;
		srvSachin = null;
	}

	@Test
	public void testSetGetDeleteVar() {
		String var = "mysharedvar";
		String value = "mysharedvalue";
		srvLocal.setVar(VarPoolType.SHARED, var, value);
		assertEquals(value, srvLocal.getVar(VarPoolType.SHARED, var));
		srvLocal.deleteVar(VarPoolType.SHARED, var);
	}

	@Test
	public void testGetSystemVar() {
		String var = "STAF/Config/Sep/Path";
		assertEquals(";", srvLocal.getSystemVar(var));
	}

}
