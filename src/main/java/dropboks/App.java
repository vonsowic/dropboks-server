package dropboks;

import static spark.Spark.*;

import dropboks.controllers.DropboksController;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.exceptions.PermissionException;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import spark.ResponseTransformer;

import javax.naming.AuthenticationException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class App {

    private static final int ALREADY_EXISTS = 400;
    private static final int AUTHENTICATION_REQUIRED = 401;
    private static final int UNSUCCESSFUL_LOGIN = 403;
    private static final int INVALID_PARAMETER = 405;
    private static final int NOT_FOUND = 404;


    final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");
	
	public static void main(String[] args) {

		final String USERS_PATH = "/users";
        final String FILES_PATH = "/files";

        final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;
        DropboksController controller = new DropboksController();

        Map<Class, Integer> exceptionMap = new HashMap<>();
        exceptionMap.put(InvalidParameterException.class, INVALID_PARAMETER);
        exceptionMap.put(AuthenticationException.class, UNSUCCESSFUL_LOGIN);
        exceptionMap.put(NoRecordForundInDatabaseException.class, INVALID_PARAMETER);
        exceptionMap.put(DataAccessException.class, NOT_FOUND);
        exceptionMap.put(AlreadyExistsException.class, ALREADY_EXISTS);
        exceptionMap.put(PermissionException.class, AUTHENTICATION_REQUIRED);
        exceptionMap.put(null, NOT_FOUND);



        // enable https
        secure("deploy/keystore.jks", "password", null, null);
		port(4567);

        //threadPool(10, 0, 30*1000);

        before(FILES_PATH + "/*", controller::authenticate);

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

        exception(Exception.class, (ex, request, response) -> {
            ex.printStackTrace();
            response.status(exceptionMap.get(ex.getClass()));
            response.body(gson.toJson(ex.getMessage()));
        });

    }
}


