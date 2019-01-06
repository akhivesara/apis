package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;
import main.webapp.dbvaluator.IDBValuator;
import main.webapp.model.Title;
import main.webapp.util.ImdbUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleDBValuator extends IDBValuator {

    public TitleDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.TITLE_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst, titleType, title, isAdult, runtimeMinutes";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.CHAR, Types.INTEGER, Types.INTEGER, Types.CHAR);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "titleType";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Title title = (Title) entity;
        v.add(title.getId());
        v.add(title.getTitleType());
        v.add(title.getTitle());
        v.add(title.isAdult() ? 1 : 0);
        v.add(title.getRuntimeMinutes());
        v.add(title.getTitleType());
        return v;
    }
}
