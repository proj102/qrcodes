package controllers;

import java.lang.Exception;
import java.util.*;

//import play.api.libs.concurrent.Promise;
import play.libs.F.Promise;

import play.libs.Json;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;
import views.html.signup.*;

import models.*;

import play.mvc.Http.Request;
import play.mvc.Http.Cookie;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;

public class Login extends Controller {
    
	public static int sessionDuration = 3600*24*30;
	
	static Form<Client> loginForm = form(Client.class);
	

	
	@SuppressWarnings("serial")
	public static final Map<String, String> identifiers = new HashMap<String, String>() {
		{
			put("google", "https://www.google.com/accounts/o8/id");
		}
	};

	public static Result auth() {
		Logger.debug("authenticate");
		String providerId = "google";
		String providerUrl = identifiers.get(providerId);
		String returnToUrl = "http://localhost:9000/login/verify";
		//"http://localhost:9000/login/verify";
		// "https://qrteam.herokuapp.com/login/verify"

		if (providerUrl == null)
			return badRequest(index.render(Application.urlForm, loginForm, InfoDisplay.ERROR, "Provider could not be found."));

		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("Email", "http://schema.openid.net/contact/email");
		attributes.put("FirstName", "http://schema.openid.net/namePerson/first");
		attributes.put("LastName", "http://schema.openid.net/namePerson/last");

		Promise<String> redirectUrl = OpenID.redirectURL(providerUrl, returnToUrl, attributes);
		return redirect(redirectUrl.get());
	}

	public static Result verify() {
		Logger.debug("verifyLogin");
		Promise<UserInfo> userInfoPromise = OpenID.verifiedId();
		UserInfo userInfo = userInfoPromise.get();
		JsonNode json = Json.toJson(userInfo);
		
		String email = json.findPath("Email").getTextValue();
		
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			String customerId = db.connection(email);
			
			if (customerId != null)
				response().setCookie("connected", customerId, sessionDuration);
			
			//flash("coco", "ok");
			
			return ok(index.render(Application.urlForm, loginForm, InfoDisplay.SUCCESS, "You are now connected."));					
		}
		catch (Exception e) {
			return badRequest(index.render(Application.urlForm, loginForm, InfoDisplay.ERROR, "Error when logging-in with Google. " + e));
		}
	}
	
	public static String getLogin() {
		try {
			String customerId = getConnected();
			
			if (customerId == null)
				return "Connected";
			else 
				return MonDataBase.getInstance().getLogin(customerId);
		}
		catch (Exception e) {
			return "Connected";
		}
	}
		
	
	public static String connection(String login, String password) throws Exception {
		MonDataBase db = MonDataBase.getInstance();
	
		String customerId = db.connection(login, password);
			
		if (customerId != null)
			response().setCookie("connected", customerId, sessionDuration);
			
		return customerId;
	}

	// test method
	public static Result isConnected() throws Exception{
		if(getConnected()!=null)	
			return ok("connect");
		else
			return ok("no");
	}
	
	public static String getConnected() throws Exception {
		MonDataBase db = MonDataBase.getInstance();
		Http.Cookie cookie = request().cookies().get("connected");
		
		if (cookie != null) {
			String custId = cookie.value();
			
			return db.custExists(custId);
		}
			
		return null;
	}
	
	public static Result logout() {
		response().discardCookies("connected");
		
		return ok(index.render(Application.urlForm, Login.loginForm, InfoDisplay.INFO, "You are now logged out."));
	}

	public static Result blank() {
		return ok(login.render(loginForm));
	}

	public static Result submit() {
			Form<Client> filledForm = loginForm.bindFromRequest();
			
			//Check in the database if login exist and if it matches with the password
			if(!filledForm.hasErrors()) {
				try {
					String checker = connection(filledForm.field("login").value(), filledForm.field("password").value());
					
					Client created = filledForm.get();
					return ok(index.render(Application.urlForm, filledForm, InfoDisplay.SUCCESS, "You are now connected."));					
				}
				catch (LoginException e) {
					filledForm.reject("login", "This login does not exist");
					return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. This login does not exist."));
				}
				catch (PasswordException e) {
					filledForm.reject("password", "The password does not match");
					return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. The password does not match."));
				}
				catch(Exception e) {
					return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. " + e));
				}
			}
			return badRequest(index.render(Application.urlForm, filledForm, InfoDisplay.ERROR, "Error when logging in. Please fill correctly all the fields."));
	}
}



