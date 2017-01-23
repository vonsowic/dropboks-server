package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.SessionData;

import java.sql.Timestamp;

/**
 * Created by miwas on 10.01.17.
 */
public class Session extends SessionData {

    public Session(SessionData value) {
        super(value);
    }

    public Session(Integer userId, String sessionId, Timestamp lastAccessed) {
        super(userId, sessionId, lastAccessed);
    }

    public Session(Integer userId, Timestamp lastAccessed) {

    }

}
