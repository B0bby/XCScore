import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScore {
    private String DATA_FILE_LOCATION;
    private String TEAM_FILE_NAME;
    private String RACE_FILE_NAME;
    private String RUNNER_FILE_NAME;
    private DatabaseHandler database;

    public XCScore(){
        DATA_FILE_LOCATION = "data/";
        TEAM_FILE_NAME = "teams.csv";
        RACE_FILE_NAME = "races.csv";
        RUNNER_FILE_NAME = "runners.csv";
        initializeDatabaseConnection(
                "xcscore",
                "192.168.1.10:3306",
                "xcscore",
                "password"
        );
        //readDefaultCSVFilesIntoDatabase();
    }

    public void readDefaultCSVFilesIntoDatabase(){
        System.out.println("Attempting to read data from default CSV files.\n");
        System.out.println("Reading:");

        readFileIntoTable("Teams", TEAM_FILE_NAME);
        readFileIntoTable("Races", RACE_FILE_NAME);
        readFileIntoTable("Runners", RUNNER_FILE_NAME);

        System.out.println("Database update complete.\n");
    }

    public void readFileIntoTable(String tableName, String fileName){
        String errorInfo = "";
        System.out.print(tableName + "... ");
        try {
            errorInfo =
                    database.readCSVIntoDatabase(DATA_FILE_LOCATION + fileName, tableName);
            System.out.println("Finished.");

            if (errorInfo != ""){
                System.out.println("Some errors occurred:");
                System.out.println(errorInfo);
            }
        }
        catch (IOException e){ System.out.println("FAILED. No file found.\n");}
        catch (SQLException e) { e.printStackTrace(); }
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
            System.out.println("Previous database connection detected. \nDisconnecting...");
            System.out.print("Re-");
        }
        catch (Exception e){
            System.out.println("Database not connected yet.");
        }

        System.out.print("Connecting... ");
        database = new DatabaseHandler(dbname, address, user, pass);
        database.openDatabaseConnection();
        System.out.println("Conneced.\n");
    }

    public String[][] getAllRunnerData(){
        String query = "SELECT bibNumber, CONCAT( firstName,  \", \", lastName ) , teamID FROM  `Runners` WHERE 1" ;

        return database.readTableFromDatabase(query);
    }

    public String[] getSingleRunnerData(String bibNumber){
        String query = "SELECT CONCAT( firstName,  \", \", lastName ) , teamID FROM Runners WHERE bibNumber=" + bibNumber;

        return database.readRowFromDatabase(query);
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

    public String scoreRace(String[][] resultsData){
        HashMap<Integer, ArrayList<Object[]>> teamResults = new HashMap<Integer, ArrayList<Object[]>>();
        int lowestCommonDenom = 1;

        int racePosition = 1;
        for (String[] rowData : resultsData){
            int key = Integer.parseInt(rowData[2]);
            Object[] runner = new Object[]{racePosition, rowData[0],rowData[1],rowData[3]};
            if (teamResults.containsKey(key)){
                ArrayList runnerScores = teamResults.get(key);
                runnerScores.add(runner);
                teamResults.put(key, runnerScores);
            } else {
                ArrayList runnerScores = new ArrayList<Object[]>();
                runnerScores.add(runner);
                teamResults.put(key, runnerScores);
            }
            racePosition++;
        }

        int smallestTeamSize = 0;
        int teamCount = 0;
        for (int key : teamResults.keySet()){
            int teamSize = 0;
            for (Object[] runner : teamResults.get(key)){
                teamSize++;
            }
            if (teamCount == 0){
                smallestTeamSize = teamSize;
            }
            if (teamSize < smallestTeamSize){
                smallestTeamSize = teamSize;
            }
            teamCount++;
        }
        if (smallestTeamSize > 3){
            lowestCommonDenom = 3;
        } else {
            lowestCommonDenom = smallestTeamSize;
        }

        String[][] finalScores = new String[teamResults.size()][3];
        Set teamKeys = teamResults.keySet();
        int teamIndex = 0;
        for (Object teamKey : teamKeys){
            finalScores[teamIndex][0] = teamKey.toString();
            int score = 0;
            int runnersCounted = 0;
            for (Object[] teamRunner : teamResults.get(teamKey)){
                if (runnersCounted >= lowestCommonDenom){
                    break;
                }
                score += (Integer)teamRunner[0];
                finalScores[teamIndex][2] += teamRunner[2] + "\n";
                runnersCounted++;
            }
            finalScores[teamIndex][1] = Integer.toString(score);
            teamIndex++;
        }

        for (int x = 0; x < finalScores.length; x++){
            int lowestScoreIndex = x;
            int lowestScore = Integer.parseInt(finalScores[x][1]);
            for (int y = x; y < finalScores.length; y++){
                int nextScore = Integer.parseInt(finalScores[y][1]);
                if (lowestScore > nextScore){
                    lowestScore = nextScore;
                    lowestScoreIndex = y;
                }
            }
            String[] scoreHolder = finalScores[x];
            finalScores[x] = finalScores[lowestScoreIndex];
            finalScores[lowestScoreIndex] = scoreHolder;
        }

        String rankingDisplay = "";
        for (String[] team : finalScores){
            rankingDisplay += "Place: " + team[1] + "\n";
            rankingDisplay += "Team #" + team[0] + ": " +
                    database.getTeamNameFromID(team[0]) + "\n";
            rankingDisplay += "\n";
        }

        return rankingDisplay;
    }

    public static void main(String args[]){
        XCScore xcscore = new XCScore();
    }
}
