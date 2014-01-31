package nl.amnesty.crm.sql;

import java.sql.Connection;
import nl.amnesty.crm.entity.Relation;

/**
 *
 * @author ed
 */
public class RelationSQL {

    /*
     * ----------------------------------------
     * Standard CRUD methods
     * ----------------------------------------
     */
    public Relation create(Connection connection, Relation relation) {
        // TODO: Implementation for adding relation, probably by adding entry to definition table
        return null;
    }

    public Relation read(Connection connection, long relationid) {
        // TODO: Implementation for reading relation, this is just a stub
        return null;
    }

    public boolean update(Connection connection, Relation relation) {
        // TODO: Implementation for updating relation, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long relationid) {
        // TODO: Implementation for deleting relation, this is just a stub
        return false;
    }

    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Relation match(Connection connection, Relation relation) {
        // TODO: Implementation for matching, this is just a stub
        relation.setStatus(Relation.STATUS_MATCHED_NONE);
        return relation;
    }
}
