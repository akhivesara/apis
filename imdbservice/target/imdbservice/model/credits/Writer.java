package main.webapp.model.credits;

import java.util.HashMap;

public class Writer extends APersonCategory {


    public Writer(HashMap<String, String> data) {
        this(data.get("writers") != null ? data.get("writers"): data.get("nconst"));
        setTitleId(data.get("tconst"));
    }

    public Writer(String id) {
        super(id);
        setCategory(PersonCategory.WRITER);
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }


}
