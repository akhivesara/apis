package com.imdb;


import com.google.gson.Gson;
import com.imdb.model.credits.PersonCategory;
import com.imdb.model.IMDBBaseEntity;
import com.imdb.model.Rating;
import com.imdb.model.Title;
import com.imdb.model.credits.Person;
import com.imdb.util.ResponseUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;

@Path("nflxstudio")
public class RestfulService {

    /**
     * Rest API: To fetch title data
     * @path {id}     title ID
     * @return
     */
    @GET
    @Path("title/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response titleDetails(@PathParam("id") String input) {
        Title title = IMDBService.getInstance().retrieveTitleById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch person data
     * @param {id}     person ID
     * @return
     */
    @GET
    @Path("person/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response personDetails(@PathParam("id") String input) {
        Person title = IMDBService.getInstance().retrievePersonById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch title ratings
     * @param {id}     title ID
     * @return
     */
    @GET
    @Path("title/rating/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ratingDetails(@PathParam("id") String input) {
        Rating title = IMDBService.getInstance().retrieveRatingById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch title rating, on re-calculation. Algorithm used is: average of
     * all episode ratings for that show
     * @param {id}     title ID
     * @return
     */
    @GET
    @Path("title/calculatedrating/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ratingRedone(@PathParam("id") String input) {
        HashMap title = IMDBService.getInstance().calculateRatingById(input);
        String employeeJsonString = new Gson().toJson(title);
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch cast info for a title. Possible cast categories are
     * {@link PersonCategory}
     *
     * @param {id}     title ID
     * @return
     */
    @GET
    @Path("title/cast/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response castDetails(@PathParam("id") String input) {
        ArrayList<IMDBBaseEntity> titles = IMDBService.getInstance().retrieveCastById(input);
        HashMap attributes = new HashMap();
        attributes.put("listsKey", "cast");
        String employeeJsonString = new Gson().toJson(ResponseUtil.listDecorator(titles, attributes));
        return Response.ok(employeeJsonString, MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch a list of ratings for all titles that includes both ratings re-calculated
     * and old ratings. Re-calculation Algorithm used is: average of all episode ratings for that show*
     * @param limit     page size for pagination support
     * @param offset    offset for pagination support
     * @return
     */
    @GET
    @Path("lists/titles/calculatedratings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calculatedRatings(@QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        ArrayList<HashMap> list = IMDBService.getInstance().calculateAllTitlesRating(limit, offset);
        HashMap attributes = new HashMap();
        attributes.put("listsKey", "titles");
        String employeeJsonString = new Gson().toJson(ResponseUtil.listDecorator(list, attributes));
        return Response.ok(employeeJsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: To fetch a list of all adult titles on the service.
     * @param limit     page size for pagination support
     * @param offset    offset for pagination support
     * @return
     */
    @GET
    @Path("lists/titles/adultTitles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response adult(@QueryParam("limit") String limit, @QueryParam("offset") String offset) {
        ArrayList<IMDBBaseEntity> titles = IMDBService.getInstance().retrieveAdultTitles(limit, offset);
        HashMap attributes;
        attributes = new HashMap<String, Object>();
        attributes.put("adult", true);
        String jsonString = new Gson().toJson(ResponseUtil.listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     *
     * Rest API: To fetch a list of all titles of type
     * @param {type}    title type, possible values
     * { short |movie |tvMovie |tvSeries |tvEpisode |
     *   tvShort |tvMiniSeries |tvSpecial |video |videoGame }
     *   TODO: Make the above case-insensitive. Consider possibly merging similar types ex: tvSeries, tvMiniSeries. Better have a middleware that does that merge for clients
     * @param limit     page size for pagination support
     * @param offset    offset for pagination support
     * @return
     * @return
     */
    @GET
    @Path("lists/titles/type/{type}")
    @Produces(MediaType.APPLICATION_JSON)

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

        String jsonString = new Gson().toJson(ResponseUtil.listDecorator(titles,attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     *
     * Rest API: To fetch a list of all titles of specified genre
     * @param {genre}    genre, possible values
     * { Documentary |Short |Animation |Comedy |Romance |Sport |News |
     *   Drama |Fantasy |Horror |Biography |Music |War |Crime |Western |
     *   Family |Adventure |History |Sci-Fi |Action |Mystery |Thriller |
     *   Musical |Film-Noir |Game-Show |Talk-Show |Reality-TV |Adult }
     *   TODO: Make the above case-insensitive
     * @param limit     page size for pagination support
     * @param offset    offset for pagination support
     * @return
     * @return
     */
    @GET
    @Path("lists/titles/genre/{genre}")
    @Produces(MediaType.APPLICATION_JSON)
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
        String jsonString = new Gson().toJson(ResponseUtil.listDecorator(titles, attributes));
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API: Search. Currently Supports Title and People.
     * Implementation performs partial word match provided
     * word begins with the query. Support filtering by query param type
     * @param {query}   search query
     * @param type      if type=person returns only people, if type=title
     *                  returns only title. default if no type returns both
     * @param limit     page size for pagination support. default is set at 100
     * @param offset    offset for pagination support
     * @return
     */
    @GET
    @Path("search/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@PathParam("query") String input, @QueryParam("type") String type, @QueryParam("limit") String limit, @QueryParam("offset") String offset) {
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
                // TODO: use a text config file to read this
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
            jsonString = new Gson().toJson(ResponseUtil.listDecorator(list, attributes));
        } else {
            attributes.put("title", ResponseUtil.listDecorator(titles, null));
            attributes.put("person", ResponseUtil.listDecorator(persons, null));
            jsonString = new Gson().toJson(ResponseUtil.listDecorator(null,attributes));
        }
        return Response.ok(jsonString,MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Rest API to download and populate DB. Dev purpose only
     * @param input
     * @return
     */
    @GET
    @Path("downloadfile")
    @Produces(MediaType.APPLICATION_JSON)
    public String download(@QueryParam("file") String input) {
        DataUpdatingTool.fetchAndPopulate(input);
        return "File and DB for "+input +" complete";
    }


    @GET
    @Path("deletefile")
    @Produces(MediaType.APPLICATION_JSON)
    /*
      Used to delete database table. For dev purpose only
     */
    public String delete(@QueryParam("file") String input) {
        DataUpdatingTool.deleteEntriesInTable(input);
        return "DB delete for "+input +" complete";
    }
}