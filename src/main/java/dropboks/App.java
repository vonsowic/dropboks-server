package dropboks;

import static pl.edu.agh.kis.florist.db.Tables.*;
import static spark.Spark.*;

import java.util.List;

import dropboks.dao.DirectoryMetadataDAO;
import dropboks.dao.UsersDAO;
import dropboks.model.User;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.db.tables.pojos.FileContents;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderFileContents;
import pl.edu.agh.kis.florist.db.tables.records.FileContentsRecord;
import spark.Request;
import spark.ResponseTransformer;

public class App {

	final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");
	
	public static void main(String[] args) {
		final int CREATED = 201;
		final String USERS_PATH = "/users";

		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;
		final String DB_URL = "jdbc:sqlite:test.db";

        DropboksController controller = new DropboksController(new UsersDAO(), new DirectoryMetadataDAO());

		
		port(4567);


        before((request, response) -> {
            boolean authenticated = true;
            // ... check if authenticated
            if (!authenticated) {
                halt(401, "You are not welcome here");
            }
        });

        // ??
        get("/files/*/list_folder_content", (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {
                int numberOfRows = 100;
                List<FolderFileContents> folderContent = create.selectFrom(FOLDER_FILE_CONTENTS).limit(numberOfRows).fetchInto(FolderFileContents.class);
                return folderContent;
            }
        },json);

        post("/files/upload", (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {
                FileContents fileContents = gson.fromJson(request.body(),FileContents.class);
                FileContentsRecord record = create.newRecord(FILE_CONTENTS);
                record.from(fileContents);
                record.store();
                response.status(CREATED);
                return record.into(FileContents.class);
            }
        },json);


        get("/files/*/download", ((request, response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));

        get("/files/*/get_meta_data", ((request, response) -> {
            return null;
        }));

        get("/files/*/get_meta_data/:userid", ((request, response) -> controller.getDirectoryByUserId(request, response)));

        get("/files/get_meta_data/:userid", ((request, response) -> controller.getDirectoryByUserId(request, response)));

        get("/files/get_meta_data/:dir_id", ((request, response) -> controller.getDirectoryByUserId(request, response)));

        put("/files/*/rename", ((request, response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));

        delete("files/*/delete", ((request, response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));

        put("/files/*/move", ((request, response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));

        put("/files/*/create_directory", ((request, response) -> controller.createDirectory(request, response)));
/*
        //before creating new user, chech if there hasnt already been created user with the same name
        before(USERS_PATH + "/create_user", (request, response) -> {
            User user = gson.fromJson(request.body(), User.class);
            try {
               controller.usersRepo.loadUserByName(user.getUserName());
            }  catch (Exception e){
                System.out.println("INNY WYJATEK !!!!!!!!!!!!!!!!!!!!!!");
                halt(450, "User aleready exists");
            }


        });
*/
        // creating new user
        post(USERS_PATH + "/create_user", (request,response) -> controller.createNewUser(request, response),json);

        // get all users
        get(USERS_PATH, (request,response) -> controller.getAllUsers(),json);

        get(USERS_PATH + "/:user_name", ((request, response) -> controller.getUserByName(request, response)), json);

        get(USERS_PATH + "/access", (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        },json);
	}

	private static void info(Request req) {
		LOGGER.info("{}",req);
	}

    public static String createNewHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean arePasswordsEquals(String candidate, String hashed){
        return BCrypt.checkpw(candidate, hashed);
    }



}


