package main.webapp.model.credits;


import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

//data model?
// inherit base class?
public class Actress extends APersonCategory implements ImDBBaseEntity {

    public Actress(String id) {
        super(id);
        setCategory(PersonCategory.ACTRESS);
    }

    public Actress(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
