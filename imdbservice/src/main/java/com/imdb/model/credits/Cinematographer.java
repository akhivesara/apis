package com.imdb.model.credits;


import com.imdb.model.IMDBBaseEntity;

import java.util.HashMap;

public class Cinematographer extends APersonCategory implements IMDBBaseEntity {

    public Cinematographer(String id) {
        super(id);
        setCategory(PersonCategory.CINEMATOGRAPHER);
    }

    public Cinematographer(HashMap<String, String> data)
    {
        super(data);
        setCategory(PersonCategory.CINEMATOGRAPHER);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
