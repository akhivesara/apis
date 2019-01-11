package com.imdb.model.credits;

/**
 * PersonCategory ENUM represents all the possible
 * category a cast can be
 */
public enum PersonCategory {
    DIRECTOR("director"),
    WRITER("writer"),
    CINEMATOGRAPHER("cinematographer"),
    COMPOSER("composer"),
    PRODUCER("producer"),
    ACTOR("actor"),
    ACTRESS("actress"),
    EDITOR("editor"),
    SELF("self"),
    UNKNOWN("unknown");

    private final String category;

    PersonCategory(final String text) {
        this.category = text;
    }

    @Override
    public String toString() {
        return this.category;
    }

    public static PersonCategory findByCategory(String category) {
        for (PersonCategory person : values()) {
            if (person.category.equals(category)) {
                return person;
            }
        }
        return UNKNOWN;
    }

    public static Class findClassByPersonCategory(PersonCategory personCategory) {
        switch (personCategory) {
            case SELF:
                return Self.class;
            case ACTOR:
                return Actor.class;
            case EDITOR:
                return Editor.class;
            case WRITER:
                return Writer.class;
            case DIRECTOR:
                return Director.class;
            case ACTRESS:
                return Actress.class;
            case COMPOSER:
                return Composer.class;
            case PRODUCER:
                return Producer.class;
            case CINEMATOGRAPHER:
                return Cinematographer.class;
            default:
                return Unknown.class;
        }
    }

    // TODO
    /*
    public static Class findClassByCategory(String personCategory) {
        switch (personCategory) {
            case SELF.toString():
                return Self.class;
            case ACTOR:
                return Actor.class;
            case EDITOR:
                return Editor.class;
            case WRITER:
                return Writer.class;
            case DIRECTOR:
                return Director.class;
            case ACTRESS:
                return Actress.class;
            case COMPOSER:
                return Composer.class;
            case PRODUCER:
                return Producer.class;
            case CINEMATOGRAPHER:
                return Cinematographer.class;
            default:
                return Unknown.class;
        }
    }
    */

}