package main.webapp.model.credits;


import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Actress extends APersonCategory implements ImDBBaseEntity {

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
