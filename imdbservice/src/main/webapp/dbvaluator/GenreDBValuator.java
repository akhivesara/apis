package main.webapp.dbvaluator;

import main.webapp.ImDBBaseEntity;
import main.webapp.model.Rating;
import main.webapp.model.Title;
import main.webapp.util.ImdbUtils;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreDBValuator extends IDBValuator {

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
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList<ArrayList> valuesPerEntity(ImDBBaseEntity entity) {
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
}

