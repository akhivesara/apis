package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;
import main.webapp.model.credits.APersonCategory;
import main.webapp.model.credits.Person;
import main.webapp.model.credits.PersonCategory;
import main.webapp.util.ImdbUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersonCategoryDBValuator extends IDBValuator {

    public PersonCategoryDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.CAST_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst, nconst, category";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.CHAR, Types.CHAR);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "tconst";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null && ((APersonCategory)entity).getCategory() != null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        APersonCategory title = (APersonCategory) entity;
        v.add(title.getTitleId());
        v.add(title.getId());
        v.add(title.getCategory().toString());
        v.add(title.getTitleId());
        return v;
    }
}

