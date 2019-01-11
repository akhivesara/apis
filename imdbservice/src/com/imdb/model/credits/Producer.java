package com.imdb.model.credits;

import com.imdb.model.ImDBBaseEntity;

import java.util.HashMap;

public class Producer extends APersonCategory implements ImDBBaseEntity {

    public Producer(String id) {
        super(id);
        setCategory(PersonCategory.PRODUCER);
    }

    public Producer(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.PRODUCER);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
