package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;
import main.webapp.model.credits.Writer;
import main.webapp.util.ImdbUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WriterDBValuator extends IDBValuator {

    public WriterDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.WRITER_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst, nconst";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.CHAR);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "tconst";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null && ((Writer)entity).getId() != null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Writer writer = (Writer) entity;
        v.add(writer.getTitleId());
        v.add(writer.getId());
        v.add(writer.getTitleId());
        return v;
    }
}

