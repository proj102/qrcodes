package models;

import java.lang.Exception;
import java.util.*;

// To process strings
import org.apache.commons.lang3.StringUtils;

// For Json processing
import play.libs.Json;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;

// For MongoDB operations
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import org.bson.types.ObjectId;
import com.mongodb.MongoException;

import controllers.Login;


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
	
	// Return the database if it has already been created
	// and if not create a new one
	public static MonDataBase getInstance() {
		if (instance == null)
			instance = new MonDataBase();
		return instance;
	}
	
	// connection to the database
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
	public String getUrl(String id) throws Exception {
		DBCollection coll = db.getCollection("qrcodes");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBCursor data = coll.find(query);
		
		if (data.size() == 0)
			throw new QrCodeException("QrCode does not exist.");
		
		DBObject currentQr = data.next();
		
		// if no exceptions have been thrown, we can return the redirection url
		// and increment the number of flashs for this qrCode
		
		int newNumber = getIntElement(currentQr, "flashs") + 1;
		BasicDBObject newQrDoc = new BasicDBObject().append("$set", new BasicDBObject().append("flashs", newNumber));
		coll.update(new BasicDBObject().append("_id", new ObjectId(id)), newQrDoc);
		
		return getElement(currentQr, "redirection");
	}
	
	// testMethod
	public boolean isConnected() {
		return authentification;
	}
	
	// add a customer to the db after checking the validity of
	// the informations given and after generating a unique ID
	public void addCustomer(String login, String password, String mail, String name, String firstname, String firm) throws Exception {
		// let's check the validity of their validity
		DBCollection coll = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("login", login);
		DBCursor data  = coll.find(query);
		if (data.count() != 0)
			throw new LoginException("Login already used.");
		
		// crypt the password in SHA-1
		password = Utils.sha1Encrypt(password);
		
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
	// return the id of the qrcode
	public String insertQr(BasicDBObject qrInfos) throws Exception {
		// get the id of the customer currently connected
		int customerId = Login.getConnected();
		
		if (customerId == -1)
			throw new CustomerException("You must be connected.");
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", customerId);
		DBCursor data  = customers.find(query);
			
		// everything is fine
		try {
			// let's add the qrcode to the qrcodes collection
			DBCollection qrs = db.getCollection("qrcodes");
			//int qrId = generateIdQRCode();
			
			//qrInfos.put("id", qrId);
			qrInfos.put("creation", System.currentTimeMillis());
			qrInfos.put("flashs", 0);
			qrInfos.put("active", 1); // active = 1 => qrcode active
			qrs.insert(qrInfos);
			// get the id the the qrcode we just created
			String qrId = qrInfos.get("_id").toString();
			
			// let's add the qr id to the customer's qrs
			String custQrs = getElement(data.next(), "qrs");
			
			if (custQrs.equals("[]"))
				custQrs = "[" + qrId + "]";
			else
				custQrs = custQrs.substring(0, custQrs.length() - 1) + "," + qrId + "]";
			
			BasicDBObject newCustDoc = new BasicDBObject().append("$set", new BasicDBObject().append("qrs", custQrs));
 
			customers.update(new BasicDBObject().append("id", customerId), newCustDoc);
			
			return qrId;
		}
		catch (Exception e) {
			throw new Exception(e.toString());
		}
	}
	
	// add a Qr with the data given in the "generate a Qrcode" form
	public String addQrFromForm(String type, String redirection, String title, String place) throws Exception {
		if (redirection == null || redirection == "")	
			throw new CustomerException("You must specify a redirection url.");
		
		BasicDBObject qrInfos = new BasicDBObject();
		qrInfos.put("type", type);
		qrInfos.put("redirection", redirection);
		qrInfos.put("title", title);
		qrInfos.put("place", place);
		
		return insertQr(qrInfos);
	}
	
	// try to log-in the customer : return the customer's id if 
	// the connection is successfull, -1 otherwise
	public int connection(String login, String password) throws Exception {
		DBCollection customers = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("login", login);
		DBCursor data  = customers.find(query);
		
		if (data.size() == 0)
			throw new LoginException();
		else {
			DBObject cust = data.next();
			String passSha1 = Utils.sha1Encrypt(password);
			String passCustomerSha1 = getElement(cust, "password");
			
			if (passSha1.equals(passCustomerSha1))
				return getIntElement(cust, "id");
			else
				throw new PasswordException();
		}
	}
	
	// check that the given customer still exists
	public int custExists(int id) {
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", id);
		DBCursor customer = customers.find(query);
		
		if (customer.size() == 0)
			return -1;
		
		return id;
	}
	
	// get all the qrcodes of the customer, parse them and return them in an ArrayList
	public ArrayList<Qrcode> getCustomersQrs() throws Exception {
		int customerId = Login.getConnected();
		
		if (customerId == -1)
			throw new CustomerException("You must be connected.");
			
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", customerId);
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		custQrs = custQrs.substring(1, custQrs.length()-1);
		String[] qrsIds = StringUtils.split(custQrs, ",");
		
		ArrayList<Qrcode> ret = new ArrayList<Qrcode>();
		DBCollection collQrs = db.getCollection("qrcodes");
		for (int i = 0 ; i < qrsIds.length ; i++) {
			//int qrId = Integer.parseInt(qrsIds[i]);
			String qrId = qrsIds[i];
			query = new BasicDBObject();
			query.put("_id", new ObjectId(qrId));
			DBObject currentQr = collQrs.findOne(query);
			
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			JsonNode json = mapper.readValue(currentQr.toString(), JsonNode.class);
			
			ret.add(new Qrcode(
				qrId,
				json.findPath("redirection").getTextValue(),
				json.findPath("type").getTextValue(),
				json.findPath("title").getTextValue(),
				json.findPath("place").getTextValue(),
				json.findPath("creation").getLongValue(),
				json.findPath("flashs").getIntValue()
			));
		}
		
		return ret;
	}
	
	public Qrcode getQrCode(String id) throws Exception {
		int customerId = Login.getConnected();
		
		if (customerId == -1)
			throw new CustomerException("You are not connected.");
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("id", customerId);
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		
		// throw an exception if the qrCode does not belong to the customer
		if (custQrs.indexOf(String.valueOf(id)) == -1)
			throw new QrCodeException("This QrCode does not exist.");
	
		DBCollection collQrs = db.getCollection("qrcodes");
		query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBObject currentQr = collQrs.findOne(query);
		
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		JsonNode json = mapper.readValue(currentQr.toString(), JsonNode.class);
		
		return new Qrcode(
			id,
			json.findPath("redirection").getTextValue(),
			json.findPath("type").getTextValue(),
			json.findPath("title").getTextValue(),
			json.findPath("place").getTextValue(),
			json.findPath("creation").getLongValue(),
			json.findPath("flashs").getIntValue()
		);
	}
	
	// Generation unique QRID 
	/*public int generateIdQRCode() throws Exception {
		DBCollection coll = db.getCollection("qrcodes");
		BasicDBObject query  = new BasicDBObject();
		BasicDBObject sorted  = new BasicDBObject();
		query.put("id", 1); // selection all id of the collection
		sorted.put("id",-1); // sort by "id" descending
		// find all ids; sort its and get the max one
		DBCursor searchMaxId = coll.find(new BasicDBObject(), query);
		if (searchMaxId.size() == 0)
			return 0;
		else {
			DBCursor idmax = searchMaxId.sort(sorted).limit(1);
			//throw new Exception("qrid : " + getElement(idmax, "id"));
			return getIntElement(idmax.next(), "id") + 1; // max id + 1 => unique id
		}
	}*/
	
	// generate a unique customer ID
	public int generateCustomerId() throws Exception {
		DBCollection coll = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		BasicDBObject sorted  = new BasicDBObject();
		query.put("id", 1); // selection all id of the collection
		sorted.put("id",-1); // sort by "id" descending
		// find all ids; sort its and get the max one
		DBCursor searchMaxId = coll.find(new BasicDBObject(), query);
		if (searchMaxId.size() == 0)
			return 0;
		else {
			DBCursor idmax = searchMaxId.sort(sorted).limit(1);
			return getIntElement(idmax.next(), "id") + 1; // max id + 1 => unique id
		}
	}


	// Decode JSON in DBCursor and get the "key" element
	public String getElement(DBObject obj, String key){
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		try {
			JsonNode json = mapper.readValue(obj.toString(), JsonNode.class);
			return json.findPath(key).getTextValue();
		}
		catch(Exception e) {
			return "problem decode Json : " + e;
		}
	}
	
	// same for integer values
	public int getIntElement(DBObject obj, String key) throws Exception {
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		
		JsonNode json = mapper.readValue(obj.toString(), JsonNode.class);
		return json.findPath(key).getIntValue();
	}

	// check if value ok the key already in collection
        public boolean isCreated(String key, Object value, String collection){
		DBCollection coll = db.getCollection(collection);
                BasicDBObject query = new BasicDBObject();
                query.put(key, value.toString());
		// if no object matching query in coll
		// then json document doesn't exist
                if (coll.findOne(query) == null)
                        return false;
                return true;
        }

	// remove qrcode : set active field to '0'
	public void removeQRCode(String id) throws MongoException {
		DBCollection coll = db.getCollection("qrcodes");
		BasicDBObject query = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		update.put("$set",new BasicDBObject("active", 0));

		coll.update(query, update);
	}

	public void removeQRCode(String[] id) throws MongoException {
		for (String i: id)
			removeQRCode(i);	
	}
}
