package Controllers;

import logic.Subscriber;

public interface IDataBaseManager {
	public Subscriber getSubBySubId(String subId);
	public boolean isMemberExist(String id, String pass);

}
