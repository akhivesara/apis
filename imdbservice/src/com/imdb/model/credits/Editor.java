package com.imdb.model.credits;

import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Editor extends APersonCategory implements IMDBBaseEntity {

    public Editor(String id) {
        super(id);
        setCategory(PersonCategory.EDITOR);
    }

    public Editor(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.EDITOR);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
