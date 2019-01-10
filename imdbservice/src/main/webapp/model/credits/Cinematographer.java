package main.webapp.model.credits;


import main.webapp.model.ImDBBaseEntity;

import java.util.HashMap;

public class Cinematographer extends APersonCategory implements ImDBBaseEntity {

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
