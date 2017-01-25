package dropboks;

import static spark.Spark.*;

import dropboks.controllers.DropboksController;

import com.google.gson.Gson;
import spark.ResponseTransformer;


public class App {

    final String USERS_PATH = "/users";
    final String FILES_PATH = "/files";

    final Gson gson = new Gson();
    final ResponseTransformer json = gson::toJson;
    final DropboksController controller = new DropboksController();

	public static void main(String[] args) {
        new App().initialize(4567, true);
    }

    public void initialize(){
        this.initialize(4567, true);
    }

    public void initialize(Integer port, boolean enableHttps){
        // enable https
        if ( enableHttps) {
            secure("deploy/keystore.jks", "password", null, null);
            port(port);
        }

        before(FILES_PATH + "/*" , controller::authenticate);

        get(FILES_PATH + "/*/list_folder_content" , controller::getListFolderContent,json);

        post(FILES_PATH + "/upload" , controller::uploadFile,json);

        get(FILES_PATH + "/*/download", controller::download, json);

        get(FILES_PATH + "/*/get_meta_data" , controller::getMetaData, json);

        put(FILES_PATH + "/*/rename" , controller::rename, json);

        delete(FILES_PATH + "/*/delete" , controller::remove, json);

        put(FILES_PATH + "/*/move" , controller::move, json);

        post(FILES_PATH + "/*/create_directory" , controller::createDirectory, json);

        post(USERS_PATH + "/create_user" , controller::createNewUser, json);

        get(USERS_PATH + "/access" , controller::access, json);

        post(USERS_PATH + "/logout" , controller::logout, json);
    }
}


