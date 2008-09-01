package name.sachin.diststaf.service.wrapper;

public interface DistStafConstants {

	public enum FileSystemEntryType {
		ALL("ALL"), FILE("F"), DIR("D"), PIPE("P"), SOCKET("S"), LINK("L"), BLOCK_DVC(
				"B"), CHAR_DVC("C"), OTHER("O"), UNKNOWN("?");
		private String typeString;

		FileSystemEntryType(String typeString) {
			this.typeString = typeString;
		}

		public String toString() {
			return this.typeString;
		}
	}

	public enum SortOrder {
		SORTBYNAME, SORTBYSIZE, SORTBYMODTIME;
	}

	public enum CaseManner {
		CASESENSITIVE, CASEINSENSITIVE;
	}

	public enum LongDetailOption {
		L("long"), LD("long details");
		private String option;

		LongDetailOption(String option) {
			this.option = option;
		}

		public String toString() {
			return option;
		}
	}

	public enum TextFormat {
		NATIVE, UNIX, WINDOWS, ASIS
	}

	public enum DestinationType {
		TOFILE, TODIRECTORY
	}
	
	public enum FailIfFile {
		FAILIFEXISTS, FAILIFNEW
	}
	
	public enum VarPoolType {
		SYSTEM, SHARED
	}
	
	public enum ServiceLibraryType {
		JSTAF
	}

	public enum JobStatus {
		SUBMITTED,
		INPROGRESS,
		FINISHED
	}
	
	public enum ResourceType {
		MACHINE
	}

	public static final String FS_SRV_NAME = "fs";

	public static final String SERVICE_SRV_NAME = "service";
	
	public static final String VAR_SRV_NAME = "var";

	public static final String STAF_LOCAL_HOST = "local";
	
	public static final String PROCESS_SRV_NAME = "process";

}
