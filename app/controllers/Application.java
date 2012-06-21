package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import views.html.*;
import models.*;


import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import play.data.*;
import play.data.validation.Constraints.*;
import java.util.*;



public class Application extends Controller {
	private static String domain = "http://qrteam.herokuapp.com/"; 
 
	public static Result index() {
		return ok(views.html.index.render(urlForm));
	}

	public static Result redirection( String id ) {
		MonDataBase db = MonDataBase.getInstance();

		try {
			return redirect(db.getUrl(id));
		}
		catch (Exception e) {
			return badRequest(e.toString());
		}
	}

	// Test genération unique ID
	public static Result testsort() {	
		MonDataBase db = MonDataBase.getInstance();
		int v = db.generateIdQRCode();
		return ok(String.valueOf(v));
	}
	
	public static Result addC() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			db.addCustomer("plequen", "test2", "plequen00@gmail.com", "Plessis", "Quentin", "Telecom");
		
			return ok("Customer added successfully !");
		}
		catch (Exception e) {
			return badRequest(e.toString());
		}
	}
	
	
	public static Result addQ() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			db.addQr(10, "url", "http://google.com", "Titre", "lieu");
		
			return ok("Qrcode added successfully !");
		}
		catch (Exception e) {
			return badRequest(e.toString());
		}
	}
	
	public static Result testP() {
		MonDataBase db = MonDataBase.getInstance();
		
		return ok(db.testPass());
	}

	//Get Url Form data
	static Form<Url> urlForm = form(Url.class);	

	// generate QRcode from url
	public static Result getUrl() {
		MonDataBase db = MonDataBase.getInstance();
		
        	Form<Url> form = form(Url.class).bindFromRequest();
		if(form.hasErrors())
			return badRequest(index.render(urlForm));
		else {
			Url data = form.get();
			db.insert(data.url);
			return ok(qrGenerator.render(domain + "r/1209fe7"));
		}
	}
}


