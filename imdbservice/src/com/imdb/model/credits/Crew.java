package com.imdb.model.credits;

import com.imdb.model.ImDBBaseEntity;

import java.util.HashMap;

public class Crew implements ImDBBaseEntity  {

    public Director getDirector() {
        return director;
    }

    public Writer getWriter() {
        return writer;
    }

    private Director director;
    private  Writer writer;


    public String getTitleId() {
        return titleId;
    }

    private String titleId;

    public Crew(HashMap<String, String> data)
    {
        this.director = new Director(data);
        this.writer = new Writer(data);
        this.titleId = data.get("tconst");
    }

    @Override
    public String toString() {
        return "Crew--> Title: "+this.titleId +
               " Director--> "+this.director +
               " Writer--> "+this.writer ;

    }
}