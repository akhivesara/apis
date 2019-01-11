package com.imdb.model;

import java.util.HashMap;

public class Title implements IMDBBaseEntity {


    public Title(String id, String genres) {
        this.id = id;
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleType() {
        return titleType;
    }

    public int getRuntimeMinutes() {
        return runtimeMinutes;
    }

    public boolean isAdult() {
        return isAdult;
    }

    private String id;
    private String title;
    private String titleType;
    private String genres;

    private boolean isAdult;

    private int runtimeMinutes;

    public Title(String id, String title, String titleType, int runtimeMinutes, boolean isAdult) {
        this.id = id;
        this.title = title;
        this.titleType = titleType;
        this.runtimeMinutes = runtimeMinutes;
        this.isAdult = isAdult;
    }

    public Title(){}

    public String getGenres() {
        return genres;
    }

    public Title(HashMap<String, String> data) {
        if (data.get("id") != null) {
            this.id = data.get("id");
        } else {
            this.id = data.get("tconst");
        }

        this.titleType = data.get("titleType");
        this.title = data.get("primaryTitle");
        this.runtimeMinutes = data.get("runtimeMinutes") != null ? Integer.parseInt(data.get("runtimeMinutes")): 0;
        this.isAdult = Integer.parseInt(data.get("isAdult")) == 1;
        this.genres = data.get("genres");
    }


    /*
    public static class TitleBuilder {

        private String id;
        private String title;

        private String titleType;
        private boolean isAdult;
        private int runtimeMinutes;

        public TitleBuilder(String id) {
            this.id = id;
        }

        public TitleBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public TitleBuilder withTitleType(String titleType) {
            this.titleType = titleType;
            return this;
        }

        public TitleBuilder withRuntime(int runtime) {
            this.runtimeMinutes = runtime;
            return this;
        }

        public TitleBuilder setAdult(String isAdult) {
            this.isAdult = Integer.parseInt(isAdult) == 1 ? true : false;
            return this;
        }

        public Title createTitle() {
            return new Title(this.id, this.title,
                    this.titleType, this.runtimeMinutes, this.isAdult);
        }

        public Title createTitleWithMap(HashMap<String, String> data) {
            if (data.get("id") != null) {
                this.id = data.get("id");
            } else {
                this.id = data.get("tconst");
            }
            this.titleType = data.get("titleType");
            this.title = data.get("primaryTitle");
            this.runtimeMinutes = data.get("runtimeMinutes") != null ? Integer.parseInt(data.get("runtimeMinutes")): 0;
            setAdult(data.get("isAdult"));
            return createTitle();
        }
    }
    */
}

