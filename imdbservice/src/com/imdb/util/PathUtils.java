package com.imdb.util;

import com.imdb.IMDBConfig;

public class PathUtils {

    static {
        config = IMDBConfig.getInstance();
    }

    private static IMDBConfig config;

    static String GZIP_EXTENTION = ".tsv.gz";
    static String GZIP_CP_EXTENTION = "-gzip-cp.txt";
    static String ENCODED_EXTENTION = "-encoded.txt";

    public static String getImdbURI(String identifier) {
        return config.getValue(IMDBConfig.IMDBConfigKeys.IMDB_BASE_PATH) + identifier + GZIP_EXTENTION;
    }

    public static String getLocalCopyPath(String identifier) {
        return config.getValue(IMDBConfig.IMDBConfigKeys.USER_BASE_PATH) + identifier + GZIP_CP_EXTENTION;
    }

    public static String getLocalFinalPath(String identifier) {
        return config.getValue(IMDBConfig.IMDBConfigKeys.USER_BASE_PATH) + identifier + ENCODED_EXTENTION;
    }
}
