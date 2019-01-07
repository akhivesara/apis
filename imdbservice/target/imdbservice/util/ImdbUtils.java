package main.webapp.util;

import java.util.HashMap;
import java.util.Map;

public class ImdbUtils {


    public static String RATINGS_IDENTIFIER = "title.ratings";

    public static String RATINGS_DB_TABLE_NAME = "rating";

    public static String EPISODES_IDENTIFIER = "title.episode";

    public static String EPISODES_DB_TABLE_NAME = "episode";

    public static String PERSON_IDENTIFIER = "name.basics";

    public static String PERSON_DB_TABLE_NAME = "person";

    public static String TITLE_IDENTIFIER = "title.basics";

    public static String TITLE_DB_TABLE_NAME = "title";

    public static String TITLE_CREW_IDENTIFIER = "title.crew";

    public static String DIRECTOR_DB_TABLE_NAME = "director_title";
    public static String WRITER_DB_TABLE_NAME = "writer_title";

    public static String CAST_DB_TABLE_NAME = "cast_title";
    public static String CAST_IDENTIFIER = "title.principals";

    public static String GENRE_DB_TABLE_NAME = "genre_title";

    public static final Map<String, Object> TITLE_MAP;
    static
    {
        String TITLE_IDENTIFIER1 = "title.akas";
        String TITLE_IDENTIFIER2 = "title.basics";

        TITLE_MAP = new HashMap<String, Object>();
        TITLE_MAP.put("identifier", new String[]{TITLE_IDENTIFIER1, TITLE_IDENTIFIER2});
        TITLE_MAP.put("dbkeys", new String[][]{
                                    new String[]{"titleId", "title", "region", "language","isOriginalTitle"},
                                    new String[]{"tconst", "titleType", "primaryTitle", "originalTitle", "isAdult"}
                                }
        );
        TITLE_MAP.put("linkedKey", new String[]{"titleId"});
        TITLE_MAP.put("linkedKeyMapper", new HashMap<String, String>().put("titleId","tconst"));

        TITLE_MAP.put(TITLE_IDENTIFIER1 +"_DBKeys", new String[]{"titleId", "title", "region", "language","isOriginalTitle"});
        TITLE_MAP.put(TITLE_IDENTIFIER2 +"_DBKeys", new String[]{"tconst", "titleType", "primaryTitle", "originalTitle", "isAdult"});
        TITLE_MAP.put(TITLE_IDENTIFIER2 +"_foreignDBKey", "tconst");
        TITLE_MAP.put(TITLE_IDENTIFIER2 +"_foreignDBKeyMapper", new HashMap<String, String>().put("tconst", "titleId"));
    }


}
