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

import java.security.MessageDigest;
import java.util.Formatter;
import java.security.NoSuchAlgorithmException;

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
	public String getUrl( String id ) throws Exception {
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
			throw new Exception("url problem : " + data.next().toString());
		}
	}
	
	public boolean isConnected() {
		return authentification;
	}
	
	public static String sha1Encrypt(String s) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] hash = md.digest(s.getBytes());
		
		Formatter formatter = new Formatter();
		for (byte b : hash)
			formatter.format("%02x", b);
		
		return formatter.toString();
	}
	
	// add a customer to the db after checking the validity of
	// the informations given and after generating a unique ID
	public void addCustomer(String login, String password, String mail, String name, String firstname, String firm) throws Exception {
		if (login == null || login == "")
			throw new Exception("No login.");
		else if (password == null || password == "")
			throw new Exception("No password.");
		else if (mail == null || mail == "")
			throw new Exception("No mail.");
		
		
		// all requieted fields have been completed
		// let's check their validity
		DBCollection coll = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("login", login);
		DBCursor data  = coll.find(query);
		if (data.count() != 0)
			throw new Exception("Login already used.");
		
		// crypt the password in SHA-1
		password = sha1Encrypt(password);
		
		// add the customer
		BasicDBObject customerInfos = new BasicDBObject();
		customerInfos.put("id", 10);
		customerInfos.put("login", login);
		customerInfos.put("password", password);
		customerInfos.put("mail", mail);
		customerInfos.put("inscription", System.currentTimeMillis());
		customerInfos.put("name", name);
		customerInfos.put("firstname", firstname);
		customerInfos.put("firm", firm);
		customerInfos.put("avatar", "");
		customerInfos.put("qrs", "[]");
		
		coll.insert(customerInfos);
	}
	
	public String testPass() {
		DBCollection coll = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("login", "plequen");
		DBCursor data  = coll.find(query);
		
		if (data.count() == 0)
			return "not found";
		else {
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-1");
				digest.reset();
				String password = "test2";
				password = sha1Encrypt(password);
				
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
				
			
				JsonNode json = mapper.readValue(data.next().toString(), JsonNode.class);
				String pass = json.findPath("password").getTextValue();
				
				if (pass.equals(password))
					return "pass value ok";
				else
					return "pass value not ok";
			}
			catch (Exception e) {
				return "pass not ok : " + e;
			}
		}
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
