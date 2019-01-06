package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

public class Producer extends APersonCategory implements ImDBBaseEntity {

    public Producer(String id) {
        super(id);
        setCategory(PersonCategory.PRODUCER);
    }

    public Producer(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
