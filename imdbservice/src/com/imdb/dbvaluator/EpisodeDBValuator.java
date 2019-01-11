package com.imdb.dbvaluator;

import com.imdb.util.ImdbUtils;
import com.imdb.model.ImDBBaseEntity;
import com.imdb.model.Episode;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpisodeDBValuator extends AbstractDBValuator {

    public EpisodeDBValuator() {}


    @Override
    public String getDBTable() {
        return ImdbUtils.EPISODES_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "id, parentId, seasonNumber, episodeNumber";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.CHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "seasonNumber";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Episode episode = (Episode) entity;
        v.add(episode.getId());
        v.add(episode.getParentId());
        v.add(episode.getSeasonNumber());
        v.add(episode.getEpisodeNumber());
        v.add(episode.getSeasonNumber());
        return v;
    }
}

