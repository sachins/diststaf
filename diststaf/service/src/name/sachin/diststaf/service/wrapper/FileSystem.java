package name.sachin.diststaf.service.wrapper;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import name.sachin.diststaf.exception.DistStafException;
import static name.sachin.diststaf.service.DistStafConstants.*;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFUtil;

public class FileSystem extends StafService {

	private static Logger LOG = Logger.getLogger(FileSystem.class);

	public FileSystem() throws STAFException {
		super();
	}

	public FileSystem(String stafHost, STAFHandle stafHandle) {
		super(stafHost, stafHandle);
	}

	public FileSystem(STAFHandle stafHandle) {
		super(stafHandle);
	}

	public String listSettings() throws STAFException {
		return stafHandle.submit(stafHost, FS_SRV_NAME, "list settings");
	}

	public String listDirectory(String dirName, String namePattern,
			String extPattern, FileSystemEntryType type, SortOrder sort,
			CaseManner caseManner, LongDetailOption ldOption, boolean recurse)
			throws STAFException {
		String req = "list directory " + STAFUtil.wrapData(dirName);
		if (namePattern != null)
			req += " name " + namePattern;
		if (extPattern != null)
			req += " ext " + extPattern;
		if (type != null)
			req += " type " + type;
		if (sort != null)
			req += " " + sort;
		if (caseManner != null)
			req += " " + caseManner;
		if (ldOption != null)
			req += " " + ldOption;
		if (recurse)
			req += " recurse";
		LOG.info(this + " - Sending request:" + req);
		return stafHandle.submit(stafHost, FS_SRV_NAME, req);
	}

	@SuppressWarnings("unchecked")
	public List<Map> listDirectoryWithLong(String dirName) throws STAFException {
		String result = listDirectory(dirName, null, null, null, null, null,
				LongDetailOption.L, false);
		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(result,
				STAFMarshallingContext.IGNORE_INDIRECT_OBJECTS);
		if (mc.hasMapClassDefinition("STAF/Service/FS/ListLongInfo")) {
			return (List<Map>) mc.getRootObject();
		} else {
			throw new DistStafException("Response doesn't contain map");
		}
	}

	public List listDirectory(String dirName) throws STAFException {
		String result = listDirectory(dirName, null, null, null, null, null,
				null, false);
		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(result);
		return (List) mc.getPrimaryObject();
	}

	public void createDirectory(String name, boolean fullpath,
			boolean failIfExists) throws STAFException {
		String req = "create directory " + STAFUtil.wrapData(name);
		if (fullpath)
			req += " fullpath";
		if (failIfExists)
			req += " failifexists";
		LOG.info(this + " - Sending request:" + req);
		String rsp = stafHandle.submit(stafHost, FS_SRV_NAME, req);
		if (rsp.length() > 0) {
			throw new DistStafException(rsp);
		}
		LOG.info(this + " - Successfully created dir " + name);
	}

	public void copyFile(String fileName, ToFileDirectoryOption toOption,
			String toMachine, TextFormatOption tfOption, FailIfFile failIfOption)
			throws STAFException {
		String req = "copy file " + STAFUtil.wrapData(fileName);
		if (toOption != null)
			req += " " + toOption;
		if (toMachine != null)
			req += " tomachine " + toMachine;
		if (tfOption != null)
			req += " " + tfOption;
		if (failIfOption != null)
			req += " " + failIfOption;
		LOG.info(this + " - Sending request:" + req);
		String rsp = stafHandle.submit(stafHost, FS_SRV_NAME, req);
		if (rsp.length() > 0) {
			throw new DistStafException(rsp);
		}
		LOG.info(this + " - Successfully copied file " + fileName);
	}

	public void copyFileToFile(String srcFileName, String targetFileName)
			throws STAFException {
		copyFile(srcFileName, new ToFileDirectoryOption(DestinationType.TOFILE,
				targetFileName), null, null, null);
	}

	public void copyFileToMachineWithName(String srcFileName, String toMachine,
			String targetFileName) throws STAFException {
		copyFile(srcFileName, new ToFileDirectoryOption(DestinationType.TOFILE,
				targetFileName), toMachine, null, null);
	}

	public void copyFileToDirectory(String srcFileName, String targetDirName)
			throws STAFException {
		copyFile(srcFileName, new ToFileDirectoryOption(
				DestinationType.TODIRECTORY, targetDirName), null, null, null);
	}

	public void copyFileToMachineToDirectory(String srcFileName,
			String toMachine, String targetDirName) throws STAFException {
		copyFile(srcFileName, new ToFileDirectoryOption(
				DestinationType.TODIRECTORY, targetDirName), toMachine, null,
				null);
	}

	public void copyDirectory(String dirName, String toDirectory,
			String toMachine, boolean recurse) throws STAFException {
		String req = "copy directory " + STAFUtil.wrapData(dirName);
		if (toDirectory != null)
			req += " todirectory " + toDirectory;
		if (toMachine != null)
			req += " tomachine " + toMachine;
		if (recurse)
			req += " recurse";
		LOG.info(this + " - Sending request:" + req);
		String rsp = stafHandle.submit(stafHost, FS_SRV_NAME, req);
		if (rsp.length() > 0) {
			LOG.error("Failed to copy directory:" + rsp);
			throw new DistStafException(rsp);
		}
		LOG.info(this + " - Successfully copied direcotry " + dirName);

	}

	public boolean fileExists(String fileName) {
		return entryExists(fileName, FileSystemEntryType.FILE);
	}

	public boolean dirExists(String dirName) {
		return entryExists(dirName, FileSystemEntryType.DIR);
	}

	public boolean entryExists(String entryName, FileSystemEntryType entryType) {
		try {
			String req = "get entry " + STAFUtil.wrapData(entryName) + " type";
			LOG.info(this + " - Sending request: " + req);
			String rsp = stafHandle.submit(stafHost, FS_SRV_NAME, req);
			return entryType.toString().equalsIgnoreCase(rsp);
		} catch (STAFException se) {
			LOG.error("STAFException Received", se);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map> deleteEntry(String entryName,
			ChildrenOption childrenOption, boolean recurse, boolean ignoreErrors)
			throws STAFException {
		String req = "delete entry " + STAFUtil.wrapData(entryName);
		if (childrenOption != null)
			req += childrenOption;
		if (recurse)
			req += " recurse";
		if (ignoreErrors)
			req += " ignoreerrors";
		req += " confirm";
		LOG.info(this + " - Sending request:" + req);

		String result = stafHandle.submit(stafHost, FS_SRV_NAME, req);
		if (result.length() == 0) // successful scenario
			return null;
		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(result);
		if (mc.hasMapClassDefinition("STAF/Service/FS/ErrorInfo")) {
			// this can be partial or no success scenario
			return (List<Map>) mc.getRootObject();
		} else {
			return null;
		}
	}

	public String toString() {
		return "FileSystem for " + stafHost;
	}

	public static class ToFileDirectoryOption {
		private String fileOrDirName;

		private DestinationType destType;

		public ToFileDirectoryOption(DestinationType destType,
				String fileOrDirName) {
			this.destType = destType;
			this.fileOrDirName = fileOrDirName;
		}

		public String toString() {
			return destType + " " + STAFUtil.wrapData(fileOrDirName);
		}
	}

	public static class TextFormatOption {
		private TextFormat tf;

		public TextFormatOption() {
		}

		public TextFormatOption(TextFormat tf) {
			this.tf = tf;
		}

		public String toString() {
			return "text " + ((tf == null) ? "" : tf);
		}
	}

	public static class ChildrenOption {
		private String namePattern;

		private String extPattern;

		private FileSystemEntryType type;

		private CaseManner caseManner;

		public ChildrenOption() {

		}

		public ChildrenOption(String namePattern, String extPattern,
				FileSystemEntryType type, CaseManner caseManner) {
			this.namePattern = namePattern;
			this.extPattern = extPattern;
			this.type = type;
			this.caseManner = caseManner;
		}

		public String toString() {
			String rsp = "children";
			if (namePattern != null)
				rsp += " name " + STAFUtil.wrapData(namePattern);
			if (extPattern != null)
				rsp += " ext " + STAFUtil.wrapData(extPattern);
			if (type != null)
				rsp += " type " + type;
			if (caseManner != null)
				rsp += " " + caseManner;
			return rsp;
		}

	}

	@Override
	protected String getServiceName() {
		return FS_SRV_NAME;
	}
}
