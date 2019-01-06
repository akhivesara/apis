package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Unknown extends APersonCategory implements ImDBBaseEntity {

    public Unknown(String id) {
        super(id);
        setCategory(PersonCategory.UNKNOWN);
    }

    public Unknown(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}

