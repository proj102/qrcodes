package models;

import java.lang.Exception;

import play.libs.Json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;

// Class depicting a MongoDB database
public class MonDataBase {

	private String address;
	private int port;
	private String name;
	
	private String login;
	private String password;

	private Mongo mongo;
	private DB db;
	boolean authentification;
	
	// Only one database
	private static MonDataBase instance;
	
	public static MonDataBase getInstance() {
		// Return the database if it has already been created
		// and if not create a new one
		
		if (instance == null)
			instance = new MonDataBase();
		
		return instance;
	}
	
	public MonDataBase() {
		//address = "ds035127.mongolab.com";
		address = "ds033767.mongolab.com";
		port = 33767;
		name = "qrcodes";
		login = "plequen";
		password = "test";
		
		try {
			mongo = new Mongo(address, port);
			db = mongo.getDB(name);
			authentification = db.authenticate(login, password.toCharArray());
		}
		catch (Exception e) {
			System.out.println("Mongo exception when connecting to the DB : " + e);
		}
	}
	
	public String getInformations() {
		// get the first document of the collection test
		DBCollection coll = db.getCollection("test");
		DBObject myDoc = coll.findOne();
		
		// map it to have a JsonNode object in order to get its values and return the url
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			JsonNode json = mapper.readValue(myDoc.toString(), JsonNode.class);
			String url = json.findPath("url").getTextValue();
			
			return url;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public boolean isConnected() {
		return authentification;
	}
}