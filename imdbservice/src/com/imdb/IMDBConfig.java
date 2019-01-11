package com.imdb;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class IMDBConfig {

    public enum IMDBConfigKeys {
        DB_USER("dbuser"),
        DB_PASSWORD("dbpassword"),
        DB_PATH("database");

        private final String configKeys;

        IMDBConfigKeys(final String text) {
            this.configKeys = text;
        }

        @Override
        public String toString() {
            return this.configKeys;
        }
    }

    private Properties properties;
    public IMDBConfig() {}

    public IMDBConfig build() {

        properties = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");
            // build a properties file
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

    public String getValue(IMDBConfigKeys key) {
        return properties != null && key != null ? properties.getProperty(key.toString()) : null;
    }
}
