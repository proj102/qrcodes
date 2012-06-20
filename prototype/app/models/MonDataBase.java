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
	
	private static MonDataBase instance;
	
	public static MonDataBase getInstance() {
		// Return the database if it has already been created
		// and if not create a new one
		
		if (instance == null)
			instance = new MonDataBase();
		
		return instance;
	}
	
	// connexion to the database
	public MonDataBase() {
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

	// retrieve the url of the document whose id is equal to the argument id
	public String getUrl( String id ) {
		DBCollection coll = db.getCollection("test");

		BasicDBObject query  = new BasicDBObject();
		query.put("id", id);
		DBCursor data  = coll.find(query);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			JsonNode json = mapper.readValue(data.next().toString(), JsonNode.class);
			String url = json.findPath("data").getTextValue();
			return url;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public boolean isConnected() {
		return authentification;
	}

       public void insert(String url){
                BasicDBObject doc = new BasicDBObject();
                doc.put("id", "1209fe7");
                doc.put("type", "url");
                doc.put("data", url);
                DBCollection coll = db.getCollection("test");
		coll.insert(doc);
        }
}
