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
	
     public static int connexion(String login, String password) throws Exception {
		MonDataBase db = MonDataBase.getInstance();
		
		int customerId = db.connexion(login, password);
			
		if (customerId >= 0)
			response().setCookie("connected", String.valueOf(customerId), sessionDuration);
			
		return customerId;
	}
	
	public static int getConnected() throws Exception {
		Http.Cookie cookie = request().cookies().get("connected");
		
		if (cookie != null)
			return Integer.parseInt(cookie.value());
			
		return -1;
	}
	
	public static void deconnexion() {
		response().discardCookies("connected");
	}
  
}



