package com.imdb.model.credits;

import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Self extends APersonCategory implements IMDBBaseEntity {

    public Self(String id) {
        super(id);
        setCategory(PersonCategory.SELF);
    }

    public Self(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.SELF);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
