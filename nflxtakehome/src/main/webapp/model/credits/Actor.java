package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Actor extends APersonCategory implements ImDBBaseEntity {


    public Actor(String id) {
        super(id);
        setCategory(PersonCategory.ACTOR);
    }

    public Actor(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
