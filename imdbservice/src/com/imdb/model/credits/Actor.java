package com.imdb.model.credits;

import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Actor extends APersonCategory implements IMDBBaseEntity {


    public Actor(String id) {
        super(id);
        setCategory(PersonCategory.ACTOR);
    }

    public Actor(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.ACTOR);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
