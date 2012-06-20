package controllers;

import play.*;
import play.mvc.*;
import play.libs.Json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import views.html.*;
import models.*;

<<<<<<< HEAD
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import play.data.*;
import play.data.validation.Constraints.*;
import java.util.*;

=======
>>>>>>> 2fad6d887742e78a8d6b84fbaff5bef1ae0ab474

public class Application extends Controller {
  
	public static Result index() {
		return ok(views.html.index.render(urlForm));
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
<<<<<<< HEAD

	//Get Url Form data

	static Form<Url> urlForm = form(Url.class);	

	 public static Result getUrl() {
        Form<Url> form = form(Url.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(index.render(urlForm));
        } else {
            Url data = form.get();
            return ok(
                qrGenerator.render(data.url)
            );
        }
    }
  
=======
>>>>>>> 2fad6d887742e78a8d6b84fbaff5bef1ae0ab474
}


