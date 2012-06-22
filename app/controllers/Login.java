package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.signup.*;

import models.*;

import play.mvc.Http.Request;
import play.mvc.Http.Cookie;

public class Login extends Controller {
    
	public static int sessionDuration = 3600*24*30;
	
    public static Result login(String login, String password) {
		
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			int customerId = db.connexion(login, password);
			
			response().setCookie("connected", String.valueOf(customerId), sessionDuration);
			
			return ok("You are now logged !");
		}
		catch (Exception e) {
			return badRequest("Error when logging : " + e);
		}
	}
	
	public static int getConnected() throws Exception {
		Http.Cookie cookie = request().cookies().get("connected");
		
		if (cookie != null)
			return Integer.parseInt(cookie.value());
			
		throw new Exception("not connected.");
	}
	
	public static Result logout() {
		response().discardCookies("connected");
		
		return ok("You are now deconnected");
	}
  
}



