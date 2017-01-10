package dropboks;

import static spark.Spark.*;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import spark.Request;
import spark.ResponseTransformer;

public class App {

	final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");
	
	public static void main(String[] args) {

		final String USERS_PATH = "/users";
        final String FILES_PATH = "/files";

        final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;

        DropboksController controller = new DropboksController();

		port(4567);

        before(controller::authenticate);

        get(FILES_PATH + "/*/list_folder_content", (request,response) -> null,json);

        post(FILES_PATH + "/upload", controller::uploadFile,json);

        get(FILES_PATH + "/*/download", ((request, response) -> null));

        get(FILES_PATH + "/*/get_meta_data", (controller::getMetaData));

        put(FILES_PATH + "/*/rename", (controller::renameFile));

        delete(FILES_PATH + "/*/delete", (controller::remove));

        put(FILES_PATH + "/*/move", ((request, response) -> null));

        put(FILES_PATH + "/*/create_directory", (controller::createDirectory));

        post(USERS_PATH + "/create_user", controller::createNewUser, json);

        get(USERS_PATH + "/access", (request,response) -> null,json);
	}



    private static void info(Request req) {
		LOGGER.info("{}",req);
	}

    public static String createHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}


