package com.imdb.model;

import java.util.HashMap;

//data model?
// inherit base class?
public class Rating implements ImDBBaseEntity {

    public Rating(String titleId, double averageRating, int totalVotes) {
        this.titleId = titleId;
        this.averageRating = averageRating;
        this.totalVotes = totalVotes;
    }

    public String getTitleId() {
        return titleId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    private String titleId;
    private double averageRating;
    private int totalVotes;

    public Rating(HashMap<String, String> data) {
        this.titleId = data.get("tconst");
        this.averageRating = Double.parseDouble(data.get("averageRating"));
        this.totalVotes = Integer.parseInt(data.get("numVotes"));
    }
}
