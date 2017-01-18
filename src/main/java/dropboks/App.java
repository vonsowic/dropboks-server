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

        // enable https
        //secure("deploy/keystore.jks", "password", null, null);
		port(4567);

        //before(FILES_PATH + "/*/*", controller::authenticate);

        get(FILES_PATH + "/*/list_folder_content", controller::getListFolderContent,json);

        post(FILES_PATH + "/upload", controller::uploadFile,json);

        get(FILES_PATH + "/*/download", controller::download, json);

        get(FILES_PATH + "/*/get_meta_data", controller::getMetaData, json);

        put(FILES_PATH + "/*/rename", controller::rename, json);

        delete(FILES_PATH + "/*/delete", controller::remove, json);

        put(FILES_PATH + "/*/move", controller::move, json);

        post(FILES_PATH + "/*/create_directory", controller::createDirectory, json);

        post(USERS_PATH + "/create_user", controller::createNewUser, json);

        get(USERS_PATH + "/access", controller::access, json);

	}

    public static String createHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}


