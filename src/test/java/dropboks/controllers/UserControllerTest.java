package dropboks.controllers;

import dropboks.dao.SessionDAO;
import dropboks.dao.UsersDAO;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

import javax.naming.AuthenticationException;

import static pl.edu.agh.kis.florist.db.tables.SessionData.SESSION_DATA;
import static pl.edu.agh.kis.florist.db.tables.Users.USERS;

/**
 * Created by miwas on 23.01.17.
 */
public class UserControllerTest {

    String userName = "Testowy_user";
    String password = "haslo";
    Integer id = 9999999;
    String cookie = "cooookieeeiieiei";

    final UserController controller = new UserController();

    final UsersDAO userRepo = new UsersDAO(
            User.class,
            USERS
    );

    final SessionDAO sessionRepo = new SessionDAO(
            dropboks.model.Session.class,
            SESSION_DATA
    );

    @Before
    public void initialize(){
        User user = new User(
                id,
                userName,
                userName,
                password);
        try{
            controller.createUser(user);
        }catch (AlreadyExistsException e){}
    }

    @Test(expected = NoRecordForundInDatabaseException.class)
    public void authenticateBeforeLogin() throws Exception {
        controller.authenticate(userName, cookie);
    }

    @Test(expected = NoRecordForundInDatabaseException.class)
    public void authenticateBeforeLoginWhenUserDoesntExist() throws Exception {
        controller.authenticate("Sialalalalalallala", cookie);
    }


    @Test(expected = AuthenticationException.class)
    public void loginWhenPasswordIsIncorrect() throws Exception {
        controller.login(userName, password+"hahahha", cookie, 100);
    }

    @Test
    public void checkUsersData() throws Exception{
        User user = userRepo.findById(id);

        Assert.assertEquals(userName, user.getUserName());
        Assert.assertEquals(userName, user.getDisplayName());
        Assert.assertEquals(id, user.getId());
        Assert.assertTrue(BCrypt.checkpw(password, user.getHashedPassword()));


    }

    @Test
    public void login() throws Exception {
        dropboks.model.Session result = controller.login(userName, password, cookie, 100);

        Assert.assertTrue(sessionRepo.existsById(id));
        Assert.assertTrue(sessionRepo.exists(result));
        Assert.assertTrue(sessionRepo.existsBySecondId(cookie));
    }

    @Test
    public void authenticateAfterLogin() throws Exception {
        controller.authenticate(userName, cookie);
    }


    @After
    public void deleteUserAfterTests() throws Exception {
        userRepo.deleteById(id);
        Assert.assertFalse(userRepo.existsBySecondId(userName));
        Assert.assertFalse(userRepo.existsById(id));

        SessionDAO repo = new SessionDAO(
                dropboks.model.Session.class,
                SESSION_DATA
        );

        repo.deleteById(id);
        Assert.assertFalse(repo.existsBySecondId(cookie));
        Assert.assertFalse(repo.existsById(id));


    }
}