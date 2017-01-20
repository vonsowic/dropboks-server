package dropboks.controllers;

import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.dao.SessionDAO;
import dropboks.dao.UsersDAO;
import dropboks.exceptions.PermissionException;
import dropboks.model.Session;
import dropboks.model.User;
import org.jooq.exception.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;

import javax.naming.AuthenticationException;

import java.util.Date;

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

    public void authenticate(String userName, String cookie)
            throws NoRecordForundInDatabaseException, PermissionException{

        User user;
        try {
            user = usersRepo.findBySecondId(userName);
        } catch (DataAccessException e){
            throw new NoRecordForundInDatabaseException("I'm sorry to say you aren't exist.");
        }

        Session session;
        try {
            session = sessionRepo.findBySecondId(cookie);
        } catch (DataAccessException e){
            throw new NoRecordForundInDatabaseException("You need to log in");
        }


        if ( !session.getUserId().equals(user.getId())){
            throw new PermissionException("You have no right to this place!!!!!!!!");
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

    public Session login(String userName, String password, spark.Session session) throws AuthenticationException{
        try {
            User user = usersRepo.findBySecondId(userName);
            String sessionId = session.id();

            if ( !checkPassword(password, user.getHashedPassword())){
                session.removeAttribute(userName);
                throw new AuthenticationException("Your password could be a little bit more correct.");
            }


            try {
                if ( sessionRepo.existsById(user.getId()))
                    return sessionRepo.findById(user.getId());
            } catch (Exception e){
            }

            Session sessionToDb = new Session(sessionId, user.getId(), sessionRepo.time());
            //new SetExpireTime(user, session, 10000);
            return sessionRepo.store(sessionToDb);

        } catch (DataAccessException | NullPointerException e){
            e.printStackTrace();
            throw new AuthenticationException("User doesn't exist");
        }
    }


    private class SetExpireTime implements Runnable{

        User user;
        spark.Session session;
        long time;

        public SetExpireTime(User user, spark.Session session, long time) {
            this.user =user;
            this.session = session;
            this.time = time;
        }

        @Override
        public void run() {
            Date date = new Date();
            System.out.println("ROZPOCZYNAM NOWA SESJE");
            while ( date.getTime()-session.lastAccessedTime() < time){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("ROZPOCZYNAM NOWA SESJE".toLowerCase());

            }
            session.removeAttribute(user.getUserName());
            sessionRepo.deleteById(user.getId());
        }
    }
}
