package dropboks;

import com.google.gson.Gson;
import dropboks.dao.DirectoryMetadataDAO;
import dropboks.dao.FileDAO;
import dropboks.dao.UsersDAO;
import dropboks.model.DirectoryMetadata;
import dropboks.model.FileContent;
import dropboks.model.User;
import org.jooq.exception.DataAccessException;

import pl.edu.agh.kis.florist.db.tables.pojos.FileMetadata;
import spark.Request;
import spark.Response;

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

    private final Gson gson = new Gson();
    //final String DB_URL = "jdbc:sqlite:test.db";

    private UsersDAO usersRepo;
    private DirectoryMetadataDAO dirMetaRepo;
    private FileDAO filesRepo;
    public DropboksController() {
        //Configuration configuration = new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE);
        this.usersRepo = new UsersDAO(User.class, USERS);
        this.dirMetaRepo = new DirectoryMetadataDAO(
                DirectoryMetadata.class,
                FOLDER_METADATA,
                this);
        this.filesRepo = new FileDAO();
    }

    //TODO
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

        User tmp = gson.fromJson(request.body(), User.class);
        User potentialNewUser = tmp.getUserWithHashedPassword();

        if ( usersRepo.exists(potentialNewUser.getUserName())){
            response.status(ALREADY_EXISTS);
            return null;
        }

        User result = null;
        try {
            result = usersRepo.store( potentialNewUser );
            dirMetaRepo.createDirectoryForUser(result);
        } catch (DataAccessException e){
            e.printStackTrace();
        }

        response.status(CREATED);
        return result;
    }

    public Object createDirectory(Request request, Response response) {
        String path = request.splat()[0];

        if ( dirMetaRepo.exists(path)){
            // for some reason 404 is returned ??
            response.status(ALREADY_EXISTS);
            return "Already exists";
        }

        DirectoryMetadata result = dirMetaRepo.store(path);
        response.status(CREATED);
        return result;
    }

    // TODO: do poprawy
    public Object renameFile(Request request, Response response) {
        final String path = request.splat()[0]; // path to old directory
        //ArrayList<String> listPath = dirMetaRepo.getListPath(path);

        if ( !dirMetaRepo.exists(path)){
            response.status(INVALID_PARAMETER);
            return null;
        }
/*
        String newName, newPath;
        if ( listPath.size()>1){
            newName = listPath.get(listPath.size()-1);
            newPath = StringUtils.join(listPath.subList(0, listPath.size()-2), "/");
        } else {
            response.status(INVALID_PARAMETER);
            return null;
        }

        DirectoryMetadata tmp = dirMetaRepo.loadDirOfPath(path);
        DirectoryMetadata directory = new DirectoryMetadata(
                tmp.getFolderId(),
                newName,
                (newPath+"/"+newName).toLowerCase(),
                newPath+"/"+newName,
                tmp.getServerCreatedAt(),
                tmp.getOwnerId());

        dirMetaRepo.remove(path);
        dirMetaRepo.store(directory);
        */
        response.status(CREATED);
        return "Hello world";
    }

    public Object remove(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory

        if ( !dirMetaRepo.exists(path)){
            response.status(INVALID_PARAMETER);
            return INVALID_PARAMETER;
        }

        // disable removing main directory
        if ( PathResolver.isHomeDirectory(path)){
            response.status(INVALID_PARAMETER);
            return "Error";
        }

        dirMetaRepo.remove(path);
        response.status(SUCCESSFUL_DELETE_OPERATION);
        return "Success";
    }

    // TODO: dodac obsluge plikow
    public Object getMetaData(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory

        if ( !dirMetaRepo.exists(path)){
            response.status(INVALID_PARAMETER);
            return "Path doesnt exists";
        }

        DirectoryMetadata result = dirMetaRepo.loadOfPath(path);
        response.status(OK);
        return result;
    }

    public Object uploadFile(Request request, Response response) {
        String pathToFile = request.queryMap().get("path").value();
        //ArrayList<String> listPath = dirMetaRepo.getListPath(pathToFile);

        TransferFile tmp = gson.fromJson(request.body(), TransferFile.class);

        FileMetadata fileMetadata = new FileMetadata(
                filesRepo.generateId(),
                PathResolver.getUserName(pathToFile),
                pathToFile.toLowerCase(),
                pathToFile,
                dirMetaRepo.getId(PathResolver.getParentPath(pathToFile)),
                tmp.size(),
                getServerName(),
                null,
                usersRepo.getId(PathResolver.getUserName(pathToFile))
                );
        FileContent fileContent = new FileContent(fileMetadata.getFileId(), tmp.getBytes());

        FileMetadata result = filesRepo.store(fileMetadata, fileContent);
        response.status(CREATED);

        return fileMetadata;
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

}
