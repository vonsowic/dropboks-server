package dropboks.dao;

import dropboks.model.User;
import org.jooq.exception.DataAccessException;
import org.junit.Assert;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import pl.edu.agh.kis.florist.db.tables.records.UsersRecord;

import static org.junit.Assert.*;
import static pl.edu.agh.kis.florist.db.tables.Users.USERS;

/**
 * Created by miwas on 23.01.17.
 */
public class UsersDAOTest {

    String userName = "User testowy";
    String password = "haslo";
    Integer id = 9999999;

    public UsersDAO initialize(boolean createUser){
        UsersDAO repo = new UsersDAO(
                User.class,
                USERS
        );

        if ( createUser) {
            try {
                repo.insert(new User(id, userName, userName, BCrypt.hashpw(password, BCrypt.gensalt())));
            } catch (DataAccessException e) {
                System.out.println(e.getMessage());
            }
        }
        return repo;
    }


    @Test
    public void existsBySecondId() throws Exception {
        UsersDAO repo = initialize(true);
        Assert.assertTrue(repo.existsBySecondId(userName));
    }

    @Test
    public void findBySecondId() throws Exception {
        UsersDAO repo = initialize(true);
        Assert.assertNotNull(repo.findBySecondId(userName));
    }

    @Test
    public void existsById() throws Exception {
        UsersDAO repo = initialize(true);
        try {
            Assert.assertTrue(repo.existsById(id));
        } catch (DataAccessException e){
        }
    }

    @Test
    public void exists() throws Exception {
        UsersDAO repo = initialize(true);
        try {
            User user = repo.findById(id);
            Assert.assertTrue(repo.exists(user));
        } catch (DataAccessException e){

        }
    }


    @Test
    public void deleteBySecondId() throws Exception {
        UsersDAO repo = initialize(true);
        repo.deleteBySecondId(userName);
        Assert.assertFalse(repo.existsBySecondId(userName));
    }

}