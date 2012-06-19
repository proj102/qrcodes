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
	
	public static Result db() {
		MonDataBase db = MonDataBase.getInstance();
		
		if (db.isConnected()) {
			
			String url = db.getInformations();
			
			if (url == null)
				return badRequest("problem");
			else {
				return redirect(url);
			}
		}
		else
			return badRequest("DB : connexion failed.");
	}
  
	public static Result pages(int id) {
		String redirectionPage;
		
		switch (id) {
			case 0:
				redirectionPage = "http://www.google.com";
				break;
			case 1:
				redirectionPage = "http://youtube.com";
				break;
			case 2:
				redirectionPage = "http://twitter.com";
				break;
			default :
				redirectionPage = "http://eole.enst.fr";
		}
		
		return redirect(redirectionPage);
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result sayHello() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		
		String name = json.findPath("name").getTextValue();
		
		if (name == null) {
			result.put("status", "KO");
			result.put("message", "Missing parameter [name]");
			return badRequest(result);
		}
		else {
			result.put("status", "OK");
			result.put("message", "Hello " + name);
			return ok(result);
		}
	}
  
}