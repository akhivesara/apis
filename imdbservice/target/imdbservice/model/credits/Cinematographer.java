package main.webapp.model.credits;


import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Cinematographer extends APersonCategory implements ImDBBaseEntity {

    public Cinematographer(String id) {
        super(id);
        setCategory(PersonCategory.CINEMATOGRAPHER);
    }

    public Cinematographer(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
