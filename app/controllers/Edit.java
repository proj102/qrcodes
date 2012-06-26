package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.data.*;

import views.html.*;

import models.*;

public class Edit extends Controller {

	public static Result edit() {
		CustomerSession custSession = Login.getCustSession();
		custSession.getInfo();
		return ok(edit.render(custSession, InfoDisplay.NONE, null));
	}

}
