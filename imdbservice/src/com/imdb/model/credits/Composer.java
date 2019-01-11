package com.imdb.model.credits;


import com.imdb.model.ImDBBaseEntity;

import java.util.HashMap;

public class Composer extends APersonCategory implements ImDBBaseEntity {


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
