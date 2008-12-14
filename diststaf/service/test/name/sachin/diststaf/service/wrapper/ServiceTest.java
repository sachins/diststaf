package name.sachin.diststaf.service.wrapper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.sachin.diststaf.service.wrapper.Service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public class ServiceTest {

	private Service srvLocal;

	private Service srvSachin;

	private STAFHandle stafHandle;

	@Before
	public void setUp() throws Exception {
		stafHandle = new STAFHandle("FileSystemTest");
		srvLocal = new Service(stafHandle);
		srvSachin = new Service("sachin.name", stafHandle);
	}

	@After
	public void tearDown() throws Exception {
		stafHandle.unRegister();
		srvLocal = null;
		srvSachin = null;
	}

	@Test
	public void testList() throws STAFException {
		List<Map> rsp = srvLocal.list();
		assertEquals(16, rsp.size());
		List<String> srvList = new ArrayList<String>();
		for (Map m : rsp) {
			srvList.add((String) m.get("name"));
		}
		assertTrue(srvList.contains("SERVICE"));
		assertTrue(srvList.contains("FS"));
		rsp = srvSachin.list();
		assertTrue(rsp.size() >= 16);
	}

}
