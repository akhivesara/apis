package com.imdb.util;

public class PathUtils {

    static String IMDB_BASE_PATH = "https://datasets.imdbws.com/";
    static String GZIP_EXTENTION = ".tsv.gz";
    static String GZIP_CP_EXTENTION = "-gzip-cp.txt";
    static String ENCODED_EXTENTION = "-encoded.txt";
    static String USER_BASE = "/Users/ashishkhivesara/Documents/";


    //https://datasets.imdbws.com/title.crew.tsv.gz

    public static String getImdbURI(String identifier) {
        return IMDB_BASE_PATH + identifier + GZIP_EXTENTION;
    }

    public static String getLocalCopyPath(String identifier) {
        return USER_BASE + identifier + GZIP_CP_EXTENTION;
    }

    public static String getLocalFinalPath(String identifier) {
        return USER_BASE + identifier + ENCODED_EXTENTION;
    }
}
