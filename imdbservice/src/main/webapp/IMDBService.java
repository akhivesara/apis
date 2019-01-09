package main.webapp;

import main.webapp.dbvaluator.*;
import main.webapp.model.*;
import main.webapp.model.credits.*;
import main.webapp.util.ImdbUtils;
import main.webapp.util.PathUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IMDBService {

    private static IMDBService instance;

    private static String LOCAL_DB_PATH  = "jdbc:mysql://localhost/nflxtakehome";
    private static String LOCAL_DB_USER = "nflxtakehome";
    private static String LOCAL_DB_PWD = "nflxtakehome";

    private IMDBService(){}

    private static class SingletonHelper{
        private static final IMDBService INSTANCE = new IMDBService();
    }

    public static IMDBService getInstance(){
        return SingletonHelper.INSTANCE;
    }

    public void fetchAndSaveRatings() {
        /*
            1. Download file/s
            2. save file
            3. populate db
         */

        ArrayList<ImDBBaseEntity> ratings = fetchAndSaveByIdentifierAndClass(ImdbUtils.RATINGS_IDENTIFIER, Rating.class);
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        //db.ratingsBatchInsert(ratings);
        try {
            db.executeBatchInsert(ratings, 0, new RatingDBValuator());
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

        ArrayList<ImDBBaseEntity> episodes = fetchAndSaveByIdentifierAndClass(ImdbUtils.EPISODES_IDENTIFIER, Episode.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        //db.episodesBatchInsert(episodes);
        try {
            db.executeBatchInsert(episodes, 0, new EpisodeDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public ArrayList<ImDBBaseEntity> fetchAndSaveByIdentifierAndClass(String identifier, Class type) {
        /*
            1. Download file/s
            2. save file
            3. populate db
         */

        FileDownloader.downloadIfNeeded(identifier);
        return readLinesAndStoreInClassType(PathUtils.getLocalFinalPath(identifier), type);
    }

    public void fetchAndSavePersons() {
        ArrayList<ImDBBaseEntity>  persons = fetchAndSaveByIdentifierAndClass(ImdbUtils.PERSON_IDENTIFIER, Person.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        //db.personsBatchInsert(ImdbUtils.PERSON_DB_TABLE_NAME, persons);
        try {
            db.executeBatchInsert(persons, 0, new PersonDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveTitles() {
        ArrayList<ImDBBaseEntity>  titles = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_IDENTIFIER, Title.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        //db.titlesBatchInsert(ImdbUtils.TITLE_DB_TABLE_NAME, titles);
        try {
            db.executeBatchInsert(titles, 0, new TitleDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchAndSaveCast() {
        fetchAndSaveCastImpl(new String[]{ImdbUtils.CAST_DB_TABLE_NAME}, ImdbUtils.CAST_IDENTIFIER);
    }

    public void fetchAndSaveGenres() {
        System.out.println("fetchAndSaveGenres");
        ArrayList<ImDBBaseEntity>  titles = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_IDENTIFIER, Title.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        try {
            db.executeBatchInsert(titles, 0, new GenreDBValuator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //db.insertGenreBatchWithErrorHandler(ImdbUtils.GENRE_DB_TABLE_NAME, titles, 0);
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

    private void fetchAndSaveDirectorOrWriter(IDBValuator valuator, Class type) {
        System.out.println("fetchAndSaveDirectorOrWriter");
        ArrayList<ImDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(ImdbUtils.TITLE_CREW_IDENTIFIER, type);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        db.executeBatchInsert(crew, 0, valuator);
    }

    private void fetchAndSaveCastImpl(String []tableNames, String identifier) {
        System.out.println("fetchAndSaveCastImpl");
        ArrayList<ImDBBaseEntity>  entities = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);

        if (entities != null && entities.size() > 0) {
            MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
            //db.insertCastBatchWithErrorHandler(tableNames[0], entities, 0);
            try {
                db.executeBatchInsert(entities, 0, new PersonCategoryDBValuator());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ArrayList<ImDBBaseEntity> readLinesAndStoreInClassType(String filePath, Class type) {
        try{
            System.out.println("Start Reading lines for "+filePath);
            BufferedReader buf = new BufferedReader(new FileReader(filePath));
            String lineJustFetched;
            String[] wordsArray;
            String[] keysArray = new String[10];
            HashMap<String, String> lineMap;
            HashMap<String, String> commaValueLineMap;
            String[] commaValues = new String[] {};
            ArrayList<Map> dataList = new ArrayList<>();
            ArrayList<ImDBBaseEntity> dataListIMDBObject = new ArrayList<>();

            Constructor<?> cons = type.getConstructor(HashMap.class);
            Object object;
            int lineCount = 0;
            while(true) {

                lineJustFetched = buf.readLine();
                lineMap = new HashMap<>();

                if(lineJustFetched == null) {
                    //lineCount = 0;
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

                                dataListIMDBObject.add((ImDBBaseEntity)object);


                            }

                        } else {

                            //words.add(each);
                            lineMap.put(keysArray[i], each);

                        }
                    }



                    // if CAST find the actual class to instantiate OR introduce a base class
                    // TODO: better solution
                    if (type.getCanonicalName().equals(APersonCategory.class.getCanonicalName())) {
                        Class resolvedClass = PersonCategory.findClassByPersonCategory(PersonCategory.findByCategory(lineMap.get("category")));

                        Constructor<?> resolvedCons = resolvedClass.getConstructor(HashMap.class);
                        object = resolvedCons.newInstance(lineMap);

                        dataListIMDBObject.add((ImDBBaseEntity) object);
                    } else if (commaValues.length <= 1 && lineMap.keySet().size() > 1) {
                        object = cons.newInstance(lineMap);

                        dataListIMDBObject.add((ImDBBaseEntity) object);
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


    public Title retrieveTitleById(String id) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        //return db.retrieveTitleById(id);
        return (Title)db.retrieveFromTableById(id, new TitleDBValuator());
    }

    public Person retrievePersonById(String id) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        return (Person)db.retrieveFromTableById(id, new PersonDBValuator());
    }

    public Rating retrieveRatingById(String id) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        return (Rating)db.retrieveFromTableById(id, new RatingDBValuator());
    }

    public ArrayList<ImDBBaseEntity> retrieveAdultTitles(String limit, String offset) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        String whereClause = "WHERE isAdult"+"='"+1+"'";
        String orderByClause = "ORDER BY title";
        return db.retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
    }

    public ArrayList<HashMap> retrieveListOfTitlesByType(String type, String limit, String offset) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        String whereClause = "WHERE "+ "titleType"+"='"+type+"'";
        String orderByClause = "ORDER BY title";
        ArrayList<ImDBBaseEntity> lists = db.retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id"});
    }

    public ArrayList<HashMap> retrieveListOfTitlesByGenre(String genre, String limit, String offset) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        String whereClause = "WHERE "+ "genre" + "='"+genre+"'";
        String orderByClause = "ORDER BY title";
        ArrayList<ImDBBaseEntity> lists = db.retrieveListOfTitles(whereClause, orderByClause, new GenreDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id"});
    }

    public ArrayList<HashMap> retrieveListOfTitlesByName(String query, String limit, String offset) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        String whereClause = "WHERE "+ "title" + " LIKE '% "+query+"%'";
        String orderByClause = "ORDER BY title";
        ArrayList<ImDBBaseEntity> lists = db.retrieveListOfTitles(whereClause, orderByClause, new TitleDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id", "title"});
    }

    public ArrayList<HashMap> retrieveListOfPeopleByName(String query, String limit, String offset) {
        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        String whereClause = "WHERE "+ "primaryName" + " LIKE '% "+query+"%'";
        String orderByClause = "ORDER BY primaryName";
        ArrayList<ImDBBaseEntity> lists = db.retrieveListOfTitles(whereClause, orderByClause, new PersonDBValuator(), limit != null ? Integer.valueOf(limit) : null, offset != null ? Integer.valueOf(offset) : null);
        return convertToKeys(lists, new String[]{"id", "name"});
    }

    private ArrayList<HashMap> convertToKeys(ArrayList<ImDBBaseEntity> entities, String[] keys) {
        ArrayList<HashMap> returnList = new ArrayList<>(entities.size());
        for (int i=0; i < entities.size() ; i++) {
            HashMap value = new HashMap(2);
            ImDBBaseEntity title = entities.get(i);

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
    OLD CODE

    public void fetchAndSaveTitlesX() {
//
//            1. Download files
//            2. save files
//            3. populate memory object (consider writing to a file or persisting object)
//            4. populate db


//    FileDownloader.downloadMultipleFiles((String[]) ImdbUtils.TITLE_MAP.get("identifier"));
      readMultipleFiles();
    }

        public void fetchAndSaveDirector() {
        fetchAndSaveCrewImpl(new String[]{ImdbUtils.DIRECTOR_DB_TABLE_NAME}, ImdbUtils.TITLE_CREW_IDENTIFIER);
    }

    public void fetchAndSaveWriter() {
        fetchAndSaveCrewImpl(new String[]{ImdbUtils.WRITER_DB_TABLE_NAME}, ImdbUtils.TITLE_CREW_IDENTIFIER);
    }

        private void readMultipleFiles() {

        //readLinesAndStoreInClassType(PathUtils.getLocalFinalPath(ImdbUtils.EPISODES_IDENTIFIER), Episode.class);

        ArrayList<Map> dataList = new ArrayList<>();
        ArrayList<Map> wantedDataList = new ArrayList<>();
        ArrayList<ImDBBaseEntity> dataListObject = new ArrayList<>();
        HashMap<String, HashMap> titleMapById = new HashMap<String, HashMap>();

        String[] identifiers = (String[]) ImdbUtils.TITLE_MAP.get("identifier");

        for (int fileCount = 0; fileCount < identifiers.length; fileCount ++) {

            String identifier = identifiers[fileCount];

            // read file
            String filePath = PathUtils.getLocalFinalPath(identifier);

            try {
                BufferedReader buf = new BufferedReader(new FileReader(filePath));
                ArrayList<String> words = new ArrayList<>();
                String lineJustFetched;
                String[] wordsArray;
                String[] keysArray = new String[10];
                HashMap<String, String> lineMap;
                HashMap<String, String> wantedLineMap;


                int lineCount = 0;
                while (true) {

                    lineJustFetched = buf.readLine();
                    lineMap = new HashMap<>();
                    wantedLineMap = new HashMap<>();
                    if (lineJustFetched == null) {
                        //lineCount = 0;
                        break;
                    } else {
                        lineCount++;
                        wordsArray = lineJustFetched.split("\t");
                        // Skip 1st line
                        if (lineCount == 1) {
                            // save keys as a copy
                            keysArray = wordsArray.clone();
                            continue;
                        }

                        for (int w = 0; w < wordsArray.length; w++) {
                            //for(String each : wordsArray){
                            String each = wordsArray[w];
                            if ("\\N".equals(each)) {
                                each = null;
                            }
                            // RATINGS
                            words.add(each);
                            lineMap.put(keysArray[w], each);
                            // filter only what you need, here or make another copy later of what you need?
                        }
                        String[] dbKeys = (String[]) ImdbUtils.TITLE_MAP.get(identifier+"_DBKeys");

                        for (String key : dbKeys) {
                            wantedLineMap.put(key, lineMap.get(key));
                        }
//                        if (fileCount > 0) {
//                            String[] linkedKey = (String[]) ImdbUtils.TITLE_MAP.get("linkedKey");
//                            break;
//                            //linkedKey
//                        } else {
                            // Make Title objects
                            String titleId = lineMap.get("titleId");

                            if (titleMapById.get(titleId) != null) {
                                HashMap<String, String> titleMap = titleMapById.get(titleId);
                                // append lineMap with title Map
                                titleMap.putAll(lineMap);
                                titleMapById.put(titleId, titleMap);

                            } else {
                                titleMapById.put(titleId, lineMap);

                            }
//                        }


                        dataList.add(lineMap);
                        wantedDataList.add(wantedLineMap);

//                    Constructor<?> cons = type.getConstructor(HashMap.class);
//                    Object object = cons.newInstance(lineMap);
//
//                    dataListRatings.add((ImDBBaseEntity)object);
                    }
                }
                System.out.println("Total "+ lineCount +"Lines for file "+fileCount +" with file identifier "+identifier);

                buf.close();


               // return dataListRatings;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



CREW LOGIC

    public void fetchAndSaveCrew() {
        fetchAndSaveCrewImpl(new String[]{ImdbUtils.DIRECTOR_DB_TABLE_NAME, ImdbUtils.WRITER_DB_TABLE_NAME}, ImdbUtils.TITLE_CREW_IDENTIFIER);
    }

    private void fetchAndSaveCrewImpl(String []tableNames, String identifier) {
        System.out.println("fetchAndSaveCrewImpl");
        ArrayList<ImDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        db.crewBatchInsert(tableNames, crew);
    }

    private void fetchAndSaveCrewImplX(IDBValuator crewValuator, String identifier) {
        System.out.println("fetchAndSaveCrewImplX");
        ArrayList<ImDBBaseEntity>  crew = fetchAndSaveByIdentifierAndClass(identifier, APersonCategory.class);

        MySQLStore db = MySQLStore.getInstance(LOCAL_DB_PATH, LOCAL_DB_USER, LOCAL_DB_PWD);
        db.pseudoBatchInsert(crew, 0, crewValuator);
    }


     */
}
