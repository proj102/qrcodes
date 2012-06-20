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
	private ObjectMapper mapper;
	  
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
		
		mapper = new ObjectMapper();
		try {
			mongo = new Mongo(address, port);
			db = mongo.getDB(name);
			authentification = db.authenticate(login, password.toCharArray());
		}
		catch (Exception e) {
			System.out.println("Mongo exception when connecting to the DB : " + e);
		}
	}

	// Retrieve the url of the document whose id is equal to the argument id
	public String getUrl( String id ) throws Exception {
		DBCollection coll = db.getCollection("test");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", id);
		DBCursor data  = coll.find(query);
		return getElement(data, "data");	
	}
	
	public boolean isConnected() {
		return authentification;
	}

	// Insert redirection url in new JSON document
	public void insert(String url){
                DBCollection coll = db.getCollection("test");
                BasicDBObject doc = new BasicDBObject();
                doc.put("id", generateIdQRCode());
                doc.put("type", "url");
                doc.put("data", url);
		coll.insert(doc);
        }

	// Generation unique ID
	public int generateIdQRCode(){
		DBCollection coll = db.getCollection("test");
		BasicDBObject query  = new BasicDBObject();
		BasicDBObject sorted  = new BasicDBObject();
		query.put("id", 1); // selection all id of the collection
		sorted.put("id",-1); // sort by "id" descending
		// find all ids; sort its and get the max one
		DBCursor idmax = coll.find(new BasicDBObject(), query).sort(sorted).limit(1);
		return Integer.parseInt(getElement(idmax, "id")) + 1; // max id + 1 => unique id
	}

	// Decode JSON in DBCursor and get the "key" element
	public String getElement(DBCursor cursor, String key){
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			JsonNode json = mapper.readValue(cursor.next().toString(), JsonNode.class);
			return json.findPath(key).getTextValue();
		}
		catch(Exception e) {
			return "problem decode Json";
		}
	}
}
