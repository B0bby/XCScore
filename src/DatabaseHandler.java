import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
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

    private void openDatabaseConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String dbName = "xcscore";
            String myUrl = "jdbc:mysql://localhost:3306/" + dbName;
            String user = "xcscore";
            String password = "password";

            connection = DriverManager.getConnection(myUrl, user, password);
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private void closeDatabaseConnection(){
        try{ connection.close(); }
        catch (Exception e){ e.printStackTrace(); }
    }

    private void writeToDatabase(String query){
        openDatabaseConnection();

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch (Exception e){
            e.printStackTrace();
//            System.out.println("Some key in query doesn't exist:");
//            System.out.println(query);
        }

        closeDatabaseConnection();
    }

    private ResultSet readFromDatabase(String query){
        openDatabaseConnection();

        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }
        catch (Exception e){ e.printStackTrace(); }

        closeDatabaseConnection();

        return resultSet;
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

    public void createRace(String name){
        String values = "'" + name + "'";
        String query = "INSERT INTO Races (raceName) " +
                       "VALUES (" + values + ")";
        writeToDatabase(query);
    }

    public void readRunnerCSVInfoIntoDatabase(String fileName){
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int bibNumber = 1;
            while (bufferedReader.ready()){
                String nextLine = bufferedReader.readLine();
                String[] runnerInfo = nextLine.split(",");

                addRunner(bibNumber,
                    runnerInfo[0],
                    runnerInfo[1],
                    runnerInfo[2],
                    runnerInfo[3],
                    runnerInfo[4]);

                bibNumber++;
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public boolean runnerExists(int bibNumber){
        boolean runnerExists = false;
        String query = "SELECT * FROM Runners WHERE bibNumber=" + bibNumber;

        try { runnerExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return runnerExists;
    }

    public void addRunner(int bibNumber, String firstName, String lastName, String gender, String phoneNum, String teamID){
        if (runnerExists(bibNumber)){
            updateRunner(bibNumber, firstName, lastName, gender, phoneNum, teamID);
        } else {
            String values = "'" + bibNumber + "', '" +
                                  firstName + "', '" +
                                  lastName  + "', '" +
                                  gender    + "', '" +
                                  phoneNum  + "', " +
                                  teamID    + "";
            String query = "INSERT INTO Runners (bibNumber, firstName, lastName, gender, phoneNum, teamID) " +
                           "VALUES (" + values + ")";
            writeToDatabase(query);
        }
    }

    public void updateRunner(int bibNumber, String firstName, String lastName, String gender, String phoneNum, String teamID){
        String query = "UPDATE Runners SET firstName='" + firstName +
                "', lastName='" + lastName +
                "', gender='" + gender +
                "', phoneNum='" + phoneNum +
                "', teamID='" + teamID +
                "'  WHERE bibNumber=" + bibNumber;
        writeToDatabase(query);
    }

    public void readRunnerOffsetTimesIntoDatabase(String fileName){
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (bufferedReader.ready()){
                String nextLine = bufferedReader.readLine();
                String[] runnerInfo = nextLine.split(",");

                recordStartOffset(runnerInfo[0],
                        runnerInfo[1],
                        runnerInfo[2]);
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public boolean startOffsetExists(String bibNumber, String raceID){
        boolean startOffsetExists = false;
        String query = "SELECT * FROM Results WHERE raceID=" + raceID + " AND bibNumber=" + bibNumber;

        try { startOffsetExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return startOffsetExists;
    }

    public void recordStartOffset(String bibNumber, String raceID, String startOffset){
        if (startOffsetExists(bibNumber, raceID)){
            updateStartOffset(bibNumber, raceID, startOffset);
        } else {
            String values = bibNumber + ", " + raceID + ", '" + startOffset + "'";
            String query = "INSERT INTO Results " +
                    "(bibNumber, raceID, startOffset)" +
                    "VALUES (" + values +")";
            writeToDatabase(query);
        }
    }

    public void updateStartOffset(String bibNumber, String raceID, String startOffset){
        String query = "UPDATE Results SET" +
                " startOffset='" + startOffset + "'" +
                " WHERE raceID=" + raceID + " AND bibNumber=" + bibNumber;
        writeToDatabase(query);
    }

    public void readRunnerFinishTimesIntoDatabase(String fileName){
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int bibNumber = 1;
            while (bufferedReader.ready()){
                String nextLine = bufferedReader.readLine();
                String[] runnerInfo = nextLine.split(",");

                recordFinishTime(runnerInfo[0],
                        runnerInfo[1],
                        runnerInfo[2]);

                bibNumber++;
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public boolean finishTimeExists(String bibNumber, String raceID ){
        boolean finishTimeExists = false;
        String query = "SELECT * FROM Results WHERE raceID=" + raceID + " AND bibNumber=" + bibNumber;

        try { finishTimeExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return finishTimeExists;
    }

    public void recordFinishTime(String bibNumber, String raceID, String finishTime){
        if (finishTimeExists(raceID, bibNumber)){
            updateFinishTime(bibNumber, raceID, finishTime);
        } else {
            String values = bibNumber + ", " + raceID + ", '" + finishTime + "'";
            String query = "INSERT INTO Results " +
                    "(bibNumber, raceID, finishTime)" +
                    "VALUES (" + values +")";
            writeToDatabase(query);
        }
    }

    public void updateFinishTime(String bibNumber, String raceID, String finishTime){
        String query = "UPDATE Results SET" +
                " finishTime='" + finishTime + "'" +
                " WHERE raceID=" + raceID + " AND bibNumber=" + bibNumber;
        writeToDatabase(query);
    }

    public void readTeamCSVInfoIntoDatabase(String fileName){
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int teamID = 1;
            while (bufferedReader.ready()){
                String teamName = bufferedReader.readLine();

                if ( teamExists(teamID) )
                { updateTeam(teamID, teamName);}
                else
                { addTeam(teamID, teamName); }

                teamID++;
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public boolean teamExists(int teamID){
        boolean teamExists = false;
        String query = "SELECT * FROM Teams WHERE teamID=" + teamID;

        try { teamExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return teamExists;
    }

    public void addTeam(int teamID, String teamName){
        String values = teamID + ", '" + teamName + "'";
        String query = "INSERT INTO Teams (teamID, teamName) " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }

    public void updateTeam(int teamID, String teamName){
        String query = "UPDATE Teams SET teamName='" + teamName + "' WHERE teamID=" + teamID;
        writeToDatabase(query);
    }

    public void readRaceCSVInfoIntoDatabase(String fileName) {
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            int raceID = 1;
            while (bufferedReader.ready()){
                String raceName = bufferedReader.readLine();

                if ( raceExists(raceID) )
                { updateRace(raceID, raceName);}
                else
                { addRace(raceID, raceName); }

                raceID++;
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private boolean raceExists(int raceID) {
        boolean raceExists = false;
        String query = "SELECT * FROM Races WHERE raceID=" + raceID;

        try { raceExists = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return raceExists;
    }

    private void addRace(int raceID, String raceName) {
        String values = raceID + ", '" + raceName + "'";
        String query = "INSERT INTO Races (raceID, raceName) " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }

    private void updateRace(int raceID, String raceName) {
        String query = "UPDATE Races SET raceName='" + raceName + "' WHERE raceID=" + raceID;
        writeToDatabase(query);
    }

    public void readRegisteredTeamsIntoDatabase(String fileName) {
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (bufferedReader.ready()){
                String info = bufferedReader.readLine();
                String[] registrationInfo = info.split(",");

                if ( alreadyRegistered(registrationInfo[0], registrationInfo[1]) )
                { continue; }
                else
                { addRegistration(registrationInfo[0], registrationInfo[1]); }
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    private boolean alreadyRegistered(String raceID, String teamID) {
        boolean alreadyRegistered = false;
        String query = "SELECT * FROM RegisteredTeams WHERE raceID=" + raceID + " AND teamID=" + teamID;

        try { alreadyRegistered = checkIfExists(query); }
        catch (Exception e) { e.printStackTrace(); }

        return alreadyRegistered;
    }

    public void addRegistration(String raceID, String teamID){
        String values = raceID + ", " +  teamID;
        String query = "INSERT INTO RegisteredTeams (raceID, teamID) " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }
}

// REMEMBER THIS QUERY: SELECT `Teams`.`teamName` FROM `Runners` INNER JOIN `Teams` ON (`Teams`.`teamID`=`Runners`.`teamID`) WHERE 1