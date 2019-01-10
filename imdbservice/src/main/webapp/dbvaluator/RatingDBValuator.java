package main.webapp.dbvaluator;

import main.webapp.model.ImDBBaseEntity;
import main.webapp.model.Rating;
import main.webapp.util.ImdbUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingDBValuator extends IDBValuator {

    public RatingDBValuator(){}

    @Override
    public String getDBTable() {
        return ImdbUtils.RATINGS_DB_TABLE_NAME;
    }

    @Override
    public String getColumnsString() {
        return "tconst, averageRating, numVotes";
    }

    @Override
    public List<Integer> getColumnsType() {
        return Arrays.asList(Types.CHAR, Types.DOUBLE, Types.INTEGER, Types.DOUBLE);
    }

    @Override
    public String getDuplicateUpdateColumnString() {
        return "averageRating";
    }

    @Override
    public Boolean isValid(ImDBBaseEntity entity) {
        return entity !=null;
    }

    @Override
    public ArrayList valuesPerEntity(ImDBBaseEntity entity) {
        ArrayList v = new ArrayList();
        Rating rating = (Rating) entity;
        v.add(rating.getTitleId());
        v.add(rating.getAverageRating());
        v.add(rating.getTotalVotes());
        v.add(rating.getAverageRating());
        return v;
    }

    @Override
    public ImDBBaseEntity entityPerResultSet(ResultSet rs) throws SQLException {
        String tconst = rs.getString("tconst");
        Double rating = rs.getDouble("averageRating");
        Integer votes = rs.getInt("numVotes");
        return new Rating(tconst, rating, votes);

    }

    @Override
    public String getColumnForPrimaryKey() {
        return "tconst";
    }
}

