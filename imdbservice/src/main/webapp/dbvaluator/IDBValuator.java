package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class IDBValuator {

    abstract public String getDBTable();

    abstract public String getColumnsString();

    abstract public List<Integer> getColumnsType();
    /**
     *
     * @param entity
     * @return
     */
    abstract public ArrayList valuesPerEntity(ImDBBaseEntity entity);

    public ImDBBaseEntity imdbEntityPerResultSet(ResultSet rs) throws SQLException {
        return null;
    };

    public String getDuplicateUpdateColumnString() {
        return null;
    }

    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    private void validator () throws Exception {
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

    public final void validateRetrieveInputs() throws Exception {
        if (getDBTable() == null) throw new Exception("getDBTable missing");
        if (getColumnForPrimaryWhereClauseById() == null) throw new Exception("WHERE ID missing");
    }


    // Cannot be overridden
    public final ArrayList<ArrayList> populate(ImDBBaseEntity entity) throws Exception {
        validator();
        ArrayList<ArrayList> returnVal = new ArrayList<>(0);
        ArrayList vals = valuesPerEntity(entity);
        if (vals != null && vals.size() > 0) {
            if (vals.get(0) instanceof ArrayList) {
                returnVal = vals;
            } else {
                returnVal = new ArrayList<ArrayList>();
                returnVal.add(vals);
            }
        }
        return returnVal;
    }

    public final ImDBBaseEntity retrieve(ResultSet rs) throws Exception {
        //validateRetrieveInputs();
        return imdbEntityPerResultSet(rs);
    }


    public String getColumnForPrimaryWhereClauseById() {
        return null;
    }

}