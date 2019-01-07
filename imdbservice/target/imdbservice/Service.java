package main.webapp;


import com.google.gson.Gson;
import main.webapp.model.Rating;
import main.webapp.model.Title;
import main.webapp.model.credits.Person;
import main.webapp.util.ImdbUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Path("nflxstudio")
public class Service {


    //
    // TODO CRONJOB
    // TODO Move toDataUpdatingTool
    @GET
    @Path("downloadfile")
    @Produces(MediaType.APPLICATION_JSON)
    public Result download(@QueryParam("file") String input) {
        Result result = new Result("downloadExample");
        result.setFile(input);
        /*
        1. Download file/s
        2. save file
        3. populate db
        */
        switch (result.getFile()) {
            case "title":
                //TODO Multiple files
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
            // TODO: skip as director and writer has this covered
            // CAST DISABLE
//            case "crew":
//                IMDBService.getInstance().fetchAndSaveCrew();
//                break;
        }
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

    /**
     *
     * @param input
     * @param i
     * @return
     */
    @GET
    @Path("title/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response titleDetails(@PathParam("id") String input, @QueryParam("x") String i) {
        //Logger.getLogger().info();
        Title title = IMDBService.getInstance().retrieveTitleById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response personDetails(@QueryParam("id") String input) {
        //Logger.getLogger().info();
        Person title = IMDBService.getInstance().retrievePersonById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("rating")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ratingDetails(@QueryParam("id") String input) {
        //Logger.getLogger().info();
        Rating title = (Rating) IMDBService.getInstance().retrieveRatingById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("adultTitles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response adult() {
        //Logger.getLogger().info();
        ArrayList<ImDBBaseEntity> titles = IMDBService.getInstance().retrieveAdultTitles();
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("adult", true);
        String jsonString = new Gson().toJson(listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("titlesByType")
    @Produces(MediaType.APPLICATION_JSON)
    //short
    //movie
    //tvMovie
    //tvSeries
    //tvEpisode
    //tvShort
    //tvMiniSeries
    //tvSpecial
    //video
    //videoGame
    public Response titlesByType(@QueryParam("type") String input) {
        //Logger.getLogger().info();
        ArrayList<ImDBBaseEntity> titles = IMDBService.getInstance().retrieveListOfTitlesByType(input);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("type", input);
        String jsonString = new Gson().toJson(listDecorator(titles,attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }


    //Documentary
    //Short
    //Animation
    //Comedy
    //Romance
    //Sport
    //News
    //Drama
    //Fantasy
    //Horror
    //Biography
    //Music
    //War
    //Crime
    //Western
    //Family
    //Adventure
    //History
    //Sci-Fi
    //Action
    //Mystery
    //Thriller
    //Musical
    //Film-Noir
    //Game-Show
    //Talk-Show
    //Reality-TV
    //Adult
    @GET
    @Path("titlesByGenre")
    @Produces(MediaType.APPLICATION_JSON)
    public Response titlesByGenre(@QueryParam("genre") String input) {
        //Logger.getLogger().info();
        ArrayList<HashMap> titles = IMDBService.getInstance().retrieveListOfTitlesByGenre(input);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("genre", input);
        String jsonString = new Gson().toJson(listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
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

    //TODO move this
    private HashMap listDecorator(ArrayList lists , HashMap attributes) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        Date now = new Date();
        String nowString = sdf.format(now);

        HashMap response = new HashMap();
        response.put("lists", lists);
        response.put("size", lists.size());
        response.put("timestamp", nowString);
        response.putAll(attributes);

        return response;


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