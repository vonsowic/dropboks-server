package dropboks.controllers;

import com.google.gson.Gson;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.PathResolver;
import dropboks.TransferFile;
import dropboks.exceptions.PermissionException;
import dropboks.model.*;
import dropboks.model.Session;
import org.jooq.exception.DataAccessException;

import spark.*;

import javax.naming.AuthenticationException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Main controller
 * Created by miwas on 08.01.17.
 */
public class DropboksController {

    private static final String SERVER_NAME = "bearcave";

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int SUCCESSFUL_DELETE_OPERATION = 204;
    private static final int INVALID_PARAMETER = 405;

    private static final String path_doesnt_exist = "Path doesn't exist";
    private static final String already_exists = "Already exists";
    private static final String success = "Success";
    private static final String error = "Error :(";
    private static final String session_id_cookie = "JSESSIONID";

    private int expire_time = 100;  // seconds

    private final Gson gson = new Gson();

    private DirectoryFileController directoryFileController;
    private UserController userController;


    public DropboksController() {
        //Configuration configuration = new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE);
        userController = new UserController();
        directoryFileController = new DirectoryFileController();
    }

    public void authenticate(Request request, Response response) throws NoRecordForundInDatabaseException, PermissionException {
        String userName = PathResolver.getUserName(request.splat()[0]); // get user name

        // I know its terrible solution to resolving path, but deadline ;)
        if (userName.equals("upload")){
            userName = PathResolver.getUserName(request.queryMap().get("path").value());
        }

        String cookie = request.cookie(session_id_cookie);
        if ( cookie == null ){
            throw new PermissionException("You are not logged in. Cookie doesnt exist");
        }

        userController.authenticate(userName, cookie);
        response.status(OK);
    }

    public Session access(Request request, Response response) throws AuthenticationException {
        try {
            String header = request.headers("Authorization");
            header = header.split(" ")[1];  // remove "Basic "

            String[] tmp =new String(Base64.getDecoder()
                    .decode(header.getBytes()))
                    .split(Pattern.quote(":"));

            spark.Session session = request.session(true);
            Session result = userController.login(tmp[0], tmp[1], session.id(), expire_time);

            response.cookie(session_id_cookie, session.id(), expire_time, true);
            response.status(OK);
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new AuthenticationException("Sth with regex went wrong");
        } catch (AuthenticationException e) {
            throw e;
        }
    }

    public Object logout(Request request, Response response) throws AuthenticationException, DataAccessException {
        spark.Session session = request.session(true);
        userController.logout(session.id());
        response.cookie(session_id_cookie, session.id(), 0, true);
        response.status(OK);
        return success;
    }

    public Object createNewUser(Request request, Response response) throws NoRecordForundInDatabaseException, InvalidParameterException, DataAccessException{
        User potentialNewUser = gson.fromJson(request.body(), User.class);
        User result = userController.createUser(potentialNewUser);
        directoryFileController.createDirectoryForUser(result.getUserName());

        response.status(CREATED);
        return result;
    }

    public Object createDirectory(Request request, Response response) throws AlreadyExistsException, NoRecordForundInDatabaseException {
        String path = request.splat()[0];  // path to new Directory
        DirectoryMetadata directoryMetadata = directoryFileController.createDirectory(path);
        response.status(CREATED);
        return directoryMetadata;
    }

    public Object rename(Request request, Response response) throws NoRecordForundInDatabaseException {
        final String path = request.splat()[0]; // path to old directory or file
        String newName = PathResolver.getParentPath(path) + "/" + request.queryMap().get("new_name").value(); // path to new directory
        directoryFileController.rename(path, newName);
        response.status(CREATED);
        return "OK";
    }

    public Object remove(Request request, Response response) throws InvalidParameterException, NoRecordForundInDatabaseException{
        final String path = request.splat()[0]; // path to directory or file
        directoryFileController.delete(path);
        response.status(SUCCESSFUL_DELETE_OPERATION);
        return success;
    }

    public Object getMetaData(Request request, Response response) throws NoRecordForundInDatabaseException{
        final String path = request.splat()[0]; // path to directory
        Object record;
        record = directoryFileController.getMetaData(path);
        response.status(OK);
        return record;
    }

    public Object uploadFile(Request request, Response response) throws InvalidParameterException{
        String pathToFile = request.queryMap().get("path").value();

        System.out.println("REQUEST" + request);
        System.out.println("BODY " +request.body());


        TransferFile tmpFile = gson.fromJson(request.body(), TransferFile.class);
        FileMetadata result = directoryFileController.uploadFile(pathToFile, tmpFile);

        response.status(CREATED);
        return result;
    }

    public Object move(Request request, Response response) throws InvalidParameterException {
        final String path = request.splat()[0]; // path to directory or file
        String newPath = request.queryMap().get("new_path").value(); // path to new directory

        Object result = directoryFileController.move(path, newPath);

        response.status(OK);
        return result;
    }

    public Object download(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file
        FileMetadata file;
        try {
            file = directoryFileController.download(path);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return e.getMessage();
        }

        response.status(OK);
        return file;
    }

    public List<Object> getListFolderContent(Request request, Response response) throws InvalidParameterException{
        final String path = request.splat()[0]; // path to directory
        String tmp = request.queryMap().get("recursive").value(); // path to new directory
        boolean recursive;
        if (tmp.equals("true")) {
            recursive = true;
        } else {
            recursive = false;
        }

        List metadataList = directoryFileController.getListFolderContent(path, recursive);

        response.status(OK);
        return metadataList;
    }

}

