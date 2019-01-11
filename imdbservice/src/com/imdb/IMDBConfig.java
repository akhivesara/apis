package com.imdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Used to read properties off config.property. Exposes all possible property keys as {@link IMDBConfigKeys}
 *
 */
public class IMDBConfig {

    private static class SingletonHelper{
        private static final IMDBConfig INSTANCE = new IMDBConfig();
    }

    public static IMDBConfig getInstance(){
        return SingletonHelper.INSTANCE;
    }

    /**
     * Property Keys defined in config.properties file
     */
    public enum IMDBConfigKeys {
        IMDB_BASE_PATH("imdbbasepath"),
        DB_PATH("database"),
        DB_USER("dbuser"),
        DB_PASSWORD("dbpassword"),
        USER_BASE_PATH("userbasepath");

        private final String configKeys;

        IMDBConfigKeys(final String text) {
            this.configKeys = text;
        }

        @Override
        public String toString() {
            return this.configKeys;
        }
    }

    /**
     * Getter for specific property values
     * @param key   {@link IMDBConfigKeys}
     * @return
     */
    public String getValue(IMDBConfigKeys key) {
        return properties != null && key != null ? properties.getProperty(key.toString()) : null;
    }

    private Properties properties;

    private IMDBConfig() {
        build();
    }

    private IMDBConfig build() {
        properties = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            // build properties file
            properties.load(input);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

}
