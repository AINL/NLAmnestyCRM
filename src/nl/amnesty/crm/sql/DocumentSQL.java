package nl.amnesty.crm.sql;

import java.sql.Connection;
import nl.amnesty.crm.entity.Document;

/**
 *
 * @author ed
 */
public class DocumentSQL {

    private static final String MSG_EXCEPTION = "Fatal error while updating document object for {0}";
    private static final String MSG_EXCEPTION_SQL = "Fatal SQL error while updating document object for {0}";

    /*
     * ----------------------------------------
     * Standard CRUD methods
     * ----------------------------------------
     */
    public Document create(Connection connection, Document document) {
        // TODO: Implementation for adding document, probably by adding entry to definition table
        return null;
    }

    public Document read(Connection connection, long documentid) {
        // TODO: Set all properties
        return null;
    }

    public boolean update(Connection connection, Document document) {
        // TODO: Implementation for updating document, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long documentid) {
        // TODO: Implementation for deleting document, this is just a stub
        return false;
    }

    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Document match(Connection connection, Document document) {
        // TODO: Implementation for matching, this is just a stub
        document.setStatus(Document.STATUS_MATCHED_NONE);
        return document;
    }
}
