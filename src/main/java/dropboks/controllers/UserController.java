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

import java.time.LocalDateTime;
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

        Integer userId;
        try {
            userId = usersRepo.getIdBySecondId(userName);
        } catch (DataAccessException e){
            throw new NoRecordForundInDatabaseException("I'm sorry to say you aren't exist.");
        }

        Session session;
        try {
            session = new Session(sessionRepo.findBySecondId(cookie));
        } catch (DataAccessException e){
            throw new NoRecordForundInDatabaseException("You need to log in");
        }

        if ( !session.getUserId().equals(userId)){
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

    public Session login(String userName, String password, String sessionId, int expireTime) throws AuthenticationException{

            User user = usersRepo.findBySecondId(userName);

            if ( !checkPassword(password, user.getHashedPassword())){
                throw new AuthenticationException("Your password could be a little bit more correct.");
            }

            new Thread(new SetExpireTime(user, null, expireTime)).start();
            Session sessionToDb = new Session(user.getId(), sessionId, sessionRepo.time());
            return sessionRepo.store(sessionToDb);
    }

    public void logout(String id) throws DataAccessException{
        sessionRepo.deleteBySecondId(id);
    }


    private class SetExpireTime implements Runnable{

        User user;
        spark.Session session;
        long time;

        public SetExpireTime(User user, spark.Session session, long time) {
            this.user =user;
            this.session = session;
            this.time = time*1000;
        }

        @Override
        public void run() {
            System.out.println("USER: "+ user.getUserName() + ": login at: "+ LocalDateTime.now());
            do {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("USER: " + user.getUserName() + ": " + (time - new Date().getTime()+session.lastAccessedTime()) + " miliseconds left");
            } while ( new Date().getTime()-session.lastAccessedTime() < time);
            sessionRepo.deleteById(user.getId());
            System.out.println("USER: " + user.getUserName() + " logout at:" + LocalDateTime.now());
        }
    }
}
