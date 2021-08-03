package logic;

/**
 * this class represent the enumerated type of workers
 */
public enum WorkerType {
	
	ENTRANCE("Entrance"), SERVICE("Service"), PARK_MANAGER("Park Manager"), DEPARTMENT_MANAGER("Department Manager");

	private String str;

	WorkerType(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}