import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
            String myUrl = "jdbc:mysql://192.168.1.10:3306/" + dbName;
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
        catch (Exception e){ e.printStackTrace(); }

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

                if (runnerExists(bibNumber)){
                    updateRunner(bibNumber,
                        runnerInfo[0],
                        runnerInfo[1],
                        runnerInfo[2],
                        runnerInfo[3],
                        runnerInfo[4]);
                }
                else {
                    addRunner(bibNumber,
                        runnerInfo[0],
                        runnerInfo[1],
                        runnerInfo[2],
                        runnerInfo[3],
                        runnerInfo[4]);
                }
                bibNumber++;
            }
        }
        catch (Exception e){ e.printStackTrace(); }
    }

    public boolean runnerExists(int bibNumber){
        boolean runnerExists = false;
        String query = "SELECT * FROM Runners WHERE bibNumber=" + bibNumber;

        try { runnerExists = !readFromDatabase(query).wasNull(); }
        catch (Exception e) { e.printStackTrace(); }

        return runnerExists;
    }

    public void addRunner(int bibNumber, String firstName, String lastName, String gender, String phoneNum, String teamID){
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

    public void updateRunner(int bibNumber, String firstName, String lastName, String gender, String phoneNum, String teamID){
        String query = "UPDATE Runners SET firstName='" + firstName +
                "', lastName='" + lastName +
                "', gender='" + gender +
                "', phoneNum='" + phoneNum +
                "', teamID='" + teamID +
                "'  WHERE bibNumber=" + bibNumber;
        writeToDatabase(query);
    }

    public void setRunnerFinishTime(int bibNumber, Time finishTime){
        String query = "UPDATE Runners " +
                       "SET finishTime=" + finishTime + " " +
                       "WHERE bibNumber=" + bibNumber;
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

        try { teamExists = !readFromDatabase(query).wasNull(); }
        catch (Exception e) { e.printStackTrace(); }

        return teamExists;
    }

    public void addTeam(int teamID, String teamName){
        String values = "'" + teamName + "', '" + "'" + teamID + "'";
        String query = "INSERT INTO Teams (teamName) " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }

    public void updateTeam(int teamID, String teamName){
        String query = "UPDATE Teams SET teamName='" + teamName + "' WHERE teamID=" + teamID;
        writeToDatabase(query);
    }

    public void registerTeam( int raceID, int teamID){
        String values = Integer.toString(raceID) + ", " +
                        Integer.toString(teamID);
        String query = "INSERT INTO RegisteredTeams (raceID, teamID) " +
                "VALUES (" + values + ")";
        writeToDatabase(query);
    }
}

// REMEMBER THIS QUERY: SELECT `Teams`.`teamName` FROM `Runners` INNER JOIN `Teams` ON (`Teams`.`teamID`=`Runners`.`teamID`) WHERE 1