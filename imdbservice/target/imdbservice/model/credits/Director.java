package main.webapp.model.credits;

import java.util.HashMap;

public class Director extends APersonCategory {


    public Director(String id) {
        super(id);
        setCategory(PersonCategory.DIRECTOR);
    }

    public Director(HashMap<String, String> data) {
        this(data.get("directors") != null ? data.get("directors"): data.get("nconst"));
        setTitleId(data.get("tconst"));
    }


    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }

}
