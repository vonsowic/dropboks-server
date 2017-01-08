package dropboks;

import com.google.gson.Gson;
import dropboks.dao.DirectoryMetadataDAO;
import dropboks.dao.UsersDAO;
import dropboks.exceptions.NoSuchUserException;
import dropboks.exceptions.ParameterFormatException;
import dropboks.model.DirectoryMetadata;
import dropboks.model.User;
import org.jooq.exception.DataAccessException;
import spark.Request;
import spark.Response;

import java.util.List;


/**
 * Created by miwas on 08.01.17.
 */
public class DropboksController {

    private static final String SERVER_NAME = "bearcave";

    private static final int CREATED = 201;
    private static final int USER_ALREADY_EXISTS = 400;
    private static final int NOT_FOUND = 404;

    private final Gson gson = new Gson();
    final String DB_URL = "jdbc:sqlite:test.db";

    UsersDAO usersRepo;
    DirectoryMetadataDAO dirMetaRepo;

    public DropboksController(UsersDAO usersRepo, DirectoryMetadataDAO dirMetaRepo) {
        this.usersRepo = usersRepo;
        this.dirMetaRepo = dirMetaRepo;
    }

    public List<User> getAllUsers(){
        return usersRepo.loadAllUsers();
    }

    public Object getUserByName(Request request, Response response){
        User user;
        try {
            user = usersRepo.loadUserByName(request.params("user_name"));
        } catch (Exception e){
            response.status(NOT_FOUND);
            return null;
        }
        return user;
    }

    public Object getSingleUser(Request request, Response response) {
        try {
            int userId = Integer.parseInt(request.params("userid"));
            User result = usersRepo.loadUserOfId(userId);
            return result;
        } catch (NumberFormatException ex) {
            throw new ParameterFormatException(ex);
        }
    }

    public User createNewUser(Request request, Response response){
        User potentialNewUser = gson.fromJson(request.body(), User.class);
        try {
            usersRepo.loadUserByName(potentialNewUser.getUserName());
        } catch (Exception e){
            User result = usersRepo.store( potentialNewUser );

            DirectoryMetadata usersDirectory =
                    new DirectoryMetadata(
                            dirMetaRepo.generateId(),
                            result.getUserName(),
                            SERVER_NAME,
                            result.getId());
            dirMetaRepo.store(usersDirectory);

            response.status(CREATED);
            return result;
        }

        response.status(USER_ALREADY_EXISTS);
        return null;
    }

    public Object createDirectory(Request request, Response response) {
        return null;
    }

    public Object getDirectoryByUserId(Request request, Response response) {
        User user = (User) getSingleUser(request, response);
        return dirMetaRepo.findUsersDirectoryByHisId(user.getId());
    }

    public Object getDirectoryById(Request request, Response response) {
        try {
            int dirId = Integer.parseInt(request.params("dir_id"));
            DirectoryMetadata result = dirMetaRepo.loadDirOfId(dirId);
            return result;
        } catch (NumberFormatException ex) {
            throw new ParameterFormatException(ex);
        }
    }
}
