package main.webapp;

import main.webapp.dbvaluator.IDBValuator;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void executeBatchInsert(ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex, IDBValuator valuator) {

        String tableName = valuator.getDBTable();
        System.out.println(" Start-- executeBatchInsert for : "+tableName);
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

            for (entityIndex = startIndex; entityIndex < dbBaseEntities.size(); entityIndex++) {

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
            System.out.println(" Done-- executeBatchInsert");
        } catch(Exception e){
            System.out.println("Pseudo Insert Exception: ex " +e);
            if (e instanceof BatchUpdateException) {
                System.out.println("BatchUpdateException: PS " +ps);
                System.out.println("BatchUpdateException: ex " +e);
                System.out.println("BatchUpdateException: Entity count = " + entityIndex + " total = "+ dbBaseEntities.size());
                System.out.println("BatchUpdateException: Entity = " + dbBaseEntity);
            }
            if (entityIndex > 0 && dbBaseEntities != null && (entityIndex + 1) < dbBaseEntities.size()) {
                executeBatchInsert(dbBaseEntities, entityIndex + 1, valuator);
            }
        } finally {
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


    // CODE REFACTORED TO USE VALUATOR PATTER -- keeping old working code around till testting

    /*
    public void episodesBatchInsert(ArrayList<ImDBBaseEntity> episodes) {
        try {
            String sql = "INSERT INTO " + ImdbUtils.EPISODES_DB_TABLE_NAME + " (id, parentId, seasonNumber, episodeNumber) values (?, ?, ?, ?) ON DUPLICATE KEY UPDATE seasonNumber=?";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            PreparedStatement ps = connection.prepareStatement(sql);

            final int batchSize = 1000;
            int count = 0;

            for (ImDBBaseEntity base : episodes) {

                Episode episode = (Episode)base;
                ps.setString(1, episode.getId());
                ps.setString(2, episode.getParentId());
                if (episode.getSeasonNumber() != null) {
                    ps.setInt(3, episode.getSeasonNumber());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }
                if (episode.getEpisodeNumber() != null) {
                    ps.setInt(4, episode.getEpisodeNumber());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                if (episode.getSeasonNumber() != null) {
                    ps.setInt(5, episode.getSeasonNumber());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }

                ps.addBatch();

                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- episodesBatchInsert");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public void personsBatchInsert(String tableName, ArrayList<ImDBBaseEntity> persons) {
        try {
            String sql = "INSERT INTO " + tableName + " (nconst, primaryName) values (?, ?) ON DUPLICATE KEY UPDATE primaryName=?";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            PreparedStatement ps = connection.prepareStatement(sql);

            final int batchSize = 1000;
            int count = 0;

            for (ImDBBaseEntity base : persons) {

                Person person = (Person) base;
                ps.setString(1, person.getId());
                ps.setString(2, person.getName());
                ps.setString(3, person.getName());
                ps.addBatch();

                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- personsBatchInsert");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // Batch insert
    public void ratingsBatchInsert(ArrayList<ImDBBaseEntity> ratings) {
        try {
            String sql = "INSERT INTO " + ImdbUtils.RATINGS_DB_TABLE_NAME + " (tconst, averageRating, numVotes) values (?, ?, ?) ON DUPLICATE KEY UPDATE averageRating=?";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            PreparedStatement ps = connection.prepareStatement(sql);

            final int batchSize = 1;
            int count = 0;

            for (ImDBBaseEntity base : ratings) {

                Rating rating = (Rating) base;
                ps.setString(1, rating.getTitleId());
                ps.setDouble(2, rating.getAverageRating());
                ps.setInt(3, rating.getTotalVotes());
                ps.setDouble(4, rating.getAverageRating());
                ps.addBatch();

                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- ratingsBatchInsert");
        } catch (Exception e) {
            if (e instanceof BatchUpdateException) {
            }
            e.printStackTrace();
        }
    }

        public void titlesBatchInsert(String tableName, ArrayList<ImDBBaseEntity> titles) {
        try {
            String sql = "INSERT INTO " + tableName + " (tconst, titleType, title, isAdult, runtimeMinutes) values (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE titleType=?";
            Connection connection = DriverManager.getConnection(url, user, pwd);
            PreparedStatement ps = connection.prepareStatement(sql);

            final int batchSize = 1000;
            int count = 0;

            for (ImDBBaseEntity base : titles) {

                Title title = (Title) base;
                ps.setString(1, title.getId());
                ps.setString(2, title.getTitleType());
                ps.setString(3, title.getTitle());
                ps.setInt(4, title.isAdult() ? 1 : 0);
                ps.setInt(5, title.getRuntimeMinutes());
                ps.setString(6, title.getTitleType());
                ps.addBatch();

                if (++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- titlesBatchInsert");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        public void insertCastBatchWithErrorHandler(String tableName, ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex) {

        Connection connection = null;
        ImDBBaseEntity cr = null;
        //type.cast(cr) = null;
        PreparedStatement ps = null;
        final int batchSize = 1;
        int count = 0;
        int entityIndex=0;


        String sql = "INSERT INTO " + tableName + " (tconst, nconst, category) values (?, ?, ?) ON DUPLICATE KEY UPDATE tconst=?";

        try {
            connection = DriverManager.getConnection(url, user, pwd);
            ps = connection.prepareStatement(sql);


            for (entityIndex = startIndex ; entityIndex < dbBaseEntities.size(); entityIndex++) {

                cr = dbBaseEntities.get(entityIndex);
                APersonCategory pc = (APersonCategory) cr;
                ps.setString(1, pc.getTitleId());
                ps.setString(2, pc.getId());
                ps.setString(3, pc.getCategory().toString());
                ps.setString(4, pc.getTitleId());

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
                System.out.println("BatchUpdateException: Entity count = " + entityIndex + " total = "+ dbBaseEntities.size());
                System.out.println("BatchUpdateException: Entity = " + cr);
            }
            if (entityIndex > 0 && dbBaseEntities != null && (entityIndex + 1) < dbBaseEntities.size()) {
                insertCastBatchWithErrorHandler(tableName, dbBaseEntities, entityIndex + 1);
            }
            System.out.println(" Done-- insertCastBatchWithErrorHandler");
        }
    }

        // Genre special case
    public void insertGenreBatchWithErrorHandler(String tableName, ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex) {
        System.out.println(" Start-- insertGenreBatchWithErrorHandler");
        Title dbBaseEntity = null;
        Connection connection = null;
        PreparedStatement ps = null;
        final int batchSize = 1;
        int count = 0;
        int entityIndex=startIndex;

        try {
            String sql = "INSERT INTO " + tableName + " (tconst, genre) values (?, ?) ON DUPLICATE KEY UPDATE tconst=?";
            connection = DriverManager.getConnection(url, user, pwd);
            ps = connection.prepareStatement(sql);

            for (entityIndex = startIndex ; entityIndex < dbBaseEntities.size(); entityIndex++) {

                dbBaseEntity = (Title) dbBaseEntities.get(entityIndex);
                String titleGenres = dbBaseEntity.getGenres();

                String[] genres = titleGenres != null ? titleGenres.split(",") : new String[] {};
                for (String genre : genres) {
                    ps.setString(1, dbBaseEntity.getId());
                    ps.setString(2, genre);
                    ps.setString(3, dbBaseEntity.getId());
                    ps.addBatch();

                    if (++count % batchSize == 0) {
                        ps.executeBatch();
                    }
                }
                entityIndex++;
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- insertGenreBatchWithErrorHandler");
        } catch (Exception e) {
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
            if (e instanceof BatchUpdateException) {
                //BatchUpdateException bE = (BatchUpdateException) e;
                System.out.println("BatchUpdateException: PS " + ps);
                System.out.println("BatchUpdateException: Ex " + e);
                System.out.println("BatchUpdateException: Entity count = " + entityIndex + " total = " + dbBaseEntities.size());
                System.out.println("BatchUpdateException: Entity = " + dbBaseEntity);
            }
            if (entityIndex >= 0 && dbBaseEntities != null && (entityIndex + 1) < dbBaseEntities.size()) {
                insertGenreBatchWithErrorHandler(tableName, dbBaseEntities, entityIndex + 1);
            }
            System.out.println(" Done-- insertGenreBatchWithErrorHandler");
        }
    }


    public void pseudoBatchInsert(ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex, IDBValuator valuator) {
        pseudoBatchInsert(valuator.getDBTable(), dbBaseEntities, startIndex, valuator.getColumnsString(), valuator, valuator.getDuplicateUpdateColumnString());
    }

    private void pseudoBatchInsert(String tableName, ArrayList<ImDBBaseEntity> dbBaseEntities, int startIndex, String columns, IDBValuator valuator, String duplicateKey) {
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

        try {
            connection = DriverManager.getConnection(url, user, pwd);
            ps = connection.prepareStatement(sql);

            for (entityIndex = startIndex ; entityIndex < dbBaseEntities.size(); entityIndex++) {

                dbBaseEntity = dbBaseEntities.get(entityIndex);

                if (!valuator.isValid(dbBaseEntity)) {
                    System.out.println("Valuator validity fails for "+dbBaseEntity);
                    continue;
                }

                // Get Values
                ArrayList vals = valuator.valuesPerEntity(dbBaseEntity);
                for (int v = 0; v < vals.size(); v++) {

                    //TODO: consider setting NULL?
                    if (vals.get(v) instanceof String) {
                        ps.setString(v+1,(String)vals.get(v));
                    } else if (vals.get(v) instanceof Boolean) {
                        ps.setBoolean(v+1,(Boolean) vals.get(v));
                    } else if (vals.get(v) instanceof Double) {
                        ps.setDouble(v + 1, (Double) vals.get(v));
                    } else if (vals.get(v) instanceof Integer) {
                        ps.setInt(v+1, ((Integer) vals.get(v)).intValue());
                    } else {
                        // default to string
                        ps.setString(v+1, vals.get(v).toString());
                    }
                }

                ps.addBatch();

                if (++count % batchSize == 0) {

                    ps.executeBatch();
                }
            }
            ps.executeBatch(); // insert remaining records
            ps.close();
            connection.close();
            System.out.println(" Done-- pseudoBatchInsert");
        } catch(Exception e){
            try {
                System.out.println("Pseudo Insert Exception: ex " +e);
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
                System.out.println("BatchUpdateException: ex " +e);
                System.out.println("BatchUpdateException: Entity count = " + entityIndex + " total = "+ dbBaseEntities.size());
                System.out.println("BatchUpdateException: Entity = " + dbBaseEntity);
            }
            if (entityIndex > 0 && dbBaseEntities != null && (entityIndex + 1) < dbBaseEntities.size()) {
                pseudoBatchInsert(tableName, dbBaseEntities, entityIndex + 1, columns, valuator, duplicateKey);
            }
        }
    }


    // CREW LOGIC

        // crew
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

    public ImDBBaseEntity retrieveFromTableById(String id, IDBValuator dbValuator) {
        //kldfl;asd
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            dbValuator.validateRetrieveInputs();
            // our SQL SELECT query.
            // if you only need a few columns, specify them by name instead of using "*"
            String sql = "SELECT * FROM " + dbValuator.getDBTable() + " WHERE "+ dbValuator.getColumnForPrimaryWhereClauseById()+"='"+id+"'";

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

    public ArrayList<ImDBBaseEntity> retrieveListOfTitles(String whereClause, IDBValuator dbValuator, Integer limit, Integer offset) {
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = DriverManager.getConnection(url, user, pwd);

            //dbValuator.validateRetrieveInputs();
            String sql = "SELECT * FROM " + dbValuator.getDBTable() + " " + whereClause;

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

}