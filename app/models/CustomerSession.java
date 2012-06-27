package models;

import com.mongodb.DBObject;

public class CustomerSession {

	private String login;
	private String id;
	boolean justLogged = false;
	private String urlToAvatar;
	private String lastName;
	private String firstName;

	public CustomerSession(){}
	
	public CustomerSession(String login, String urlToAvatar, String id){
		this.login = login;
		this.urlToAvatar= urlToAvatar;
		this.id= id;
	}

	public CustomerSession(String id, String login) {
		this.login = login;
		this.id = id;
		justLogged = true;
		getInfo();
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login){
		this.login = login;
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

	public void setAvatar(String urlToAvatar){
		this.urlToAvatar = urlToAvatar;
	}

	public String getLastName(){
		return lastName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	/**
	  *To get all the information from a customer
	  *
	 **/
	public void getInfo(){
		MonDataBase db = MonDataBase.getInstance();
		DBObject dbo   = db.getInfoCustomer(id);
		login 	       = db.getElement(dbo, "login");
		urlToAvatar    = db.getElement(dbo, "avatar");
		lastName       = db.getElement(dbo, "name");
		firstName      = db.getElement(dbo, "firstname");	
	}
}
