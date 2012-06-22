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

	public static Result addC() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			db.addCustomer("plequen2", "test2", "plequen00@gmail.com", "Plessis", "Quentin", "Telecom");
		
			return ok("Customer added successfully !");
		}
		catch (Exception e) {
			return badRequest(e.toString());
		}
	}
	
	
	public static Result addQ() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			String qrId = db.addQrFromForm("url", "http://google.com", "Titre", "lieu");
		
			return ok("Qrcode added successfully ! Id : " + qrId);
		}
		catch (Exception e) {
			return badRequest(e.toString());
		}
	}
	
	public static Result testP() {
		MonDataBase db = MonDataBase.getInstance();
		
		return ok(db.testPass());
	}

	public static Result myQrTable() {
		return ok(myQrTable.render());
	}
	public static Result overview() {
		return ok(overview.render());
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
			try {
				String qrId = db.addQrFromForm("url", data.url, "titre", "lieu");
				return ok(qrGenerator.render(domain + "r/" + qrId));
			}
			catch (Exception e) {
				return badRequest("error when adding qr code to db : " + e);
			}
		}
	}
}


