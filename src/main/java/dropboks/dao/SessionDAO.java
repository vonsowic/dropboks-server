package dropboks.dao;

import dropboks.DropboksController;
import dropboks.model.Session;
import org.jooq.Table;
import org.jooq.TableField;
import pl.edu.agh.kis.florist.db.tables.records.SessionDataRecord;

import static pl.edu.agh.kis.florist.db.Tables.SESSION_DATA;


/**
 * Created by miwas on 17.01.17.
 */
public class SessionDAO extends DAO<Session, SessionDataRecord, Object> {
    public SessionDAO(Class<Session> type, Table<SessionDataRecord> table, DropboksController controller) {
        super(type, table, controller);
    }

    @Override
    public TableField<SessionDataRecord, Integer> getIdOfTableRecord() {
        return SESSION_DATA.USER_ID;
    }

    @Override
    public TableField<SessionDataRecord, Object> getSecondIdOfTableRecord() {
        return SESSION_DATA.SESSION_ID;
    }

    @Override
    protected Integer getId(Session object) {
        return object.getUserId();
    }


}
