package dropboks.controllers;

import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.dao.SessionDAO;
import dropboks.dao.UsersDAO;
import dropboks.model.Session;
import dropboks.model.User;
import org.mindrot.jbcrypt.BCrypt;

import javax.naming.AuthenticationException;

import static pl.edu.agh.kis.florist.db.tables.SessionData.SESSION_DATA;
import static pl.edu.agh.kis.florist.db.tables.Users.USERS;

/**
 * Created by miwas on 19.01.17.
 */
public class UserController {

    private UsersDAO usersRepo;
    private SessionDAO sessionRepo;

    UserController(){
        this.usersRepo = new UsersDAO(
                User.class,
                USERS
        );


        this.sessionRepo = new SessionDAO(
                Session.class,
                SESSION_DATA
        );
    }

    public void authanticate(String name, String password)
            throws NoRecordForundInDatabaseException, AuthenticationException{

        if ( !usersRepo.existsBySecondId(name)){
            throw new NoRecordForundInDatabaseException("I'm sorry to say you aren't exist.");
        }

        User user = usersRepo.findBySecondId(name);

        if (checkPassword(password, user.getHashedPassword())){
            throw new AuthenticationException("Password could have been more accurate.");
        }
    }

    public User createUser(User user) throws AlreadyExistsException{
        user.setHashedPassword(createHashedPassword(user.getHashedPassword()));
        if ( usersRepo.existsBySecondId(user.getUserName())){
            throw new AlreadyExistsException("I'm sorry to say user name has been already taken.");
        }

        return usersRepo.store(user);
    }

    public static boolean checkPassword(String candidatePassword,String storedHashedPassword) {
        return BCrypt.checkpw(candidatePassword, storedHashedPassword);
    }

    public static String createHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
