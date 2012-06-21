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
		int customerId = generateCustomerId();
		
		BasicDBObject customerInfos = new BasicDBObject();
		customerInfos.put("id", customerId);
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
	
	// insert a qrcode in the database with the given json content
	// to which is added significant fields such as the id of the qrcode
	public void insertQr(BasicDBObject qrInfos) throws Exception {
		int customerId = getCustomerId();
		
		// check that the customer exists
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", customerId);
		DBCursor data  = customers.find(query);
		if (data.count() == 0)
			throw new Exception("Unknown customer.");
			
		// everything is fine
		try {
			// let's add the qrcode to the qrcodes collection
			DBCollection qrs = db.getCollection("qrcodes");
			String qrId = String.valueOf(generateIdQRCode());
			
			qrInfos.put("id", qrId);
			qrInfos.put("creation", System.currentTimeMillis());
			qrInfos.put("flashs", 0);
			qrs.insert(qrInfos);
			
			// let's add the qr id to the customer's qrs
			String custQrs = getElement(data, "qrs");
			
			if (custQrs.equals("[]"))
				custQrs = "[" + qrId + "]";
			else
				custQrs = custQrs.substring(0, custQrs.length() - 1) + ", " + qrId + "]";
			
			BasicDBObject newCustDoc = new BasicDBObject().append("$set", new BasicDBObject().append("qrs", custQrs));
 
			customers.update(new BasicDBObject().append("id", customerId), newCustDoc);
		}
		catch (Exception e) {
			throw new Exception("Error when adding the qrcode : " + e);
		}
	}
	
	// add a Qr with the data given in the "generate a Qrcode" form
	public void addQrFromForm(String type, String redirection, String title, String place) throws Exception {
		BasicDBObject qrInfos = new BasicDBObject();
		qrInfos.put("type", type);
		qrInfos.put("redirection", redirection);
		qrInfos.put("title", title);
		qrInfos.put("place", place);
		
		insertQr(qrInfos);
	}
	
	// get the id of the current customer
	public int getCustomerId() {
		return 1;
	}
	
	// add a qrCode to the database and add it to the customer's qrs
	/*public void addQr(int customerId, String type, String redirection, String title, String place) throws Exception {
		// check that the customer exists
		DBCollection customers = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("id", customerId);
		DBCursor data  = customers.find(query);
		if (data.count() == 0)
			throw new Exception("Unknown customer.");
		
		// everything is fine
		try {
			// let's add the qrcode to the qrcodes collection
			DBCollection qrs = db.getCollection("qrcodes");
			String qrId = String.valueOf(generateIdQRCode());
			
			BasicDBObject qrInfos = new BasicDBObject();
			qrInfos.put("id", qrId);
			qrInfos.put("type", type);
			qrInfos.put("redirection", redirection);
			qrInfos.put("creation", System.currentTimeMillis());
			qrInfos.put("title", title);
			qrInfos.put("place", place);
			qrInfos.put("flashs", 0);
			qrs.insert(qrInfos);
			
			// let's add the qr id to the customer's qrs
			String custQrs = getElement(data, "qrs");
			
			if (custQrs.equals("[]"))
				custQrs = "[" + qrId + "]";
			else
				custQrs = custQrs.substring(0, custQrs.length() - 1) + ", " + qrId + "]";
			
			BasicDBObject newCustDoc = new BasicDBObject().append("$set", new BasicDBObject().append("qrs", custQrs));
 
			customers.update(new BasicDBObject().append("id", customerId), newCustDoc);
		}
		catch (Exception e) {
			throw new Exception("Error when adding the qrcode : " + e);
		}
	}*/
	
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
				
				String pass = getElement(data, "password");
				
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

	// Insert redirection url in new JSON document
	public void insert(String url){
                DBCollection coll = db.getCollection("test");
                BasicDBObject doc = new BasicDBObject();
                doc.put("id", generateIdQRCode());
                doc.put("type", "url");
                doc.put("data", url);
		coll.insert(doc);
    }

	// Generation unique QRID 
	public int generateIdQRCode(){
		DBCollection coll = db.getCollection("qrcodes");
		BasicDBObject query  = new BasicDBObject();
		BasicDBObject sorted  = new BasicDBObject();
		query.put("id", 1); // selection all id of the collection
		sorted.put("id",-1); // sort by "id" descending
		// find all ids; sort its and get the max one
		DBCursor idmax = coll.find(new BasicDBObject(), query).sort(sorted).limit(1);
		return Integer.parseInt(getElement(idmax, "id")) + 1; // max id + 1 => unique id
	}
	
	// generate a unique customer ID
	public int generateCustomerId() throws Exception {
		DBCollection coll = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		BasicDBObject sorted  = new BasicDBObject();
		query.put("id", 1); // selection all id of the collection
		sorted.put("id",-1); // sort by "id" descending
		// find all ids; sort its and get the max one
		DBCursor idmax = coll.find(new BasicDBObject(), query).sort(sorted).limit(1);
		
		//throw new Exception (getIntElement(idmax, "id"));
		
		return getIntElement(idmax, "id") + 1; // max id + 1 => unique id
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
	
	// same for integer values
	public int getIntElement(DBCursor cursor, String key) throws Exception {
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		
		JsonNode json = mapper.readValue(cursor.next().toString(), JsonNode.class);
		return json.findPath(key).getIntValue();
	}
}
