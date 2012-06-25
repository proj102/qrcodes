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
	public final static String domain = "http://qrteam.herokuapp.com/"; 
 
	public static Result index() {
		return ok(index.render(urlForm, Login.loginForm, InfoDisplay.NONE, null));
	}

	// Manage the redirection
	public static Result redirection(int id) {
		MonDataBase db = MonDataBase.getInstance();

		try {
			return redirect(db.getUrl(id));
		}
		catch (Exception e) {
			return badRequest(index.render(urlForm, Login.loginForm, InfoDisplay.ERROR, "Redirection failed. Reason : " + e));
		}
	}

	// visualisation of the qrcodes of the customer
	public static Result myQrTable() {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			return ok(myQrTable.render(Login.loginForm, db.getCustomersQrs(), InfoDisplay.NONE, null));
		}
		catch (Exception e) {
			return badRequest(index.render(urlForm, Login.loginForm, InfoDisplay.ERROR, "Impossible to get the Qrcodes. " + e));
		}
	}
	
	public static Result overview() {
		return ok(overview.render(Login.loginForm));
	}

	//Get Url Form data
	static Form<Url> urlForm = form(Url.class);

	// generate QRcode from url
	public static Result getUrl() {
		MonDataBase db = MonDataBase.getInstance();
		
        Form<Url> form = form(Url.class).bindFromRequest();
		if(form.hasErrors())
			return badRequest(index.render(urlForm, Login.loginForm, 0, null));
		else {
			Url data = form.get();
			try {
				int qrId = db.addQrFromForm("url", data.url, "titre", "lieu");
				return ok(qrGenerator.render(domain + "r/" + qrId, Login.loginForm, InfoDisplay.SUCCESS, "You have successfully created a QrCode that redirects to " + data.url + " ."));
			}
			catch (Exception e) {
				return badRequest(index.render(urlForm, Login.loginForm, InfoDisplay.ERROR, "The QrCode could not be generated. " + e));
			}
		}
	}

	// Test methode
	// insertion of all elements from a csv document and save in db
	public static Result test(String key){
		MonDataBase db = MonDataBase.getInstance();

		db.removeQRCode(key);
		return ok("fini");

		/*// this variable will be send from the view
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
		}*/
	}
}


