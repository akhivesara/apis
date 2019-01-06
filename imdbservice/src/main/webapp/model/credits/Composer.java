package main.webapp.model.credits;


import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

//data model?
// inherit base class?
public class Composer extends APersonCategory implements ImDBBaseEntity {


    public Composer(String id) {
        super(id);
        setCategory(PersonCategory.COMPOSER);
    }

    public Composer(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }


    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
