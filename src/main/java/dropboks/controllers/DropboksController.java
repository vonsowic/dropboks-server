package dropboks.controllers;

import com.google.gson.Gson;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.PathResolver;
import dropboks.TransferFile;
import dropboks.model.*;
import org.jooq.exception.DataAccessException;

import spark.Request;
import spark.Response;

import javax.naming.AuthenticationException;
import java.security.InvalidParameterException;
import java.util.List;

import static spark.Spark.halt;


/**
 * Main controller
 * Created by miwas on 08.01.17.
 */
public class DropboksController {

    private static final String SERVER_NAME = "bearcave";

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int SUCCESSFUL_DELETE_OPERATION = 204;
    private static final int ALREADY_EXISTS = 400;
    private static final int AUTHENTICATION_FAILURE = 401;
    private static final int UNSUCCESSFUL_LOGIN = 403;
    private static final int NOT_FOUND = 404;
    private static final int INVALID_PARAMETER = 405;

    private static final String path_doesnt_exist = "Path doesn't exist";
    private static final String already_exists = "Already exists";
    private static final String success = "Success";
    private static final String error = "Error :(";

    private final Gson gson = new Gson();

    private DirectoryFileController directoryFileController;
    private UserController userController;


    public DropboksController() {
        //Configuration configuration = new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE);
        userController = new UserController();
        directoryFileController = new DirectoryFileController();
    }

    //TODO: get password
    public void authenticate(Request request, Response response) {
        String userName = PathResolver.getUserName(request.splat()[0]); // get user name

        // get session
        request.queryMap().get("session").value();

        try{
            userController.authanticate(userName, null);
        } catch (NoRecordForundInDatabaseException e){
            halt(NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e){
            halt(AUTHENTICATION_FAILURE, e.getMessage());
        }
    }

    public Object createNewUser(Request request, Response response){
        User potentialNewUser = gson.fromJson(request.body(), User.class);
        User result;
        try{
            result = userController.createUser(potentialNewUser);
            directoryFileController.createDirectoryForUser(result.getUserName());
        } catch (AlreadyExistsException | NoRecordForundInDatabaseException e){
            response.status(ALREADY_EXISTS);
            return e.getMessage();
        } catch (DataAccessException e){
            e.printStackTrace();
            return e.toString();
        }
        response.status(CREATED);
        return result;
    }

    public Object createDirectory(Request request, Response response) {
        String path = request.splat()[0];  // path to new Directory
        DirectoryMetadata directoryMetadata;
        try {
            directoryMetadata = directoryFileController.createDirectory(path);
        } catch (AlreadyExistsException e){
            response.status(ALREADY_EXISTS);
            return e.toString();
        } catch (NoRecordForundInDatabaseException e){
            response.status(INVALID_PARAMETER);
            return e.toString();
        }
        response.status(CREATED);
        return directoryMetadata;
    }

    public Object rename(Request request, Response response)  {
        final String path = request.splat()[0]; // path to old directory or file
        String newName = PathResolver.getParentPath(path) + "/" + request.queryMap().get("new_name").value(); // path to new directory

        try {
            directoryFileController.rename(path, newName);
        } catch (NoRecordForundInDatabaseException e){
            response.status(INVALID_PARAMETER);
            return e.getMessage();
        }
        response.status(CREATED);
        return "OK";
    }

    public Object remove(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file
        try {
            directoryFileController.delete(path);
        } catch (InvalidParameterException | NoRecordForundInDatabaseException e){
            response.status(INVALID_PARAMETER);
            return e.getMessage();
        }
        response.status(SUCCESSFUL_DELETE_OPERATION);
        return success;
    }

    public Object getMetaData(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory
        Object record;
        try {
            record = directoryFileController.getMetaData(path);
        } catch (InvalidParameterException e) {
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        response.status(OK);
        return record;
    }

    public Object uploadFile(Request request, Response response) {
        String pathToFile = request.queryMap().get("path").value();
        TransferFile tmpFile = gson.fromJson(request.body(), TransferFile.class);

        FileMetadata result;
        try {
            result = directoryFileController.uploadFile(pathToFile, tmpFile);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return e.getMessage();
        }

        response.status(CREATED);
        return result;
    }

    public Object move(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file
        String newPath = request.queryMap().get("new_path").value(); // path to new directory

        Object result;
        try {
            result = directoryFileController.move(path, newPath);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return e.getMessage();
        }
        response.status(OK);
        return result;
    }

    //TODO: all
    public Object access(Request request, Response response) {
        return null;
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

    public List<Object> getListFolderContent(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory
        String tmp = request.queryMap().get("recursive").value(); // path to new directory
        boolean recursive;
        if (tmp.equals("true")) {
            recursive = true;
        } else {
            recursive = false;
        }

        List metadataList;
        try {
            metadataList = directoryFileController.getListFolderContent(path, recursive);
        } catch (InvalidParameterException e){
            response.status(NOT_FOUND);
            return null;
        }
        response.status(OK);
        return metadataList;
    }
}
