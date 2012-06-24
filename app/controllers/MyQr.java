package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import views.html.*;
import models.*;

import play.data.*;
import play.data.validation.Constraints.*;
import java.util.*;
import java.io.File;
import play.mvc.Http.MultipartFormData.*;
import play.mvc.Http.MultipartFormData;


public class MyQr extends Controller {
	
	public static Result viewQr(int id) {
		MonDataBase db = MonDataBase.getInstance();
		
		try {
			Qrcode qr = db.getQrCode(id);
			
			return ok(myQr.render(qr, Login.loginForm, InfoDisplay.NONE, null));
		}
		catch (Exception e) {
			try {
				return ok(myQrTable.render(Login.loginForm, db.getCustomersQrs(), InfoDisplay.ERROR, "Cannot view this QrCode. " + e));
			}
			catch (Exception f) {
				return badRequest(myQrTable.render(Login.loginForm, new ArrayList<Qrcode>(), InfoDisplay.ERROR, "Impossible to get your Qrcodes." + f));
			}
		}
	}
	
	public static Result submit() {
		//Form<User> filledForm = signupForm.bindFromRequest();
		
		return badRequest("To be implemented.");
	}
	
}

