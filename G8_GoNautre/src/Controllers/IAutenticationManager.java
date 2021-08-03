package Controllers;

public interface IAutenticationManager {
	
	public boolean isConnected(String id);
	
	public boolean isTravelerExist(String id);
	
	public void insertTologgedinTable(String id);
}
