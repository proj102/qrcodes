package models;

import java.lang.Exception;
import java.lang.Math;
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
import controllers.QrTable;


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
	public String getUrl(String id, String userAgent) throws Exception {
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
		
		// add this flash to the logs
		try {
			DBCollection logsColl = db.getCollection("logs");
			BasicDBObject logInfo  = new BasicDBObject();
			logInfo.put("qrId", id);
			logInfo.put("time", System.currentTimeMillis());
			logInfo.put("userAgent", userAgent);
			logsColl.insert(logInfo);
		}
		catch (Exception e) {
			// error when adding log : can't disturb customer though
		}
		
		return getElement(currentQr, "redirection");
	}
	
	// testMethod
	public boolean isConnected() {
		return authentification;
	}
	
	// add a customer that registers via a provider
	public String addCustomer(String mail, String lastName, String firstName) throws Exception {
		DBCollection coll = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("mail", mail);
		DBCursor data  = coll.find(query);
		if (data.count() != 0)
			throw new CustomerException("Account already existing.");
		
		// add the customer
		BasicDBObject customerInfos = new BasicDBObject();
		customerInfos.put("login", firstName + " " + lastName);
		customerInfos.put("mail", mail);
		customerInfos.put("inscription", System.currentTimeMillis());
		customerInfos.put("name", lastName);
		customerInfos.put("firstname", firstName);
		customerInfos.put("avatar", "");
		customerInfos.put("qrs", "[]");
		coll.insert(customerInfos);
		
		return customerInfos.get("_id").toString();
	}
	
	// insert a qrcode in the database with the given json content
	// to which is added significant fields such as the id of the qrcode
	// return the id of the qrcode
	public String insertQr(BasicDBObject qrInfos) throws Exception {
		// get the id of the customer currently connected
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBObject data  = customers.findOne(query);
			
		// everything is fine
		try {
			// let's add the qrcode to the qrcodes collection
			DBCollection qrs = db.getCollection("qrcodes");
			
			qrInfos.put("creation", System.currentTimeMillis());
			qrInfos.put("flashs", 0);
			qrInfos.put("active", 1); // active = 1 => qrcode active
			//qrInfos.put("stats", stats);
			qrs.insert(qrInfos);
			// get the id the the qrcode we just created
			String qrId = qrInfos.get("_id").toString();
			
			// let's add the qr id to the customer's qrs
			String custQrs = getElement(data, "qrs");
			
			if (custQrs.equals("[]"))
				custQrs = "[" + qrId + "]";
			else
				custQrs = custQrs.substring(0, custQrs.length() - 1) + "," + qrId + "]";
			
			BasicDBObject newCustDoc = new BasicDBObject().append("$set", new BasicDBObject().append("qrs", custQrs));
			customers.update(new BasicDBObject().append("_id", new ObjectId(customerId)), newCustDoc);
			
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
	
	public Stats getQrStats(String id) throws Exception {
		// check we ca see the stats
		if (!isQrMine(id))
			throw new QrCodeException("This QrCode does not exist.");
		
		// logs
		DBCollection logs = db.getCollection("logs");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("qrId", id);
		BasicDBObject sortQuery  = new BasicDBObject();
		sortQuery.put("time", -1);
		DBCursor data  = logs.find(query).sort(sortQuery);
		
	
		if (data.size() == 0)
			throw new QrCodeException("No statistics available.");
		
		Stats ret = new Stats();
		
		long currentTime = System.currentTimeMillis();
		long currentDay = currentTime / (1000*3600*24);
		long currentMonth = currentDay / 31;
		
		while (data.hasNext()) {
			DBObject log = data.next();
			
			long logTime = getLongElement(log, "time");
			long logDay = logTime / (1000*3600*24);
			long logMonth = logDay / 31;

			String userAgent = getElement(log, "userAgent");
			ret.addToUserAgent(userAgent);
	
			if (currentDay - logDay < 32) {
				ret.addToMonth((int) (currentDay - logDay));
				ret.addToYear(0);
			}
			else if (currentMonth - logMonth < 13)
				ret.addToYear((int) (currentMonth - logMonth));
			else
				break;
		}
		
		return ret;
	}
	
	// connection with email (OpenId with google)
	public String connection(String email) throws Exception {
		DBCollection customers = db.getCollection("customers");
		
		BasicDBObject query  = new BasicDBObject();
		query.put("mail", email);
		DBCursor data  = customers.find(query);
		
		if (data.size() == 0)
			throw new CustomerException("No account with this email.");
		else {
			DBObject cust = data.next();
			return cust.get("_id").toString();
		}
	}
	
	public String getLogin(String customerId) throws Exception {
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBObject data  = customers.findOne(query);
		
		if (data == null)
			throw new CustomerException("Unknown customer.");
		
		return getElement(data, "login");
	}
	
	// check that the given customer still exists
	public String custExists(String id) {
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBCursor customer = customers.find(query);
		
		if (customer.size() == 0)
			return null;
		
		return id;
	}
	
	// get all the qrcodes of the customer, parse them and return them in an ArrayList
	public ArrayList<Qrcode> getCustomersQrs() throws Exception {
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		custQrs = custQrs.substring(1, custQrs.length()-1);
		String[] qrsIds = StringUtils.split(custQrs, ",");
		
		ArrayList<Qrcode> ret = new ArrayList<Qrcode>();
		DBCollection collQrs = db.getCollection("qrcodes");
		for (int i = 0 ; i < qrsIds.length ; i++) {
			String qrId = qrsIds[i];
			query = new BasicDBObject();
			query.put("_id", new ObjectId(qrId));
			DBObject currentQr = collQrs.findOne(query);
			
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			JsonNode json = mapper.readValue(currentQr.toString(), JsonNode.class);
			
			if (json.findPath("active").getIntValue() == 1) {
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
		}
		
		return ret;
	}
	
	// get only the qrcodes of the given page : if page=0, return the first qrs
	public QrArray getCustomersQrs(int page) throws Exception {
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		custQrs = custQrs.substring(1, custQrs.length()-1);
		String[] qrsIds = StringUtils.split(custQrs, ",");
		
		QrArray ret = new QrArray();
		DBCollection collQrs = db.getCollection("qrcodes");
		int j = 0;
		for (int i = 0 ; i < qrsIds.length ; i++) {
			String qrId = qrsIds[i];
			query = new BasicDBObject();
			query.put("_id", new ObjectId(qrId));
			DBObject currentQr = collQrs.findOne(query);
			
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			JsonNode json = mapper.readValue(currentQr.toString(), JsonNode.class);
			
			if (json.findPath("active").getIntValue() == 1) {
				if (j >= page*QrTable.qrPerPage && ret.size() < QrTable.qrPerPage) {
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
				j++;
			}
		}
		ret.setNumber(j);
		
		return ret;
	}
	
	public DBObject getInfoCustomer(String customerId) {
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		return customer.next();
	}
	
	public Qrcode getQrCode(String id) throws Exception {
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		
		// throw an exception if the qrCode does not belong to the customer
		if (custQrs.indexOf(id) == -1)
			throw new QrCodeException("This QrCode does not exist.");
	
		DBCollection collQrs = db.getCollection("qrcodes");
		query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		DBObject currentQr = collQrs.findOne(query);
		
		if (getIntElement(currentQr, "active") == 1) {
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
		else
			throw new QrCodeException("This QrCode does not exist.");
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
	
	public long getLongElement(DBObject obj, String key) throws Exception {
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		
		JsonNode json = mapper.readValue(obj.toString(), JsonNode.class);
		return json.findPath(key).getLongValue();
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
		
	public boolean isQrMine(String id) throws Exception {
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		
		// throw an exception if the qrCode does not belong to the customer
		if (custQrs.indexOf(id) == -1)
			return false;
		return true;
	}

	// remove qrcode : set active field to '0'
	public void removeQRCode(String id) throws Exception {
		if (!isQrMine(id))
			throw new QrCodeException("This QrCode does not exist.");
		
		DBCollection coll = db.getCollection("qrcodes");
		BasicDBObject query = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		update.put("$set",new BasicDBObject("active", 0));

		coll.update(query, update);
	}

	public void removeQRCode(String[] id) throws Exception {
		for (String i: id)
			removeQRCode(i);	
	}
	
	public void removeQrCode(ArrayList<String> ids) throws Exception {
		String customerId = Login.getConnected();
		
		DBCollection customers = db.getCollection("customers");
		BasicDBObject query  = new BasicDBObject();
		query.put("_id", new ObjectId(customerId));
		DBCursor customer = customers.find(query);
		
		String custQrs = getElement(customer.next(), "qrs");
		DBCollection coll = db.getCollection("qrcodes");
		
		for (String i : ids) {
			if (custQrs.indexOf(i) != -1) {
				BasicDBObject query2 = new BasicDBObject();
				BasicDBObject update = new BasicDBObject();
				query2.put("_id", new ObjectId(i));
				update.put("$set", new BasicDBObject("active", 0));

				coll.update(query2, update);
			}
		}
	}

	public void updateQRCode(HashMap<String, String> map) throws MongoException {
		DBCollection coll = db.getCollection("qrcodes");
		updateDocument(map, coll);
	/*	BasicDBObject query = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();
		query.put("_id", new ObjectId(map.get("id")));
		for (Map.Entry<String, String> m : map.entrySet()){
			update.put("$set",new BasicDBObject(m.getKey(),m.getValue()));
			coll.update(query, update);
		}*/
	}

	public void updateCustomer(HashMap<String, String> map) throws MongoException{
		DBCollection coll = db.getCollection("customers");
		updateDocument(map, coll);
	}

	public void updateDocument(HashMap<String, String> map, DBCollection coll) throws MongoException{
                BasicDBObject query = new BasicDBObject();
                BasicDBObject update = new BasicDBObject();
                query.put("_id", new ObjectId(map.get("id")));
                for (Map.Entry<String, String> m : map.entrySet()){
                        update.put("$set",new BasicDBObject(m.getKey(),m.getValue()));
                        coll.update(query, update);
                }
	}
}
