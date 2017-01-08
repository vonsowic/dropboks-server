package dropboks.dao;

import dropboks.model.User;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import pl.edu.agh.kis.florist.db.tables.records.UsersRecord;

import java.util.List;
import java.util.stream.Collectors;

import static pl.edu.agh.kis.florist.db.Tables.USERS;

/**
 * Created by miwas on 08.01.17.
 */
public class UsersDAO {
    private final String DB_URL = "jdbc:sqlite:test.db";

    public List<User> loadAllUsers() {
        try (DSLContext create = DSL.using(DB_URL)) {
            List<User> users =
                    create.select(USERS.fields())
                            .from(USERS)
                            .fetchInto(User.class);
            return users;
        }
    }

    public User loadUserByName(String name) throws DataAccessException{
        try (DSLContext create = DSL.using(DB_URL)) {
            UsersRecord record = null;
            try {
                record = create.selectFrom(USERS).where(USERS.USER_NAME.equal(name)).fetchOne();
            } catch (DataAccessException e){
                throw e;
            }
            User user = record.into(User.class);
            return user;
        }
    }

    public User loadUserOfId(int userId) {
        try (DSLContext create = DSL.using(DB_URL)) {
            UsersRecord record = create.selectFrom(USERS).where(USERS.ID.equal(userId)).fetchOne();
            User user = record.into(User.class);
            return user;
        }
    }

    public User store(User user) {
        try (DSLContext create = DSL.using(DB_URL)) {
            UsersRecord record = create.newRecord(USERS,user);
            record.store();
            return record.into(User.class);
        }
    }

    public List<User> store(List<User> users) {
        //in the future can be optimized into single db query
        return users.stream().map(this::store).collect(Collectors.toList());
    }
}
