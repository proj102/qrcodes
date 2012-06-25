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

	static Form<Qrcode> qrForm = form(Qrcode.class);
	
	public static Result viewQr(String id) {
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

/**
  *TODO: Implement the MonDataBase method updateQrcode
 **/		
		Form<Qrcode> filledForm = qrForm.bindFromRequest();
		MonDataBase MongoDB = MonDataBase.getInstance();
		HashMap<String, String> hmap = new HashMap<String, String>();
		
		try{
			if(!filledForm.field("redirection").value().isEmpty() || !filledForm.field("title").value().isEmpty() || !filledForm.field("place").value().isEmpty()){
				hmap.put("id",filledForm.field("id").value());
				// Insert the redirection field if not empty
				if(!filledForm.field("redirection").value().isEmpty()) {
					hmap.put("redirection", filledForm.field("redirection").value());
				}

				// Insert the title field if not empty
				if(!filledForm.field("title").value().isEmpty()) {
					hmap.put("title", filledForm.field("title").value());
				}

				// Insert the place field if not empty
				if(!filledForm.field("place").value().isEmpty()) {
					hmap.put("place", filledForm.field("place").value());
				}

		
				MongoDB.updateQRCode(hmap);
				Qrcode qr = MongoDB.getQrCode(filledForm.field("id").value());
				return ok(myQr.render(qr, Login.loginForm, InfoDisplay.SUCCESS, "Your changes have been updated."));
			} else {
				Qrcode qr = MongoDB.getQrCode(filledForm.field("id").value());
				return ok(myQr.render(qr, Login.loginForm, InfoDisplay.ERROR, "Please fill at least one field."));
			}
		}catch(Exception e){
			return badRequest("not implemented");
		}
	}	/*	
		return badRequest("not implemented");
	}
	*/
}
