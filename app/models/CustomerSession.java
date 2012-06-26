package models;

import com.mongodb.DBObject;

public class CustomerSession {

	private String login;
	private String id;
	boolean justLogged = false;
	private String urlToAvatar;

	
	public CustomerSession(String id, String login) {
		this.login = login;
		this.id = id;
		justLogged = true;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setJustLogged(boolean b) {
		justLogged = b;
	}
	
	public boolean getJustLogged() {
		return justLogged;
	}
	
	public String getId() {
		return id;
	}
	
	public String getAvatar(){
		return urlToAvatar;
	}

	public void setLogin(String login){
		this.login = login;
	}

	public void setAvatar(String urlToAvatar){
		this.urlToAvatar = urlToAvatar;
	}
	
	/**
	  *To get all the information from a customer
	  *
	  *TODO: implement the MongoDB method in MonDataBase.java to getInfos(id)
	 **/
	public void getInfo(){
		MonDataBase db = MonDataBase.getInstance();
		DBObject dbo   = db.getInfoCustomer(id);
		login 	       = getElement(dbo, "login");
		urlToAvatar    = getElement(dbo, "avatar");				
	}
}
