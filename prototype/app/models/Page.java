package models;

import java.lang.Exception;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

public class Page {

	public String name;
	public String colour;
	public int topSpeed;
	
	public static Mongo m;

	public static DB db;
	
	boolean auth;
	
	public Page() {
		try {
			m = new Mongo("ds033767.mongolab.com", 33767);
			db = m.getDB("qrcodes");
			
			auth = db.authenticate("plequen", "test".toCharArray());
		}
		catch (Exception e) {
			System.out.println("BDD problem");
		}
	}
	
	public String informations() {
		if (auth)
			return "Authentification successful";
		else
			return "Authentification failed";
	}
}