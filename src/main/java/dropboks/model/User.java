package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.Users;

/**
 * Created by miwas on 08.01.17.
 */
public class User extends Users {
    public User(Users user) {
        super(user);
    }

    public User(Integer id, String name, String displayName, String hashedPassword) {
        super(id, name, displayName, hashedPassword);
    }

    public User(String name, String displayName, String hashedPassword) {
        super(null, name, displayName, hashedPassword);
    }

    public User(String name,  String hashedPassword) {
        super(null, name, null, hashedPassword);
    }

    private static final long serialVersionUID = -7821816371758851390L;
}
