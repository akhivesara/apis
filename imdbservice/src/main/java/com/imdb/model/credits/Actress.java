package com.imdb.model.credits;


import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Actress extends APersonCategory implements IMDBBaseEntity {

    public Actress(String id) {
        super(id);
        setCategory(PersonCategory.ACTRESS);
    }

    public Actress(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.ACTRESS);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
