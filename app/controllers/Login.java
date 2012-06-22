package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.*;

import play.mvc.Http.Request;
import play.mvc.Http.Cookie;

public class Login extends Controller {
    
	public static int sessionDuration = 3600*24*30;
	static Form<Client> loginForm = form(Client.class);
	
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

	public static Result blank() {
		return ok(login.render(loginForm));
	}

	public static Result submit() {
		Form<Client> filledForm = loginForm.bindFromRequest();

		//Check in the database if login exist and if it matches with the password
		if(!filledForm.hasErrors()) {
			try {
				int checker = connexion(filledForm.field("login").value(), filledForm.field("password").value());
				if (checker == -1)
					filledForm.reject("login", "This login does not exist");
				if (checker == -2)
					filledForm.reject("password", "The password does not match");
			} catch(Exception e) {
				return badRequest("problems"+e);
			}        
		}

        
		if(filledForm.hasErrors()) {
				return badRequest("probleme"+filledForm.getErrors().toString());
		} else {
			Client created = filledForm.get();
			return redirect("http://localhost:9000");
		}
	}
  
}



