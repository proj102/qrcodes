package controllers;

import java.lang.Exception;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.signup.*;
import views.html.*;

import models.*;

import play.mvc.Http.Request;
import play.mvc.Http.Cookie;

public class Login extends Controller {
    
	public static int sessionDuration = 3600*24*30;
	final static Form<Client> loginForm = form(Client.class);
	
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
			
		return -1;
	}
	
	public static Result logout() {
		response().discardCookies("connected");
		
		return ok("You are now deconnected");
	}

	public static Result submit() {
		Form<Client> filledForm = signupForm.bindFromRequest();

		//Check in the database if login exist and if it matches with the password
		if(!filledForm.hasErrors()) {
			try {
				int checker = login(filledForm.field("login").value(), filledForm.field("password"));
				if (checker == -1)
					filledForm.reject("login", "This login does not exist");
				if (checker == -2)
					filledForm.reject("password", "The password does not match");
			} catch(Exception e) {
				return badRequest(form.render(filledForm));
			}        
		}

        
		if(filledForm.hasErrors()) {
			return badRequest(form.render(filledForm));
		} else {
			Client created = filledForm.get();
			return ok(index.render("You're logged!"));
		}
	}
  
}



