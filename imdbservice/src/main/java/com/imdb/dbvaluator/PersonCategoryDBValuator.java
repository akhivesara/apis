package com.imdb.dbvaluator;

import com.imdb.model.IMDBBaseEntity;
import com.imdb.model.credits.APersonCategory;
import com.imdb.util.ImdbUtils;
import com.imdb.model.credits.PersonCategory;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PersonCategoryDBValuator extends AbstractDBValuator {

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
    public Boolean isValid(IMDBBaseEntity entity) {
        return entity !=null && ((APersonCategory)entity).getCategory() != null;
    }

    @Override
    public ArrayList valuesPerEntity(IMDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        APersonCategory title = (APersonCategory) entity;
        v.add(title.getTitleId());
        v.add(title.getId());
        v.add(title.getCategory().toString());
        v.add(title.getTitleId());
        return v;
    }

    @Override
    public IMDBBaseEntity entityPerResultSet(ResultSet rs) throws SQLException {
        IMDBBaseEntity object = null;
        Class resolvedClass = PersonCategory.findClassByPersonCategory(PersonCategory.findByCategory(rs.getString("category")));
        HashMap i = new HashMap();
        i.put("tconst", rs.getString("tconst"));
        i.put("nconst", rs.getString("nconst"));
        i.put("name", rs.getString("primaryName"));
        i.put("title", rs.getString("title"));
        Constructor<?> resolvedCons = null;
        try {
            resolvedCons = resolvedClass.getConstructor(HashMap.class);
            object = (APersonCategory)resolvedCons.newInstance(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }
}

