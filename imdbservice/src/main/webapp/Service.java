package main.webapp;


import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
     * @param
     * @returnF
     */
    @GET
    @Path("title/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns Title details")
    @ApiResponses(value = {@ApiResponse(code = 200 , message = "OK", response = Title.class)})
    public Response titleDetails(@PathParam("id") String input) {
        //Logger.getLogger().info();
        Title title = IMDBService.getInstance().retrieveTitleById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns Person details")
    public Response personDetails(@PathParam("id") String input) {
        //Logger.getLogger().info();
        Person title = IMDBService.getInstance().retrievePersonById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("title/rating/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns Title ratings")
    public Response ratingDetails(@PathParam("id") String input) {
        Rating title = IMDBService.getInstance().retrieveRatingById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("title/calculaterating/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns recalculated details")
    public Response ratingRedone(@PathParam("id") String input) {
        HashMap title = IMDBService.getInstance().calculateRatingById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("title/cast/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns Title cast details")
    public Response castDetails(@PathParam("id") String input) {
        ArrayList<ImDBBaseEntity> titles = IMDBService.getInstance().retrieveCastById(input);
        HashMap attributes = new HashMap();
        attributes.put("listsKey", "cast");
        String employeeJsonString = new Gson().toJson(listDecorator(titles, attributes));
        return Response.ok(employeeJsonString, MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("lists/calculatedRatings")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns List of all Titles with old and re-calculated ratings")
    public Response calculatedRatings(@QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        ArrayList<HashMap> list = IMDBService.getInstance().calculateAllTitlesRating(limit, offset);
        HashMap attributes = new HashMap();
        attributes.put("listsKey", "titles");
        String employeeJsonString = new Gson().toJson(listDecorator(list, attributes));
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("lists/adultTitles")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns List of all Adult Titles ")
    public Response adult(@QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        ArrayList<ImDBBaseEntity> titles = IMDBService.getInstance().retrieveAdultTitles(limit, offset);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("adult", true);
        String jsonString = new Gson().toJson(listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("lists/type/{type}")
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
    @ApiOperation("Returns List of all Titles particular type")
    public Response titlesByType(@PathParam("type") String input, @QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        ArrayList<HashMap> titles = IMDBService.getInstance().retrieveListOfTitlesByType(input, limit, offset);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("type", input);
        if (limit != null) {
            attributes.put("limit", limit);
        }
        if (offset != null) {
            attributes.put("offset", offset);
        }

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
    @Path("lists/genre/{genre}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Returns List of all Titles particular genre")
    public Response titlesByGenre(@PathParam("genre") String input, @QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        //Logger.getLogger().info();
        ArrayList<HashMap> titles = IMDBService.getInstance().retrieveListOfTitlesByGenre(input, limit, offset);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("genre", input);
        if (limit != null) {
            attributes.put("limit", limit);
        }
        if (offset != null) {
            attributes.put("offset", offset);
        }
        String jsonString = new Gson().toJson(listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("search/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Search for matching titles or people")
    @ApiResponses(value = {@ApiResponse(code = 200 , message = "OK")})
    public Response search(@PathParam("query") String input, @QueryParam("type") String type, @QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        //Logger.getLogger().info();
        ArrayList<HashMap> titles = null;
        ArrayList<HashMap> persons = null;
        ArrayList<HashMap> list = null;
        if (type == null) type = "fallback";
        switch (type) {
            case "person":
                list = IMDBService.getInstance().retrieveListOfPeopleByName(input, limit, offset);
                break;
            case "title":
                list = IMDBService.getInstance().retrieveListOfTitlesByName(input, limit, offset);
                break;
            default: // fallback
                limit = limit != null ? limit : "100";
                persons = IMDBService.getInstance().retrieveListOfPeopleByName(input, limit, offset);
                titles = IMDBService.getInstance().retrieveListOfTitlesByName(input, limit, offset);

        }
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("search", input);
        if (limit != null) {
            attributes.put("limit", limit);
        }
        attributes.put("offset", offset != null ? offset : 0);
        String jsonString;
        if (list != null) {
            jsonString = new Gson().toJson(listDecorator(list, attributes));
        } else {
            attributes.put("title", listDecorator(titles, null));
            attributes.put("person", listDecorator(persons, null));
            jsonString = new Gson().toJson(listDecorator(null,attributes));
        }
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
        if (lists != null) {
            String key = "lists";
            if (attributes != null && attributes.get("listsKey") != null) {
                key = (String)attributes.get("listsKey");
                attributes.remove("listsKey");
            }
            response.put(key, lists);
            response.put("size", lists.size());
        }
        response.put("timestamp", nowString);
        if (attributes != null) {
            response.putAll(attributes);
        }

        return response;


    }
}