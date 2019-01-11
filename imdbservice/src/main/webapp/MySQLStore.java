package main.webapp;

import main.webapp.dbvaluator.IDBValuator;
import main.webapp.model.ImDBBaseEntity;
import main.webapp.util.ImdbUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation for MySQL
 */
public class MySQLStore {

    private final String url;

    private final String user;
    private final String pwd;

    private MySQLStore(String url, String user, String pwd) {
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }

    public static MySQLStore getInstance(String url, String user, String pwd) {
        return new MySQLStore(url, user, pwd);
    }

    public void populateUsingBatchInsert(ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex, IDBValuator valuator) {

        String tableName = valuator.getDBTable();
        System.out.println(" Start-- populateUsingBatchInsert for : "+tableName);
        String columns = valuator.getColumnsString();
        String duplicateKey = valuator.getDuplicateUpdateColumnString();

        String [] columnsArray = columns.split(",");

        List<String> copies = Collections.nCopies(columnsArray.length, "?");
        String values = String.join(",",copies);
        String sql = "INSERT INTO " + tableName + " (" + columns + ") values (" + values + ")";
        if (duplicateKey != null) {
            sql += " ON DUPLICATE KEY UPDATE "+ duplicateKey +"=?";
        }

        Connection connection = null;
        ImDBBaseEntity dbBaseEntity = null;
        PreparedStatement ps = null;
        final int batchSize = 1;
        int count = 0;
        int entityIndex=0;
        Object entityValue;
        try {
            connection = DriverManager.getConnection(url, user, pwd);
            ps = connection.prepareStatement(sql);

            for (entityIndex = startIndex; entityIndex < dbBaseEntities.size() * .1; entityIndex++) {

                dbBaseEntity = dbBaseEntities.get(entityIndex);

                if (!valuator.isValid(dbBaseEntity)) {
                    System.out.println("Valuator validity fails for "+dbBaseEntity);
                    continue;
                }

                // Get Values
                ArrayList<ArrayList> containerValues = valuator.populate(dbBaseEntity);
                for (int p = 0; p < containerValues.size(); p++) {
                    ArrayList entityValues = containerValues.get(p);
                    for (int v = 0; v < entityValues.size(); v++) {
                        int type = valuator.getColumnsType().get(v);
                        entityValue = entityValues.get(v);
                        if (entityValue == null) {
                            ps.setNull(v + 1, type);
                        } else {
                            switch (type) {
                                case Types.CHAR:
                                    ps.setString(v + 1, (String) entityValue);
                                    break;
                                case Types.INTEGER:
                                    ps.setInt(v + 1, (Integer) entityValue);
                                    break;
                                case Types.BOOLEAN:
                                    ps.setBoolean(v + 1, (Boolean) entityValue);
                                    break;
                                case Types.DOUBLE:
                                    ps.setDouble(v + 1, (Double) entityValue);
                                    break;
                                case Types.DECIMAL:
                                    ps.setBigDecimal(v + 1, (BigDecimal) entityValue);
                                default:
                                    throw new Exception("Unsupported entity value format");
                            }
                        }
                    }
                    ps.addBatch();
                    if (++count % batchSize == 0) {
                        ps.executeBatch();
                    }
                }
            }
            ps.executeBatch(); // insert remaining records
            System.out.println(" Done-- populateUsingBatchInsert");
        } catch(Exception e){
            System.out.println("Pseudo Insert Exception: ex " +e);
            closeConnections(ps, connection);
            if (e instanceof BatchUpdateException) {
                System.out.println("BatchUpdateException: PS " +ps);
                System.out.println("BatchUpdateException: ex " +e);
                System.out.println("BatchUpdateException: Entity count = " + entityIndex + " total = "+ dbBaseEntities.size());
                System.out.println("BatchUpdateException: Entity = " + dbBaseEntity);
            }
            if (entityIndex > 0 && dbBaseEntities != null && (entityIndex + 1) < dbBaseEntities.size()) {
                populateUsingBatchInsert(dbBaseEntities, entityIndex + 1, valuator);
            }
        } finally {
            closeConnections(ps, connection);
        }
    }

    private void closeConnections(PreparedStatement ps, Connection connection) {
        try {
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // TODO: delete table, used only for debugging, DO NOT EXPOSE
    public void delete(String tableName) {
        Connection conn = null;
        Statement stmt = null;

        try {
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Deleting all records into the table...");
            stmt = conn.createStatement();

            String sql = "DELETE FROM " + tableName;
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    // TODO: Consider checking a faster way to know is a table is populated
    public boolean isTableEmpty(String tableName) {
        Connection conn;
        Statement stmt;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            stmt = conn.createStatement();

            String sql = "SELECT * FROM " + tableName;

            ResultSet rs = stmt.executeQuery(sql);

            if (!rs.next()) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }





    /*
      // CREW LOGIC
    public void crewBatchInsert(String[] tableNames, ArrayList<ImDBBaseEntity> crew) {
        for (String tableName: tableNames) {
            insertCrewTypeWithErrorHandler(tableName, crew, 0);
        }
    }

    public void insertCrewTypeWithErrorHandlerX(IDBValuator crewValuator, ArrayList<ImDBBaseEntity> crew, int startCount) {
        pseudoBatchInsert(crew, startCount, crewValuator);
    }

    // recursive error handler
    private void insertCrewTypeWithErrorHandler(String tableName, ArrayList<ImDBBaseEntity> crew, int startCount) {
        //try {
        Connection connection = null;
        Crew cr = null;
        PreparedStatement ps = null;
        final int batchSize = 1;
        int count = 0;
        int crewCount=0;


        // per table
        //for (String table : tableNames) {
        String sql = "INSERT INTO " + tableName + " (tconst,nconst) values (?, ?) ON DUPLICATE KEY UPDATE tconst=?";

        try {
            connection = DriverManager.getConnection(url, user, pwd);
            ps = connection.prepareStatement(sql);


            for (crewCount=startCount ; crewCount < crew.size(); crewCount++) {

                cr = (Crew) crew.get(crewCount);
                ps.setString(1, cr.getTitleId());
                // hack for now
                String crewId;
                if (tableName.equals(ImdbUtils.DIRECTOR_DB_TABLE_NAME)) {
                    crewId = cr.getDirector().getId();
                    //ps.setString(2, cr.getDirector().getId());
                } else {
                    crewId = cr.getWriter().getId();
                    //ps.setString(2, cr.getWriter().getId());
                }
                if (crewId == null) {
                    continue;
                }
                ps.setString(2, crewId);
                ps.setString(3, cr.getTitleId());

                ps.addBatch();

                if (++count % batchSize == 0) {

                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
        } catch(Exception e){
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }catch (Exception ex) {
              ex.printStackTrace();
            }
            if (e instanceof BatchUpdateException) {
                System.out.println("BatchUpdateException: PS " +ps);
                System.out.println("BatchUpdateException: crewCount count = " + crewCount);
                System.out.println("BatchUpdateException: Crew = " + cr);
            }
            if (crewCount > 0 && crew != null && (crewCount + 1) < crew.size()) {
                insertCrewTypeWithErrorHandler(tableName, crew, crewCount + 1);
            }
            System.out.println(" Done-- insertCrewTypeWithErrorHandler");
        }
    }
    */

    public ArrayList<ImDBBaseEntity> retrieveCastById(String id, IDBValuator dbValuator) {

        ArrayList<ImDBBaseEntity> data = new ArrayList<>(0);
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            String sql = "select title, primaryName, category , title.tconst, person.nconst" +
                    " from " + ImdbUtils.TITLE_DB_TABLE_NAME +
                    " join " + ImdbUtils.CAST_DB_TABLE_NAME +
                    " on "+ImdbUtils.TITLE_DB_TABLE_NAME+".tconst = "+ImdbUtils.CAST_DB_TABLE_NAME+".tconst" +
                    " join " + ImdbUtils.PERSON_DB_TABLE_NAME +
                    " on "+ImdbUtils.CAST_DB_TABLE_NAME+".nconst = "+ImdbUtils.PERSON_DB_TABLE_NAME+".nconst" +
                    " where "+ ImdbUtils.TITLE_DB_TABLE_NAME +".tconst='"+ id +"'";

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            while (rs.next()) {
                data.add(dbValuator.entityPerResultSet(rs));
            }
            st.close();

        } catch (Exception e) {
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return data;
    }

    public ImDBBaseEntity retrieveFromTableById(String id, IDBValuator dbValuator) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            dbValuator.validateRetrieveInputs();
            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String sql = "SELECT * FROM " + dbValuator.getDBTable() + " WHERE "+ dbValuator.getColumnForPrimaryKey()+"='"+id+"'";

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            if (rs.next()) {
                return dbValuator.retrieve(rs);
            }
            st.close();

        } catch (Exception e) {
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return null;
    }

    public ArrayList<ImDBBaseEntity> retrieveListOfTitles(String whereClause, String orderByClause, IDBValuator dbValuator, Integer limit, Integer offset) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            //dbValuator.validateRetrieveInputs();
            String sql = "SELECT * FROM " + dbValuator.getDBTable() + " " + whereClause;

            if (orderByClause != null) {
                sql += " "+orderByClause;
            }

            if (limit != null) {
                sql += " LIMIT "+limit.intValue();
            }
            if (offset !=null) {
                sql += " OFFSET "+offset.intValue();
            }

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            ArrayList<ImDBBaseEntity> l = new ArrayList<>(0);

            while (rs.next()) {
                ImDBBaseEntity title = dbValuator.retrieve(rs);
                l.add(title);
            }
            st.close();
            return l;

        } catch (Exception e) {
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return null;
    }

    // default limit=0 --> no limit offset=0
    public ArrayList<ImDBBaseEntity> retrieveListOfTitles(String whereClause, IDBValuator dbValuator) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            //dbValuator.validateRetrieveInputs();
            String sql = "SELECT * FROM " + dbValuator.getDBTable() + " " + whereClause;

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            ArrayList<ImDBBaseEntity> l = new ArrayList<>(0);

            while (rs.next()) {
                ImDBBaseEntity title = dbValuator.retrieve(rs);
                l.add(title);
            }
            st.close();
            return l;

        } catch (Exception e) {
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return null;
    }

    public HashMap calculateRatingById(String id, IDBValuator dbValuator) {
        HashMap data = null;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            // our SQL SELECT query.
            String sql = "WITH aggregate AS (select episode.parentId AS tconst, round(avg(rating.averageRating),2) AS averageRating" +
            " from episode" +
            " join rating" +
            " on episode.id = rating.tconst" +
            " group by episode.parentId)" +
            " select * from aggregate" +
            " where tconst='"+ id +"'";

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            if (rs.next()) {
                data = new HashMap();
                data.put("titleId", rs.getString("tconst"));
                data.put("averageRating", rs.getDouble("averageRating"));
            }
            st.close();

        } catch (Exception e) {
            data = null;
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return data;
    }

    public ArrayList<HashMap> calculateAllTitlesRating(IDBValuator dbValuator, Integer limit, Integer offset) {
        ArrayList<HashMap> list = new ArrayList<>(0);
        HashMap data = null;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            // our SQL SELECT query.
            String sql = "with aggregate AS (select episode.parentId AS showId, round(avg(rating.averageRating),2) AS newRating" +
                    " from episode" +
                    " join rating" +
                    " on episode.id = rating.tconst" +
                    " group by episode.parentId)" +
                    " select * from aggregate" +
                    " join title" +
                    " on title.tconst = showId" +
                    " join rating" +
                    " on rating.tconst = showId"
                    ;

            if (limit != null) {
                sql += " LIMIT "+limit.intValue();
            }
            if (offset !=null) {
                sql += " OFFSET "+offset.intValue();
            }

            // create the java statement
            Statement st = connection.createStatement();

            // populate the query, and get a java resultset
            rs = st.executeQuery(sql);

            // iterate through the java resultset
            while (rs.next()) {
                data = new HashMap();
                data.put("id", rs.getString("showId"));
                data.put("averageRating", rs.getDouble("averageRating"));
                data.put("newAverageRating", rs.getString("newRating"));
                data.put("title", rs.getString("title"));
                data.put("numVotes", rs.getDouble("numVotes"));
                list.add(data);
            }
            st.close();

        } catch (Exception e) {
            System.out.println("retrieveTitleById ex "+e);
            System.out.println("retrieveTitleById result set "+rs);
        }
        return list;

    }
}