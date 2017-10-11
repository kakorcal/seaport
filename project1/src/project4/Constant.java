package project4;

public class Constant {
	// statuses
	public static final String JOB_RUNNING = "RUNNING";
	public static final String JOB_WAITING_PORT = "WAITING - PORT";
	public static final String JOB_WAITING_DOCK = "WAITING - DOCK";
	public static final String JOB_SUSPENDED = "SUSPENDED";
	public static final String JOB_DONE = "DONE";
	public static final String JOB_CANCELED = "CANCELED";
	public static final String JOB_CANCEL = "CANCEL";
	public static final String JOB_SKILL_NOT_FOUND = "SKILL NOT FOUND";
	public static final String SHIP_RELEASED = "RELEASED";
	public static final String PERSON_ASSIGNED_JOB = "ASSIGNED";
	public static final String PERSON_UNASSIGNED_JOB = "UNASSIGNED";
	public static final String PERSON_WORKING = "WORKING";
	public static final String NONE = "NONE";
	public static final String IDLE = "--";
	// job table
	public static final int JOB_NAME = 0;
	public static final int JOB_REQUIREMENTS = 1;
	public static final int JOB_PORT = 2;
	public static final int JOB_DOCK = 3;
	public static final int JOB_SHIP = 4;
	public static final int JOB_PROGRESS_BAR = 5;
	public static final int JOB_STATUS_BUTTON = 6;
	public static final int JOB_CANCEL_BUTTON = 7;
	public static final int JOB_INDEX = 8;
	public static final int JOB_PORT_INDEX = 9;
	public static final int JOB_DOCK_INDEX = 10;
	public static final int JOB_SHIP_INDEX = 11;
	// person table
	public static final int PERSON_NAME = 0;
	public static final int PERSON_SKILL = 1;
	public static final int PERSON_PORT = 2;
	public static final int PERSON_DOCK = 3;
	public static final int PERSON_SHIP = 4;
	public static final int PERSON_JOB = 5;
	public static final int PERSON_REQUIREMENTS = 6;
	public static final int PERSON_STATUS = 7;
	public static final int PERSON_INDEX = 8;
	public static final int PERSON_PORT_INDEX = 9;
	public static final int PERSON_DOCK_INDEX = 10;
	public static final int PERSON_SHIP_INDEX = 11;
	public static final int PERSON_JOB_INDEX = 12;
	// max indices
	public static final int MAX_PORT_INDEX = 19999;
	public static final int MAX_DOCK_INDEX = 29999;
	public static final int MAX_SHIP_INDEX = 49999;
}
