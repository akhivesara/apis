package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

//data model?
// inherit base class?
public class Editor extends APersonCategory implements ImDBBaseEntity {

    public Editor(String id) {
        super(id);
        setCategory(PersonCategory.EDITOR);
    }

    public Editor(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
