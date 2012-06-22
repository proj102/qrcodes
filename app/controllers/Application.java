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
import com.mongodb.BasicDBObject;

import play.data.*;
import play.data.validation.Constraints.*;
import java.util.*;
import java.io.File;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Http.MultipartFormData;




public class Application extends Controller {
	private static String domain = "http://qrteam.herokuapp.com/"; 
 
	public static Result index() {
		return ok(views.html.index.render(urlForm, Login.loginForm));
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
			int qrId = db.addQrFromForm("url", "http://google.com", "Titre", "lieu");
		
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

	//redirection to Web pages of the website
	public static Result myQrTable() {
		return ok(myQrTable.render(Login.loginForm));
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
			return badRequest(index.render(urlForm, Login.loginForm));
		else {
			Url data = form.get();
			try {
				int qrId = db.addQrFromForm("url", data.url, "titre", "lieu");
				return ok(qrGenerator.render(domain + "r/" + qrId, Login.loginForm));
			}
			catch (Exception e) {
				return badRequest("error when adding qr code to db : " + e);
			}
		}
	}

	// Test methode
	// insertion of all elements from a csv document and save in db
	public static Result test(String key){
		MonDataBase db = MonDataBase.getInstance();

		// this variable will be send from the view
		HashMap<String, String> mapTitles = new HashMap<String, String>();
		mapTitles.put("Société", "societe");
                mapTitles.put("Secteur", "secteur");
                mapTitles.put("Poids indiciel", "poids");
                mapTitles.put("Rang mondial", "rang");
                mapTitles.put("Chiffre d'affaires", "ca");
                mapTitles.put("Capitalisation boursière", "capital");
                mapTitles.put("redirection", "redirection");
                mapTitles.put("Résultat net", "net");

		ArrayList<BasicDBObject> json = new ArrayList<BasicDBObject>();
		try {
			// get array of json document from csv file
			json = Utils.parseCSV("./doc/tab.csv", mapTitles, ";");
			
			// insertion in db only if document doesn't exist in collection
			for (BasicDBObject j : json){
				if ( !db.isCreated(key, j.get(key), "qrcodes") ) 
					db.insertQr(j);
			}
			return ok ("insertion csv fini");
		}
		catch (Exception e){
			return badRequest("Error" + e);
		}
	}
}


