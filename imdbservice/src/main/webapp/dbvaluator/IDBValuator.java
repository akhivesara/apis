package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;


import java.sql.Types;
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
    // Cannot be overridden
    public final ArrayList<ArrayList> execute(ImDBBaseEntity entity) throws Exception {
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

}