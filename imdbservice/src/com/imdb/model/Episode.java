package com.imdb.model;

import java.util.HashMap;

//data model?
// inherit base class?
public class Episode implements ImDBBaseEntity {

    public String getId() {
        return id;
    }
    public String getParentId() {
        return parentId;
    }



    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    private String id;
    private String parentId;


    private Integer seasonNumber;
    private Integer episodeNumber;

    public Episode(HashMap<String, String> data) {
        this.id = data.get("tconst");
        this.parentId = data.get("parentTconst");
        this.seasonNumber = data.get("seasonNumber") != null ? Integer.valueOf(data.get("seasonNumber")) : null;
        this.episodeNumber = data.get("episodeNumber") != null ? Integer.valueOf(data.get("episodeNumber")) : null;

    }
}
