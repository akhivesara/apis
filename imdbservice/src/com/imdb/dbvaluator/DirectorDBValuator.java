package com.imdb.dbvaluator;

import com.imdb.model.credits.Director;
import com.imdb.util.ImdbUtils;
import com.imdb.model.ImDBBaseEntity;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectorDBValuator extends IDBValuator {


    public DirectorDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.DIRECTOR_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst,nconst";
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "tconst";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null && ((Director)entity).getId() != null;
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.CHAR);
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Director director = (Director) entity;
        v.add(director.getTitleId());
        v.add(director.getId());
        v.add(director.getTitleId());
        return v;
    }
}

