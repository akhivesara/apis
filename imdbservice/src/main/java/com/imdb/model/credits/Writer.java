package com.imdb.model.credits;

import java.util.HashMap;

public class Writer extends APersonCategory {


    public Writer(String id) {
        super(id);
        setCategory(PersonCategory.WRITER);
    }

    public Writer(HashMap<String, String> data) {
        this(data.get("writers") != null ? data.get("writers"): data.get("nconst"));
        setTitleId(data.get("tconst"));
        setTitle(data.get("title"));
        setName(data.get("name"));
    }

    @Override
    public void setCategory(PersonCategory category) {
        this.category = category;
    }


}
