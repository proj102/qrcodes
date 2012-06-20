package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import views.html.*;
import models.*;


public class Application extends Controller {
  
	public static Result index() {
		return ok("Hello world");
	}

	public static Result redirection( String id ) {
		MonDataBase db = MonDataBase.getInstance();

		return redirect(db.getUrl(id)); 
	}

	public static Result testinsert(String url) {	
		MonDataBase db = MonDataBase.getInstance();
		db.insert(url);
		return ok("Qr code ready");
	}
}


