/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScore {
    private String DATA_FILE_LOCATION;
    private DatabaseHandler database;

    public XCScore(){
        DATA_FILE_LOCATION = "data/";
        initializeDatabaseConnection(
                "xcscore",
                "192.168.1.10:3306",
                "xcscore",
                "password"
        );
        // readDefaultCSVFilesIntoDatabase();
    }

    public void readDefaultCSVFilesIntoDatabase(){
        try {database.readTeamCSVInfoIntoDatabase(DATA_FILE_LOCATION + "teams.csv");}
        catch (Exception e){ System.out.println("No team CSV file found.");}

        try {database.readRaceCSVInfoIntoDatabase(DATA_FILE_LOCATION + "races.csv");}
        catch (Exception e){ System.out.println("No race CSV file found.");}

        try {database.readRunnerCSVInfoIntoDatabase(DATA_FILE_LOCATION + "runnersShort.csv");}
        catch (Exception e){ System.out.println("No runner CSV file found.");}

        try {database.readRunnerFinishTimesIntoDatabase(DATA_FILE_LOCATION + "finishTimesShort.csv");}
        catch (Exception e){ System.out.println("No finish times CSV file found.");}
    }

    public void changeDataFileLocation(String dataLocation){
        if (dataLocation.substring(dataLocation.length() -1) != "/" ){
            dataLocation = dataLocation + "/";
        }
        DATA_FILE_LOCATION = dataLocation;
    }

    public void initializeDatabaseConnection(String dbname, String address, String user, String pass){
        try {
            database.closeDatabaseConnection();
            System.out.println("Database connection detected. Disconnecting...");
        }
        catch (Exception e){
            System.out.println("Database not connected yet. Connecting...");
        }

        database = new DatabaseHandler(dbname, address, user, pass);
        database.openDatabaseConnection();
    }

    public String[][] getAllRunnerData(){
        String query = "SELECT bibNumber, CONCAT( firstName,  \", \", lastName ) , teamID FROM  `Runners`WHERE 1" ;

        return database.readTableFromDatabase(query);
    }

    public String[] getSingleRunnerData(String bibNumber){
        String query = "SELECT * FROM Runners WHERE bibNumber=" + bibNumber;

        return database.readColumnFromDatabase(query);
    }

    public String[] getBibNumbers(){
        String query = "SELECT bibNumber FROM `Runners` ORDER BY bibNumber ASC";

        return database.readColumnFromDatabase(query);
    }

    public String[][] getAllTeamData(){
        String query = "SELECT * FROM Teams WHERE 1";

        return database.readTableFromDatabase(query);
    }

    public String[][] getAllRaceData(){
        String query = "SELECT * FROM Races WHERE 1";

        return database.readTableFromDatabase(query);
    }

    public static void main(String args[]){
        XCScore xcscore = new XCScore();
    }
}
