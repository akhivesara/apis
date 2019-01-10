package main.webapp.dbvaluator;

import main.webapp.model.ImDBBaseEntity;
import main.webapp.model.credits.Person;
import main.webapp.util.ImdbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonDBValuator extends IDBValuator {

    public PersonDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.PERSON_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "nconst, primaryName";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.CHAR);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "primaryName";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Person title = (Person) entity;
        v.add(title.getId());
        v.add(title.getName());
        v.add(title.getName());
        return v;
    }

    @Override
    public String getColumnForPrimaryKey() {
        return "nconst";
    }

    @Override
    public Person entityPerResultSet(ResultSet rs) throws SQLException {
        String nconst = rs.getString("nconst");
        String primaryName = rs.getString("primaryName");
        return new Person(nconst, primaryName);

    }
}

