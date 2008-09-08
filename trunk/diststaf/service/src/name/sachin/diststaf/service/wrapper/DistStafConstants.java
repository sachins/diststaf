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
	
	public static final String DISTSTAF_SRV_NAME = "diststaf";
	
	public static final int JOB_DOESNT_EXIST = 4021;

	public static final int RESOURCE_DOESNT_EXIST = 4020;

	public static final int RESOURCE_ALREADY_ASSIGNED = 4022;

	public static final int ASSIGN_RESOURCE_TO_JOB_FAILED = 4023;

	public static final int DELETE_JOB_FAILED = 4003;
	
	public static final int RESOURCE_EXISTS = 4010;

	public static final int ADD_RESOURCE_FAILED = 4011;
	
	public static final int EXECUTEJOB_FAILED = 4030;
	
	public static final int ADD_JOB_FAILED = 4002;
	public static final int JOB_EXISTS = 4001;
	
	public static final int DELETE_JOB_TRUST_LEVEL = 3;
	
	public static final int ASSIGNRESOURCETOJOB_TRUST_LEVEL = 3;
	
	public static final int ADDRESOURCE_TRUST_LEVEL = 3;
	
	public static final int ADDJOB_TRUST_LEVEL = 3;
	
	public static final int EXECUTEJOB_TRUST_LEVEL = 3;

}
