package com.imdb;

import com.imdb.databaseimpl.MySQLStore;
import com.imdb.dbvaluator.*;
import com.imdb.model.Episode;
import com.imdb.model.IMDBBaseEntity;
import com.imdb.model.Rating;
import com.imdb.model.Title;
import com.imdb.model.credits.*;
import com.imdb.util.FileDownloader;
import com.imdb.util.ImdbUtils;
import com.imdb.util.PathUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class IMDBService {

    private static MySQLStore mySQLStore;

    private IMDBService() {}

    private static class SingletonHelper{
        private static final IMDBService INSTANCE = new IMDBService();
    }

    public static IMDBService getInstance(){
        return SingletonHelper.INSTANCE;
    }

    // worry about multi-thread?
    private static MySQLStore getMySQLStore() {
        if (mySQLStore == null) {
            IMDBConfig config = IMDBConfig.getInstance();
            mySQLStore = MySQLStore.getInstance(config.getValue(IMDBConfig.IMDBConfigKeys.DB_PATH), config.getValue(IMDBConfig.IMDBConfigKeys.DB_USER), config.getValue(IMDBConfig.IMDBConfigKeys.DB_PASSWORD));
        }
        return mySQLStore;
    }

    public void fetchAndSaveRatings() {
        /*
            1. Download file/s
            2. save file
            3. populate db
         */
        ArrayList<IMDBBaseEntity> ratings = fetchAndSaveByIdentifierAndClass(ImdbUtils.RATINGS_IDENTIFIER, Rating.class);
        try {
            getMySQLStore().populateUsingBatchInsert(ratings, 0, new RatingDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveEpisodes() {
        /*
            1. Download file/s
            2. save file
            3. populate db
         */
        ArrayList<IMDBBaseEntity> episodes = fetchAndSaveByIdentifierAndClass(ImdbUtils.EPISODES_IDENTIFIER, Episode.class);
        try {
            getMySQLStore().populateUsingBatchInsert(episodes, 0, new EpisodeDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<IMDBBaseEntity> fetchAndSaveByIdentifierAndClass(String identifier, Class type) {
        /*
            1. Download file/s
            2. save file
            3. populate db
         */
        FileDownloader.downloadIfNeeded(identifier, false);
        return readLinesAndStoreInClassType(PathUtils.getLocalFinalPath(identifier), type);
    }

    public void fetchAndSavePersons() {
        ArrayList<IMDBBaseEntity>  persons = fetchAndSaveByIdentifierAndClass(ImdbUtils.PERSON_IDENTIFIER, Person.class);
        try {
            getMySQLStore().populateUsingBatchInsert(persons, 0, new PersonDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveTitles() {
        ArrayList<IMDBBaseEntity>  titles = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_IDENTIFIER, Title.class);
        try {
            getMySQLStore().populateUsingBatchInsert(titles, 0, new TitleDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveCast() {
        fetchAndSaveCastImpl(new String[]{ImdbUtils.CAST_DB_TABLE_NAME}, ImdbUtils.CAST_IDENTIFIER);
    }

    public void fetchAndSaveGenres() {
        System.out.println("fetchAndSaveGenres");
        ArrayList<IMDBBaseEntity>  titles = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_IDENTIFIER, Title.class);
        try {
            getMySQLStore().populateUsingBatchInsert(titles, 0, new GenreDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveDirector() {
        System.out.println("fetchAndSaveDirector");
        try {
            fetchAndSaveDirectorOrWriter(new DirectorDBValuator(), Director.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveWriter() {
        System.out.println("fetchAndSaveWriter");
        try {
            fetchAndSaveDirectorOrWriter(new WriterDBValuator(), Writer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchAndSaveDirectorOrWriter(AbstractDBValuator valuator, Class type) {
        System.out.println("fetchAndSaveDirectorOrWriter");
        ArrayList<IMDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_CREW_IDENTIFIER, type);
        getMySQLStore().populateUsingBatchInsert(crew, 0, valuator);
    }

    private void fetchAndSaveCastImpl(String []tableNames, String identifier) {
        System.out.println("fetchAndSaveCastImpl");
        ArrayList<IMDBBaseEntity>  entities = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);
        if (entities != null && entities.size() > 0) {
            try {
                getMySQLStore().populateUsingBatchInsert(entities, 0, new PersonCategoryDBValuator());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<IMDBBaseEntity> readLinesAndStoreInClassType(String filePath, Class type) {
        try{
            System.out.println("Start Reading lines for "+filePath);
            BufferedReader buf = new BufferedReader(new FileReader(filePath));
            String lineJustFetched;
            String[] wordsArray;
            String[] keysArray = new String[10];
            HashMap<String, String> lineMap;
            HashMap<String, String> commaValueLineMap;
            String[] commaValues = new String[] {};
            ArrayList<IMDBBaseEntity> dataListIMDBObject = new ArrayList<>();

            Constructor<?> cons = type.getConstructor(HashMap.class);
            Object object;
            int lineCount = 0;
            while(true) {

                lineJustFetched = buf.readLine();
                lineMap = new HashMap<>();

                if(lineJustFetched == null) {
                    break;
                }else{
                    lineCount++;

                    wordsArray = lineJustFetched.split("\t");
                    // Skip 1st line
                    if (lineCount == 1) {
                        // save keys as a copy
                        keysArray = wordsArray.clone();
                        continue;
                    }


                    for (int i=0; i < wordsArray.length ; i++) {
                        //for(String each : wordsArray){
                        String each = wordsArray[i];

                        if("\\N".equals(each)) {
                            each = null;
                        }

                        // for crew only
                        Boolean isCrew = type.getCanonicalName().equals(Crew.class.getCanonicalName());


                        commaValues = isCrew  && each != null ? each.split(",") : new String[] {};
                        // 1 a  b
                        // crew with t=1 d=a w=b
                        // 2 c,d    e
                        // crew with t=2 d=c
                        // crew with t=2 d=d
                        // crew with t=2 w=e
                        // 3 f,g    h,i
                        if (isCrew && commaValues.length > 1) {
                            commaValueLineMap = new HashMap<>();
                            for (int c = 0; c < commaValues.length; c++) {
                                //words.add(commaValues[c]);

                                // build base object
                                commaValueLineMap.put(keysArray[0],lineMap.get(keysArray[0]));
                                commaValueLineMap.put(keysArray[i],commaValues[c]);


//                                Constructor<?> cons = type.getConstructor(HashMap.class);
                                object = cons.newInstance(commaValueLineMap);

                                dataListIMDBObject.add((IMDBBaseEntity)object);


                            }

                        } else {

                            //words.add(each);
                            lineMap.put(keysArray[i], each);

                        }
                    }

                    // if CAST find the actual class to instantiate OR introduce a base class
                    // TODO: Remove PersonCategory dependency from IMDBService
                    if (type.getCanonicalName().equals(APersonCategory.class.getCanonicalName())) {
                        Class resolvedClass = PersonCategory.findClassByPersonCategory(PersonCategory.findByCategory(lineMap.get("category")));

                        Constructor<?> resolvedCons = resolvedClass.getConstructor(HashMap.class);
                        object = resolvedCons.newInstance(lineMap);

                        dataListIMDBObject.add((IMDBBaseEntity) object);
                    } else if (commaValues.length <= 1 && lineMap.keySet().size() > 1) {
                        object = cons.newInstance(lineMap);
                        dataListIMDBObject.add((IMDBBaseEntity) object);
                    }
                }
            }

            buf.close();

            System.out.println("All lines read");
            return dataListIMDBObject;
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // TODO: Consider spiltting populate and retrieval code

    public Title retrieveTitleById(String id) {
        return (Title)getMySQLStore().retrieveFromTableById(id, new TitleDBValuator());
    }

    public Person retrievePersonById(String id) {
        return (Person)getMySQLStore().retrieveFromTableById(id, new PersonDBValuator());
    }

    public Rating retrieveRatingById(String id) {
        return (Rating)getMySQLStore().retrieveFromTableById(id, new RatingDBValuator());
    }

    public HashMap calculateRatingById(String id) {
        return getMySQLStore().calculateRatingById(id, new RatingDBValuator());
    }

    public ArrayList<HashMap> calculateAllTitlesRating(String limit, String offset) {
        return getMySQLStore().calculateAllTitlesRating(new RatingDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
    }

    public ArrayList<IMDBBaseEntity> retrieveCastById(String id) {
        return getMySQLStore().retrieveCastById(id, new PersonCategoryDBValuator());
    }

    public ArrayList<IMDBBaseEntity> retrieveAdultTitles(String limit, String offset) {
        String whereClause = "WHERE isAdult"+"='"+1+"'";
        String orderByClause = "ORDER BY title";
        return getMySQLStore().retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
    }

    public ArrayList<HashMap> retrieveListOfTitlesByType(String type, String limit, String offset) {
        String whereClause = "WHERE "+ "titleType"+"='"+type+"'";
        String orderByClause = "ORDER BY title";
        ArrayList<IMDBBaseEntity> lists = getMySQLStore().retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id"});
    }

    public ArrayList<HashMap> retrieveListOfTitlesByGenre(String genre, String limit, String offset) {
        String whereClause = "WHERE "+ "genre" + "='"+genre+"'";
        ArrayList<IMDBBaseEntity> lists = getMySQLStore().retrieveListOfTitles(whereClause, null, new GenreDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id"});
    }

    public ArrayList<HashMap> retrieveListOfTitlesByName(String query, String limit, String offset) {
        String whereClause = "WHERE "+ "title" + " LIKE '% "+query+"%'";
        String orderByClause = "ORDER BY title";
        ArrayList<IMDBBaseEntity> lists = getMySQLStore().retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id", "title"});
    }

    public ArrayList<HashMap> retrieveListOfPeopleByName(String query, String limit, String offset) {
        String whereClause = "WHERE "+ "primaryName" + " LIKE '% "+query+"%'";
        String orderByClause = "ORDER BY primaryName";
        ArrayList<IMDBBaseEntity> lists = getMySQLStore().retrieveListOfTitles(whereClause, orderByClause, new PersonDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id", "name"});
    }

    private ArrayList<HashMap> convertToKeys(ArrayList<IMDBBaseEntity> entities, String[] keys) {
        ArrayList<HashMap> returnList = new ArrayList<>(entities.size());
        for (int i=0; i < entities.size() ; i++) {
            HashMap value = new HashMap(2);
            IMDBBaseEntity title = entities.get(i);

            for (int k=0; k < keys.length; k++) {
                try {
                    value.put(keys[k],PropertyUtils.getProperty(title, keys[k]));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            returnList.add(value);
        }
        return returnList;
    }

    /*

CREW LOGIC

    public void fetchAndSaveCrew() {
        fetchAndSaveCrewImpl(new String[]{ImdbUtils.DIRECTOR_DB_TABLE_NAME, ImdbUtils.WRITER_DB_TABLE_NAME}, ImdbUtils.TITLE_CREW_IDENTIFIER);
    }

    private void fetchAndSaveCrewImpl(String []tableNames, String identifier) {
        System.out.println("fetchAndSaveCrewImpl");
        ArrayList<IMDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        db.crewBatchInsert(tableNames, crew);
    }

    private void fetchAndSaveCrewImplX(AbstractDBValuator crewValuator, String identifier) {
        System.out.println("fetchAndSaveCrewImplX");
        ArrayList<IMDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        db.pseudoBatchInsert(crew, 0, crewValuator);
    }


     */
}
