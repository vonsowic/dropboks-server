package dropboks;

import static pl.edu.agh.kis.florist.db.Tables.*;
import static spark.Spark.*;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pl.edu.agh.kis.florist.db.tables.pojos.Authors;
import pl.edu.agh.kis.florist.db.tables.pojos.FileContents;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderFileContents;
import pl.edu.agh.kis.florist.db.tables.pojos.Users;
import pl.edu.agh.kis.florist.db.tables.records.AuthorsRecord;
import pl.edu.agh.kis.florist.db.tables.records.FileContentsRecord;
import pl.edu.agh.kis.florist.db.tables.records.UsersRecord;
import spark.Request;
import spark.ResponseTransformer;

public class App {

	final static private Logger LOGGER = LoggerFactory.getILoggerFactory().getLogger("requests");
	
	public static void main(String[] args) {
		final int CREATED = 201;
		final String AUTHORS_PATH = "/authors";

		final Gson gson = new Gson();
		final ResponseTransformer json = gson::toJson;
		final String DB_URL = "jdbc:sqlite:test.db";
		
		port(4567);
		
		before("/*/", (req, res) -> { 
		    info(req);
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
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));

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


        put("/files/*/create_directory", ((request, response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {

                return true;
            }
        }));


        // creating new user
        post("/users/create_user", (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {
                Users user = gson.fromJson(request.body(),Users.class);
                UsersRecord record = create.newRecord(USERS);
                record.from(user);
                record.store();
                response.status(CREATED);
                return record.into(Users.class);
            }
        },json);

        // get all users
        get("/users", (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {
                int numberOfRows = 100;
                List<Users> users = create.selectFrom(USERS).limit(numberOfRows).fetchInto(Users.class);
                return users;
            }
        },json);

        get("/users/access", (request,response) -> {
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


