package dropboks;

import com.google.gson.Gson;
import dropboks.dao.*;
import dropboks.model.DirectoryFileContest;
import dropboks.model.DirectoryMetadata;
import dropboks.model.FileContent;
import dropboks.model.FileMetadata;
import dropboks.model.User;
import org.jooq.exception.DataAccessException;

import spark.Request;
import spark.Response;

import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;
import static pl.edu.agh.kis.florist.db.tables.FileContents.FILE_CONTENTS;
import static pl.edu.agh.kis.florist.db.tables.FolderFileContents.FOLDER_FILE_CONTENTS;
import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;
import static pl.edu.agh.kis.florist.db.tables.Users.USERS;



import static spark.Spark.halt;


/**
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

    private UsersDAO usersRepo;
    private DirectoryMetadataDAO dirMetaRepo;
    private DirectoryFileContestDAO dirFileContRepo;
    private FileMetadataDAO filesMetaRepo;
    private FileContentDAO filesContRepo;

    public DropboksController() {
        //Configuration configuration = new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE);
        this.usersRepo = new UsersDAO(
                User.class,
                USERS);

        this.dirMetaRepo = new DirectoryMetadataDAO(
                DirectoryMetadata.class,
                FOLDER_METADATA,
                this);

        this.filesMetaRepo = new FileMetadataDAO(
                FileMetadata.class,
                FILE_METADATA,
                this
        );

        this.filesContRepo = new FileContentDAO(
                FileContent.class,
                FILE_CONTENTS
        );

        this.dirFileContRepo = new DirectoryFileContestDAO(
                DirectoryFileContest.class,
                FOLDER_FILE_CONTENTS
        );
    }

    //TODO: all
    public void authenticate(Request request, Response response) {
        // get session
        request.queryMap().get("session").value();

        boolean authenticated = true;
        // ... check if authenticated
        if (!authenticated) {
            halt(AUTHENTICATION_FAILURE, "You are not welcome here");
        }
    }

    public Object createNewUser(Request request, Response response){

        User potentialNewUser = gson.fromJson(request.body(), User.class);
        potentialNewUser = potentialNewUser.getUserWithHashedPassword();

        if ( usersRepo.existsBySecondId(potentialNewUser.getUserName())){
            response.status(ALREADY_EXISTS);
            return already_exists;
        }

        User result = null;
        try {
            result = usersRepo.store(potentialNewUser);
            //result = usersRepo.findById(usersRepo.getId(potentialNewUser));
            dirMetaRepo.createDirectoryForUser(result);
            //potentialNewUser = usersRepo.loadOfPath(potentialNewUser.getUserName());
            //usersRepo.hashPassword(potentialNewUser);

        } catch (DataAccessException e){
            e.printStackTrace();
        }

        response.status(CREATED);
        return result;
    }

    public Object createDirectory(Request request, Response response) {
        String path = request.splat()[0];

        if ( dirMetaRepo.existsBySecondId(path)){
            response.status(ALREADY_EXISTS);
            return already_exists;
        }

        DirectoryMetadata result = dirMetaRepo.store(path);
        response.status(CREATED);
        return result;
    }

    public Object rename(Request request, Response response) {
        final String path = request.splat()[0]; // path to old directory or file
        String newName = request.queryMap().get("new_path").value(); // path to new directory

        MetadataDAO repo = resolveMetaType(path);
        if ( repo == null){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        repo.rename(path, newName);

        response.status(CREATED);
        return "Hello world";
    }

    public Object remove(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file

        // disable removing main directory
        if ( PathResolver.isHomeDirectory(path)){
            response.status(INVALID_PARAMETER);
            return "Can't remove home directory";
        }

        MetadataDAO repo = resolveMetaType(path);
        if ( repo == null){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        repo.delete(path);
        response.status(SUCCESSFUL_DELETE_OPERATION);
        return success;
    }

    public Object getMetaData(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory

        // Resolve type of file
        MetadataDAO repo = resolveMetaType(path);
        if ( repo == null){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        response.status(OK);
        return repo.getMetaData(path);
    }

    public Object uploadFile(Request request, Response response) {
        String pathToFile = request.queryMap().get("path").value();

        if ( !dirMetaRepo.existsBySecondId(PathResolver.getParentPath(pathToFile))
            || filesMetaRepo.existsBySecondId(pathToFile)) {
            response.status(INVALID_PARAMETER);
            return error;
        }

        TransferFile tmp = gson.fromJson(request.body(), TransferFile.class);

        FileMetadata fileMetadata = new FileMetadata(
                filesMetaRepo.generateId(),
                PathResolver.getUserName(pathToFile),
                pathToFile.toLowerCase(),
                pathToFile,
                dirMetaRepo.getIdBySecondId(PathResolver.getParentPath(pathToFile)),
                tmp.size(),
                getServerName(),
                usersRepo.getIdBySecondId(PathResolver.getUserName(pathToFile))
                );

        FileMetadata result = filesMetaRepo.store(fileMetadata);

        FileContent fileContent = new FileContent(filesContRepo.generateId(), tmp.getBytes());
        filesContRepo.store(fileContent);

        DirectoryFileContest directoryFileContest = new DirectoryFileContest(fileMetadata.getEnclosingFolderId(), fileMetadata.getOwnerId());
        dirFileContRepo.store(directoryFileContest);


        response.status(CREATED);

        return fileMetadata;
    }

    // TODO: move content type
    public Object move(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file
        String newPath = request.queryMap().get("new_path").value(); // path to new directory

        MetadataDAO repo = resolveMetaType(path);
        if ( repo == null){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        if ( !repo.existsBySecondId(newPath)){
            response.status(INVALID_PARAMETER);
            return null;
        }

        response.status(OK);
        return repo.move(path, newPath);
    }

    //TODO: all
    public Object access(Request request, Response response) {
        return null;
    }

    //TODO: all
    public Object download(Request request, Response response) {
        return null;
    }

    //TODO: all
    public Object getListFolderContent(Request request, Response response) {
        return null;
    }

    public UsersDAO getUsersRepository() {
        return usersRepo;
    }

    public DirectoryMetadataDAO getDirectoryMetadataRepository() {
        return dirMetaRepo;
    }

    public static String getServerName() {
        return SERVER_NAME;
    }

    public DirectoryFileContestDAO getDirectoryFileContentRepository() {
        return dirFileContRepo;
    }

    public MetadataDAO resolveMetaType(String path){
        if ( dirMetaRepo.existsBySecondId(path)){ // so its directory
            return dirMetaRepo;
        } else if (filesMetaRepo.existsBySecondId(path)) {
            return filesMetaRepo;
        } else return null;
    }
}
