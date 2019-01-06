package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Self extends APersonCategory implements ImDBBaseEntity {

    public Self(String id) {
        super(id);
        setCategory(PersonCategory.SELF);
    }

    public Self(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}