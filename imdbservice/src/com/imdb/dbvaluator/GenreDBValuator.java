package com.imdb.dbvaluator;

import com.imdb.util.ImdbUtils;
import com.imdb.model.IMDBBaseEntity;
import com.imdb.model.Title;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreDBValuator extends AbstractDBValuator {

    public GenreDBValuator() {}

    @Override
    public String getDBTable() {
        return ImdbUtils.GENRE_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst, genre";
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
    public Boolean isValid(IMDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList<ArrayList> valuesPerEntity(IMDBBaseEntity entity) {
        ArrayList p = new ArrayList();
        Title title = (Title) entity;
        String titleGenres = title.getGenres();
        String[] genres = titleGenres != null ? titleGenres.split(",") : new String[] {};
        for (String genre : genres) {
            ArrayList c = new ArrayList();
            c.add(title.getId());
            c.add(genre);
            c.add(title.getId());
            p.add(c);
        }
        return p;
    }

    @Override
    public IMDBBaseEntity entityPerResultSet(ResultSet rs) throws SQLException {
        String tconst = rs.getString("tconst");
        String genre = rs.getString("genre");
        return new Title(tconst, genre);
    }
}

