package com.imdb.model.credits;


import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Composer extends APersonCategory implements IMDBBaseEntity {


    public Composer(String id) {
        super(id);
        setCategory(PersonCategory.COMPOSER);
    }

    public Composer(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.COMPOSER);
    }


    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
