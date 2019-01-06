package main.webapp;


import main.webapp.util.ImdbUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;

@Path("nflxstudio")
public class Service {


    // POPULATE DB
    // CRONJOB  TASK 2

    @GET
    @Path("downloadfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Result download(@QueryParam("file") String input) {
        Result result = new Result("downloadExample");
        result.setFile(input);
        switch (result.getFile()) {
            case "title":
                //TODO Multiple files
                DatabaseController.getInstance().fetchAndSaveTitles();
                break;
            case "person":
                DatabaseController.getInstance().fetchAndSavePersons();
                break;
            case "episode":
                DatabaseController.getInstance().fetchAndSaveEpisodes();
                break;
            case "director":
                DatabaseController.getInstance().fetchAndSaveDirector();
                break;
            case "writer":
                DatabaseController.getInstance().fetchAndSaveWriter();
                break;

            case "cast":
                DatabaseController.getInstance().fetchAndSaveCast();
                break;
            case "rating":

                /*
                1. Download file/s
                2. save file
                3. populate db
                 */
                DatabaseController.getInstance().fetchAndSaveRatings();
                break;
            case "genre":
                // Multiple files
                DatabaseController.getInstance().fetchAndSaveGenres();
                break;
            // TODO: skip as director and writer has this covered
            // CAST DISABLE
//            case "crew":
//                DatabaseController.getInstance().fetchAndSaveCrew();
//                break;
        }
//        if ("rating".equalsIgnoreCase(result.getFile())) {
//            FileDownloader.downloadRatings();
//        }

        return result;
    }


    @GET
    @Path("deletefile")
    @Produces(MediaType.APPLICATION_JSON)
    /*
      Used to delete database table. For dev purpose only
     */
    public Result delete(@QueryParam("file") String input) {
        Result result = new Result("deleteFile");
        result.setFile(input);
        ArrayList<String> tables = new ArrayList<>();
        switch (result.getFile()) {
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
            MySQLStore db = MySQLStore.getInstance("jdbc:mysql://localhost/nflxtakehome", "nflxtakehome", "nflxtakehome");
            for (String table : tables) {
                db.delete(table);
            }
        }
        return result;
    }

    // CRONJOB TASK 1
    // DEBUGGING FORCE DOWNLOAD FILE
    // TODO ^^

    // OUTPUT!
    static class Result {
        double input;
        double output;
        String action;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        String file;

        public Result(){}

        public Result(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public double getInput() {
            return input;
        }

        public void setInput(double input) {
            this.input = input;
        }

        public double getOutput() {
            return output;
        }

        public void setOutput(double output) {
            this.output = output;
        }
    }

    /*
    OLD CODE

        @GET
    @Path("downloadExample")
    @Produces(MediaType.APPLICATION_JSON)
    public Result download(@QueryParam("input") double input) {
        Result result = new Result("downloadExample");
        result.setInput(input);
        result.setOutput(Math.sqrt(result.getInput()));

        FileDownloader.downloadExample();

        return result;
    }


     */
}