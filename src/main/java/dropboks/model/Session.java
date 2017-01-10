package dropboks.model;

import pl.edu.agh.kis.florist.db.tables.pojos.SessionData;

/**
 * Created by miwas on 10.01.17.
 */
public class Session extends SessionData {
    public Session(SessionData value) {
        super(value);
    }

    public Session(Object sessionId, Integer userId, String lastAccessed) {
        super(sessionId, userId, lastAccessed);
    }
}
