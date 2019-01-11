package com.imdb.model.credits;

import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Unknown extends APersonCategory implements IMDBBaseEntity {

    public Unknown(String id) {
        super(id);
        setCategory(PersonCategory.UNKNOWN);
    }

    public Unknown(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.UNKNOWN);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}

