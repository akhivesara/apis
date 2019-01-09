package main.webapp.model.credits;

import java.util.HashMap;

public abstract class APersonCategory extends Person {

    //optional
    public PersonCategory category;

    private String titleId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public APersonCategory(String id) {
        super(id);
    }

    public APersonCategory(HashMap<String, String> data)
    {
        this(data.get("nconst"));
        setTitleId(data.get("tconst"));
        setTitle(data.get("title"));
        setName(data.get("name"));
    }

    abstract public void setCategory(PersonCategory category);

    public PersonCategory getCategory() {
        return category;
    }

    public void setTitleId(String id) {
        this.titleId = id;
    }

    public String getTitleId() {
        return titleId;
    }

    @Override
    public String toString() {
        return super.toString() +
                " PersonCategory: "+ this.category.toString() +
                " TitleId: "+ this.titleId;
    }
}
