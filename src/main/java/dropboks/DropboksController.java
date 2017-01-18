package dropboks;

import com.google.gson.Gson;
import dropboks.dao.*;
import dropboks.model.*;
import javafx.util.Pair;
import org.jooq.exception.DataAccessException;

import spark.Request;
import spark.Response;

import java.security.InvalidParameterException;
import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;
import static pl.edu.agh.kis.florist.db.tables.FileContents.FILE_CONTENTS;
import static pl.edu.agh.kis.florist.db.tables.FolderFileContents.FOLDER_FILE_CONTENTS;
import static pl.edu.agh.kis.florist.db.tables.FolderFolderContents.FOLDER_FOLDER_CONTENTS;

import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;


import static pl.edu.agh.kis.florist.db.tables.SessionData.SESSION_DATA;
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

    private FileMetadataDAO filesMetaRepo;
    private FileContentDAO filesContRepo;

    private DirectoryFileContestDAO dirFileContRepo;
    private DirectoryDirectoryContenstDAO dirDirContRepo;

    private SessionDAO sessionRepo;


    public DropboksController() {
        //Configuration configuration = new DefaultConfiguration().set(DriverManager.getConnection(DB_URL)).set(SQLDialect.SQLITE);
        this.usersRepo = new UsersDAO(
                User.class,
                USERS,
                this
        );

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
                FILE_CONTENTS,
                this
        );

        this.dirFileContRepo = new DirectoryFileContestDAO(
                DirectoryFileContest.class,
                FOLDER_FILE_CONTENTS,
                this
        );

        this.dirDirContRepo = new DirectoryDirectoryContenstDAO(
                DirectoryDirectoryContest.class,
                FOLDER_FOLDER_CONTENTS,
                this
        );

        this.sessionRepo = new SessionDAO(
                Session.class,
                SESSION_DATA,
                this
        );
    }

    //TODO: all
    public void authenticate(Request request, Response response) {
        String userName = PathResolver.getUserName(request.splat()[0]); // get user name

        // get session
        request.queryMap().get("session").value();


        halt(AUTHENTICATION_FAILURE, "You are not " + userName);
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
            dirMetaRepo.createDirectoryForUser(result);

        } catch (DataAccessException e){
            e.printStackTrace();
        }

        response.status(CREATED);
        return result;
    }

    public Object createDirectory(Request request, Response response) {
        String path = request.splat()[0];  // path to new Directory

        if ( dirMetaRepo.existsBySecondId(path)
                || !dirMetaRepo.existsBySecondId(PathResolver.getParentPath(path))){
            response.status(ALREADY_EXISTS);
            return already_exists;
        }



        DirectoryMetadata result = dirMetaRepo.store(path);

        DirectoryDirectoryContest directoryDirectoryContest = dirDirContRepo.create(
                result.getFolderId(),
                result.getParentFolderId()
                );
        dirDirContRepo.store(directoryDirectoryContest);

        response.status(CREATED);
        return result;
    }

    public Object rename(Request request, Response response)  {
        final String path = request.splat()[0]; // path to old directory or file
        String newName = PathResolver.getParentPath(path) + "/" + request.queryMap().get("new_name").value(); // path to new directory


        MetadataDAO repo;
        try {
            repo = resolveMetaType(path);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }
        repo.rename(path, newName);
        response.status(CREATED);
        return "OK";
    }

    public Object remove(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file

        // disable removing main directory
        if ( PathResolver.isHomeDirectory(path)){
            response.status(INVALID_PARAMETER);
            return "Can't remove home directory";
        }

        Pair repo;
        try {
            repo = resolveType(path);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }
        MetadataDAO metadataDAO = (MetadataDAO) repo.getKey();
        ContentsDAO contentsDAO = (ContentsDAO) repo.getValue();

        Integer id = dirMetaRepo.findBySecondId(path).getFolderId();
        contentsDAO.delete(contentsDAO.findById(id));
        metadataDAO.deleteBySecondId(path);


        response.status(SUCCESSFUL_DELETE_OPERATION);
        return success;
    }

    public Object getMetaData(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory

        MetadataDAO repo;
        try {
            repo = resolveMetaType(path);
        } catch (InvalidParameterException e) {
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }

        response.status(OK);
        return repo.getMetaData(path);
    }

    public Object uploadFile(Request request, Response response) {
        String pathToFile = request.queryMap().get("path").value();
        if (!dirMetaRepo.existsBySecondId(PathResolver.getParentPath(pathToFile))
                || filesMetaRepo.existsBySecondId(pathToFile)) {
            response.status(INVALID_PARAMETER);
            return already_exists;
        }

        TransferFile tmpFile = gson.fromJson(request.body(), TransferFile.class);

        FileMetadata fileMetadata = new FileMetadata(
                filesMetaRepo.generateId(),
                PathResolver.getUserName(pathToFile),
                pathToFile.toLowerCase(),
                pathToFile,
                dirMetaRepo.getIdBySecondId(PathResolver.getParentPath(pathToFile)),
                tmpFile.size(),
                getServerName(),
                usersRepo.getIdBySecondId(PathResolver.getUserName(pathToFile))
        );

        FileMetadata result = filesMetaRepo.store(fileMetadata);

        FileContent fileContent = new FileContent(filesContRepo.generateId(), tmpFile.decode());
        filesContRepo.store(fileContent);

        DirectoryFileContest directoryFileContest = new DirectoryFileContest(fileMetadata.getFileId(), fileMetadata.getEnclosingFolderId());
        dirFileContRepo.store(directoryFileContest);

        response.status(CREATED);


        return result;
    }

    public Object move(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory or file
        String newPath = request.queryMap().get("new_path").value(); // path to new directory

        Pair repo;
        try {
            repo = resolveType(path);
        } catch (InvalidParameterException e){
            response.status(INVALID_PARAMETER);
            return path_doesnt_exist;
        }
        MetadataDAO metaRepo = (MetadataDAO) repo.getKey();
        ContentsDAO contRepo = (ContentsDAO) repo.getValue();

        if ( !metaRepo.existsBySecondId(PathResolver.getParentPath(newPath))){
            response.status(INVALID_PARAMETER);
            return error;
        }

        Object result = null;
        try {
            contRepo.move(path, newPath);
            result = metaRepo.move(path, newPath);
        } catch (DataAccessException ex){
            ex.printStackTrace();
        }
        response.status(OK);
        return result;
    }

    //TODO: all
    public Object access(Request request, Response response) {
        return null;
    }

    //TODO: all
    public Object download(Request request, Response response) {
        try {
            final String path = request.splat()[0]; // path to directory or file
            FileMetadata fileMetadata = filesMetaRepo.findBySecondId(path);
            Integer id = fileMetadata.getFileId();
            FileContent fileContent = filesContRepo.findById(id);
            TransferFile file = new TransferFile(fileContent.getContents());
            response.status(OK);
            return fileMetadata.getMetadataWithFile(file);
        } catch (Exception e){
            response.status(INVALID_PARAMETER);
            return error;
        }
    }

    public List<Object> getListFolderContent(Request request, Response response) {
        final String path = request.splat()[0]; // path to directory
        String tmp = request.queryMap().get("recursive").value(); // path to new directory
        boolean recursive;
        if (tmp.equals("true")){
            recursive = true;
        } else {
            recursive = false;
        }

        DirectoryMetadata parentDirectory;
        try {
            parentDirectory = dirMetaRepo.findBySecondId(path);
        } catch (NullPointerException ex){
            response.status(INVALID_PARAMETER);
            return null;
        }

        Integer directoryId = parentDirectory.getFolderId();

        List metadataList = dirMetaRepo
                        .getListOfMetadata(
                            dirDirContRepo
                                    .getListFolderContentOfId(directoryId, recursive)
        );

        metadataList.addAll(
                filesMetaRepo
                        .getListOfMetadata(
                                dirFileContRepo
                                        .getListFolderContentOfId(directoryId, recursive)
                        )
        );


        //List<Object> metadataList = dirMetaRepo.getMetadataWithChildren(directoryId, recursive);
        response.status(OK);
        return metadataList;
    }

    public UsersDAO getUsersRepository() {
        return usersRepo;
    }

    public DirectoryMetadataDAO getDirectoryMetadataRepository() {
        return dirMetaRepo;
    }

    public DirectoryFileContestDAO getDirectoryFileContestRepository() {
        return dirFileContRepo;
    }

    public DirectoryDirectoryContenstDAO getDirectoryDirectoryContestRepository() {
        return dirDirContRepo;
    }

    public static String getServerName() {
        return SERVER_NAME;
    }

    public Pair resolveType(String path) throws InvalidParameterException{
        if ( dirMetaRepo.existsBySecondId(path)){ // so its directory
            return new Pair<>(dirMetaRepo, dirDirContRepo);
        } else if (filesMetaRepo.existsBySecondId(path)) {
            return new Pair<>(filesMetaRepo, dirFileContRepo);
        } else throw new InvalidParameterException("Not found in database.");
    }

    public MetadataDAO resolveMetaType(String path) throws InvalidParameterException{
        return (MetadataDAO) resolveType(path).getKey();
    }

}
