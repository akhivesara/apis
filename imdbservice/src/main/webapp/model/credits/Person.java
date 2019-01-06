package main.webapp.model.credits;

import main.webapp.ImDBBaseEntity;

import java.util.HashMap;

//data model?
// inherit base class?
public class Person implements ImDBBaseEntity {

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private String id;
    private String name;

    //TODO: Needed? Commenting to find out
//    public void setCategory(PersonCategory category) {
//        this.category = category;
//    }

    //optional
    //private PersonCategory category;

    public Person(HashMap<String, String> data) {
        this.id = data.get("nconst");
        this.name = data.get("primaryName");
    }

    public Person(String id) {
        if (id != null) {
            String[] ids = id.split(",");
            this.id = ids[0];
        }
    }

    @Override
    public String toString() {
        return "Person--> ID: "+this.id +
               " Name: "+this.name;
    }
}
