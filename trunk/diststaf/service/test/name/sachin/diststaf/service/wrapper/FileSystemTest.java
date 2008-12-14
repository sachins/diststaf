package name.sachin.diststaf.service.wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.sachin.diststaf.exception.DistStafException;
import name.sachin.diststaf.service.wrapper.FileSystem;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;

public class FileSystemTest {

	private FileSystem fsLocal;

	private FileSystem fsSachin;

	private STAFHandle stafHandle;

	@Before
	public void setUp() throws Exception {
		stafHandle = new STAFHandle("FileSystemTest");
		fsLocal = new FileSystem(stafHandle);
		fsSachin = new FileSystem("sachin.name", stafHandle);
	}

	@After
	public void tearDown() throws Exception {
		stafHandle.unRegister();
		fsLocal = null;
		fsSachin = null;
	}

	@Test
	public void listSettings() throws STAFException {
		String rsp = fsLocal.listSettings();
		assertTrue(rsp.contains("Disabled"));
		rsp = fsSachin.listSettings();
		assertTrue(rsp.contains("Disabled"));
	}

	@Test
	public void listDirectory() throws STAFException {
		List result = fsLocal.listDirectory("/");
		assertTrue(result.contains("WINDOWS"));
		result = fsSachin.listDirectory("/");
		assertTrue(result.contains("WINDOWS"));
	}

	@Test
	public void listDirectoryWithLong() throws STAFException {
		List<Map> result = fsLocal
				.listDirectoryWithLong("{STAF/Config/STAFRoot}");
		assertTrue(result.size() >= 12);
		Map first = result.get(0);
		assertTrue(first.containsKey("type"));
		assertTrue(first.containsKey("lastModifiedTimestamp"));
		List<String> file = new ArrayList<String>();
		for (Map m : result) {
			file.add((String) m.get("name"));
		}
		assertTrue(file.contains("bin"));
		assertTrue(file.contains("LICENSE.htm"));
		result = fsSachin.listDirectoryWithLong("{STAF/Config/STAFRoot}");
		assertTrue(result.size() >= 12);

	}

	@Test
	public void createDirectory() throws STAFException {
		try {
			File f = new File("c:/tmp/staf");
			f.delete();
			fsLocal.createDirectory("c:/tmp/staf", true, true);
			assertTrue(f.exists());
			f.delete();
		} catch (DistStafException dse) {
			fail(dse.getMessage());
		}
		try {
			fsSachin.createDirectory("c:/tmp/staf", true, true);
		} catch (DistStafException dse) {
			assertTrue(true);
		}
	}

	@Test
	public void copyFile() throws STAFException {
		Var varLocal = new Var();
		File f = new File(varLocal.getSystemVar("STAF/Env/TEMP")
				+ varLocal.getSystemVar("STAF/Config/Sep/File") + "STAF.cfg");
		f.delete();
		fsLocal.copyFileToDirectory("{STAF/Config/ConfigFile}",
				"{STAF/Env/TEMP}");
		assertTrue(f.exists());
		fsSachin.deleteEntry(
				"{STAF/Env/SystemDrive}{STAF/Config/Sep/File}test.cfg", null,
				true, true);
		fsLocal.copyFileToMachineWithName("{STAF/Config/ConfigFile}",
				"sachin.name",
				"{STAF/Env/SystemDrive}{STAF/Config/Sep/File}test.cfg");
		assertTrue(fsSachin
				.fileExists("{STAF/Env/SystemDrive}{STAF/Config/Sep/File}test.cfg"));
	}
}
