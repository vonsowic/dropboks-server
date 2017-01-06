package dropboks;

import com.google.gson.Gson;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;
import pl.edu.agh.kis.florist.db.tables.pojos.FolderFileContents;
import pl.edu.agh.kis.florist.db.tables.records.FolderFileContentsRecord;
import spark.ResponseTransformer;

import java.util.List;
import static spark.Spark.get;

import static pl.edu.agh.kis.florist.db.Tables.FOLDER_FILE_CONTENTS;

/**
 * Created by miwas on 06.01.17.
 */
public class GetRequest<R extends UpdatableRecordImpl<R>, SentObject extends TableImpl<R>> {

    final static Gson gson = new Gson();
    final static ResponseTransformer json = gson::toJson;
    final static String DB_URL = "jdbc:sqlite:test.db";

    int numberOfRows = 100;
    String path;


    public void initialize(){

        get(path, (request,response) -> {
            try(DSLContext create = DSL.using(DB_URL)) {
                //List<FolderFileContents> folderContent = create.selectFrom(FOLDER_FILE_CONTENTS).limit(numberOfRows).fetchInto(FolderFileContents.class);
                //List<SentObject> folderContent =
                       // create.selectFrom(SentObject)
                      //  .limit(numberOfRows)
                      //  .fetchInto(SentObject);

              //  return folderContent;
                return null;
            }
        },json);
    }

}
