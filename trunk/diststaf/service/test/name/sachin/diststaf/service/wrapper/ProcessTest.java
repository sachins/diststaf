package name.sachin.diststaf.service.wrapper;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public class ProcessTest {

	private STAFHandle stafHandle;

	private Process procLocal;

	@Before
	public void setUp() {
		try {
			stafHandle = new STAFHandle("ProcessTest");
		} catch (STAFException e) {
			e.printStackTrace();
		}
		procLocal = new Process(stafHandle);
	}

	@After
	public void tearDown() throws Exception {
		// stafHandle.unRegister();
		procLocal = null;
	}

	@Test
	public void start() throws STAFException {
		Map resultMap = procLocal.start("java -version");
		assertEquals("0", resultMap.get("rc"));
		List<Map> fileList = (List<Map>) resultMap.get("fileList");
		assertEquals(1, fileList.size());
		assertEquals("0", fileList.get(0).get("rc"));
		assertTrue(((String) fileList.get(0).get("data"))
				.contains("java version"));
	}

}
