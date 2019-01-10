package main.webapp.dbvaluator;

import main.webapp.model.ImDBBaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * IDBValuator is a abstract class. It is used for both populate
 * and retrieval operations to & from the database table.
 * If say TITLE DB table needs to be populated. It will require a
 * corresponding, say {@link TitleDBValuator}
 * Populate operation is performed by
 * {@link main.webapp.MySQLStore#populateUsingBatchInsert(ArrayList, int, IDBValuator)}
 * Retrieval operatrion is performed by
 * {@link main.webapp.MySQLStore} as well
 */
public abstract class IDBValuator {

    /**
     * @return    database table name
     */
    abstract public String getDBTable();

    /**
     * @return    comma separated string representing
     *            database table columns
     */
    abstract public String getColumnsString();

    /**
     * @return    database table types
     */
    abstract public List<Integer> getColumnsType();

    /**
     * Called to populate the database table. It should return
     * an arraylist with values in the same order as {@link #getColumnsString()}
     * @param entity    ImDBBaseEntity object
     * @return
     */
    abstract public ArrayList valuesPerEntity(ImDBBaseEntity entity);

    /**
     * Called when data is retrieved from the database table.
     * @param rs    ResultSet
     * @return      ImDBBaseEntity object
     * @throws SQLException     If the ResultSet does not contain the data
     */
    public ImDBBaseEntity entityPerResultSet(ResultSet rs) throws SQLException {
        return null;
    };

    /**
     * Database column used for ON DUPLICATE KEY UPDATE
     * @return      Column name
     */
    public String getDuplicateUpdateColumnString() {
        return null;
    }

    /**
     * Called before inserting into the database table, IDBValuator
     * can evaluate the entity and skip the insert if data is not valid
     * @param entity
     * @return      boolean
     */
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    /**
     * Validator called before populating table
     * @throws Exception
     */
    private void validator() throws Exception {
        if (getDBTable() == null) throw new Exception("getDBTable missing");
        if (getColumnsString() == null) throw new Exception("getColumnsString missing");
        if (getColumnsType() == null || getColumnsType().size() <= 0) throw new Exception("getColumnsType missing");
        if (getDuplicateUpdateColumnString() == null) {
            if (getColumnsString().split(",").length != getColumnsType().size())
                throw new Exception("mismatch for type and columns");

        } else {
            if (getColumnsString().split(",").length +
                    getDuplicateUpdateColumnString().split(",").length != getColumnsType().size())
                throw new Exception("mismatch for type and columns");
        }
    }

    /**
     * validator called before retrieving data from table
     * @throws Exception
     */
    public final void validateRetrieveInputs() throws Exception {
        if (getDBTable() == null) throw new Exception("getDBTable missing");
        if (getColumnForPrimaryKey() == null) throw new Exception("WHERE ID missing");
    }


    /**
     * Called to populate database table. Specific IDBValuators need to implement
     * {@link #valuesPerEntity(ImDBBaseEntity)}
     * @param entity
     * @return      2 dimensional arraylist
     * @throws Exception
     */
    public final ArrayList<ArrayList> populate(ImDBBaseEntity entity) throws Exception {
        validator();
        ArrayList<ArrayList> returnVal = new ArrayList<>(0);
        ArrayList vals = valuesPerEntity(entity);
        if (vals != null && vals.size() > 0) {
            if (vals.get(0) instanceof ArrayList) {
                returnVal = vals;
            } else {
                returnVal = new ArrayList<>();
                returnVal.add(vals);
            }
        }
        return returnVal;
    }

    /**
     * Called to retrieve data from database table. Specific IDBValuators need to implement
     * {@link #entityPerResultSet(ResultSet)}
     * @param rs
     * @return
     * @throws Exception
     */
    public final ImDBBaseEntity retrieve(ResultSet rs) throws Exception {
        return entityPerResultSet(rs);
    }

    /**
     * Primary Key for the database table
     * @return
     */
    public String getColumnForPrimaryKey() {
        return null;
    }

}