import java.io.*;
import java.util.ArrayList;
import java.sql.*;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHandler {
    private Connection connection = null;
    private ResultSet resultSet = null;
    private String DBNAME;
    private String URL;
    private String USER;
    private String PASS;

    public DatabaseHandler(){
        this.DBNAME = "xcscore";
        this.URL = "jdbc:mysql://localhost:3306/";
        this.USER = "xcscore";
        this.PASS = "password";
    }

    public DatabaseHandler(String dbname, String address, String user, String pass){
        this.DBNAME = dbname;
        this.URL = "jdbc:mysql://" + address + "/";
        this.USER = user;
        this.PASS = pass;
    }

    public void openDatabaseConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String myUrl = URL + DBNAME;
            String user = USER;
            String password = PASS;

            connection = DriverManager.getConnection(myUrl, user, password);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public void closeDatabaseConnection(){
        try{ connection.close(); }
        catch (Exception e){ e.printStackTrace(); }
    }

    public void writeToDatabase(String query) throws SQLException {
        openDatabaseConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch (SQLException e){
            throw(e);
        }

        closeDatabaseConnection();
    }

    private void internalWriteToDatabase(String query) {
        openDatabaseConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch (SQLException e){ e.printStackTrace(); }

        closeDatabaseConnection();
    }

    public void addRowToDatabase(String table, Object[] valueArray) throws SQLException {
        String values = valueListBuilder(valueArray);
        String query = "INSERT INTO " + table + " " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }

    public void updateRowInDatabase(String table, Object[] valueArray) throws SQLException {
        String[] columnNames = getColumnNames(table);
        String updateStatement = "";

        for (int index = 1; index < columnNames.length; index++){
            String column = columnNames[index];
            Object value = valueArray[index];

            updateStatement += column + "=";

            if (value.getClass() == String.class)
            { updateStatement += "'" + value + "'"; }
            else if (value.getClass() == Integer.class)
            { updateStatement += value.toString(); }

            if (index < columnNames.length - 1)
            { updateStatement += ","; }
        }

        String query = "UPDATE " + table + " " +
                "SET " + updateStatement +
                " WHERE " + columnNames[0] + "=" + valueArray[0];
        writeToDatabase(query);
    }

    private String valueListBuilder(Object[] values){
        String valueList = "";

        int index = 1;
        for (Object value : values){
            if (value.getClass() == String.class)
                { valueList += "'" + value + "'"; }
            else if (value.getClass() == Integer.class)
                { valueList += value.toString(); }
            if (index < values.length)
                { valueList += ","; }
            index++;
        }

        return valueList;
    }

    private String[] getColumnNames(String tableName){
        String[] columnNames;
        String query = "SELECT  COLUMN_NAME \n" +
                       "FROM    INFORMATION_SCHEMA.COLUMNS C \n" +
                       "WHERE   TABLE_NAME = '" + tableName + "'";

        columnNames = readColumnFromDatabase(query);
        return columnNames;
    }

    public boolean rowExists (String tableName, Object rowID){
        String[] columnNames = getColumnNames(tableName);
        boolean rowExists = false;
        String query = "SELECT * FROM " + tableName + " WHERE " + columnNames[0] + "=" + rowID;

        try { rowExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return rowExists;
    }

    private boolean checkIfExists(String query){
        boolean doesExist = false;
        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            doesExist = resultSet.next();
        }
        catch (Exception e){ e.printStackTrace(); }

        closeDatabaseConnection();

        return doesExist;
    }

    public String[][] readTableFromDatabase(String query){
        ArrayList<String[]> data = new ArrayList<String[]>();

        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int columnCount = resultSet.getMetaData().getColumnCount();
            int rowCount = resultSet.getRow();

            if (!resultSet.next()){
                closeDatabaseConnection();
                return new String[0][0];
            } else { resultSet.beforeFirst(); }

            while(resultSet.next())
            {
                String[] row = new String[columnCount];
                for (int i=0; i <columnCount ; i++)
                {
                    row[i] = resultSet.getString(i + 1);
                }
                data.add(row);
            }
        }
        catch (Exception e){ e.printStackTrace(); }

        closeDatabaseConnection();

        return data.toArray(new String[data.size()][data.get(0).length]);
    }

    public String[] readColumnFromDatabase(String query){
        ArrayList<String> data = new ArrayList<String>();
        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (!resultSet.next()){
                closeDatabaseConnection();
                return new String[0];
            } else { resultSet.beforeFirst(); }

            while (resultSet.next()){
                data.add(resultSet.getString(1));
            }
        }
        catch (Exception e){ e.printStackTrace(); }

        closeDatabaseConnection();

        return data.toArray(new String[data.size()]);
    }

    public String[] readRowFromDatabase(String query){
        ArrayList<String> data = new ArrayList<String>();
        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int columnCount = resultSet.getMetaData().getColumnCount();

            if (!resultSet.next()){
                closeDatabaseConnection();
                return new String[0];
            } else { resultSet.beforeFirst(); }

            while (resultSet.next()){
                for (int index = 0; index < columnCount; index++){
                    data.add(resultSet.getString(index + 1));
                }
            }
        }
        catch (Exception e){ e.printStackTrace(); }

        closeDatabaseConnection();

        return data.toArray(new String[data.size()]);
    }

    public void createRace(String name){
        String values = valueListBuilder(new String[]{ name });
        String query = "INSERT INTO Races (raceName) " +
                       "VALUES (" + values + ")";
        internalWriteToDatabase(query);
    }

    public String readCSVIntoDatabase(String fileName, String tableName) throws IOException, SQLException {
        String errorInfo = "";

        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (bufferedReader.ready()){
                try {
                    String nextLine = bufferedReader.readLine();
                    Object[] rowData = nextLine.split(",");

                    int rowID = Integer.parseInt((String)rowData[0]);
                    if (rowExists(tableName, rowID))
                        { updateRowInDatabase(tableName, rowData); }
                    else
                        { addRowToDatabase(tableName, rowData); }
                }
                catch (Exception e){ errorInfo += e.getMessage() + "\n";}
            }
        }
        catch (FileNotFoundException e){ throw(e); }
        catch (IOException e){ throw(e); }

        return errorInfo;
    }

    public String getTeamNameFromID(String teamID){
        String query = "SELECT teamName FROM Teams WHERE teamID=" + teamID;
        return readRowFromDatabase(query)[0];
    }
}

// REMEMBER THIS QUERY: SELECT `Teams`.`teamName` FROM `Runners` INNER JOIN `Teams` ON (`Teams`.`teamID`=`Runners`.`teamID`) WHERE 1