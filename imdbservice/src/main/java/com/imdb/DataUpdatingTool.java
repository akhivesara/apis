package com.imdb;

import com.imdb.databaseimpl.MySQLStore;
import com.imdb.util.ImdbUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class DataUpdatingTool {
    public static void main(String[] args) throws Exception {
        // package as a jar with dependencies
        // maven plugin to package jar with dependency copied
        // manifest by pointing the main class
        fetchAndPopulate("all");
    }

    public static void fetchAndPopulate(String input) {
        /*
        1. Download file/s
        2. save file
        3. populate db
        */
        switch (input) {
            case "title":
                IMDBService.getInstance().fetchAndSaveTitles();
                break;
            case "person":
                IMDBService.getInstance().fetchAndSavePersons();
                break;
            case "episode":
                IMDBService.getInstance().fetchAndSaveEpisodes();
                break;
            case "director":
                IMDBService.getInstance().fetchAndSaveDirector();
                break;
            case "writer":
                IMDBService.getInstance().fetchAndSaveWriter();
                break;
            case "cast":
                IMDBService.getInstance().fetchAndSaveCast();
                break;
            case "rating":
                IMDBService.getInstance().fetchAndSaveRatings();
                break;
            case "genre":
                IMDBService.getInstance().fetchAndSaveGenres();
                break;
            case "all":
                IMDBService.getInstance().fetchAndSaveTitles();
                IMDBService.getInstance().fetchAndSavePersons();
                IMDBService.getInstance().fetchAndSaveEpisodes();
                IMDBService.getInstance().fetchAndSaveCast();
                IMDBService.getInstance().fetchAndSaveRatings();
                IMDBService.getInstance().fetchAndSaveGenres();
                IMDBService.getInstance().fetchAndSaveDirector();
                IMDBService.getInstance().fetchAndSaveWriter();
                break;
        }
        return;
    }

    public static void deleteEntriesInTable(String input) {
        ArrayList<String> tables = new ArrayList<>();
        switch (input) {
            case "title":
                tables.add(ImdbUtils.TITLE_DB_TABLE_NAME);
                break;
            case "person":
                tables.add(ImdbUtils.PERSON_DB_TABLE_NAME);
                break;
            case "episode":
                tables.add(ImdbUtils.EPISODES_DB_TABLE_NAME);
                break;
            case "director":
                tables.add(ImdbUtils.DIRECTOR_DB_TABLE_NAME);
                break;
            case "writer":
                tables.add(ImdbUtils.WRITER_DB_TABLE_NAME);
                break;
            case "rating":
                tables.add(ImdbUtils.RATINGS_DB_TABLE_NAME);
                break;
            case "genre":
                tables.add(ImdbUtils.GENRE_DB_TABLE_NAME);
                break;
            case "cast":
                tables.add(ImdbUtils.CAST_DB_TABLE_NAME);
                break;
            case "all":
                tables = new ArrayList<String>(
                        Arrays.asList(ImdbUtils.TITLE_DB_TABLE_NAME,
                                ImdbUtils.PERSON_DB_TABLE_NAME,
                                ImdbUtils.EPISODES_DB_TABLE_NAME,
                                ImdbUtils.DIRECTOR_DB_TABLE_NAME,
                                ImdbUtils.WRITER_DB_TABLE_NAME,
                                ImdbUtils.RATINGS_DB_TABLE_NAME,
                                ImdbUtils.GENRE_DB_TABLE_NAME,
                                ImdbUtils.CAST_DB_TABLE_NAME
                        ));

        }

        if (tables.size() > 0) {
            IMDBConfig config = IMDBConfig.getInstance();
            MySQLStore db = MySQLStore.getInstance(config.getValue(IMDBConfig.IMDBConfigKeys.DB_PATH), config.getValue(IMDBConfig.IMDBConfigKeys.DB_USER), config.getValue(IMDBConfig.IMDBConfigKeys.DB_PASSWORD));
            for (String table : tables) {
                db.delete(table);
            }
        }
    }
}
