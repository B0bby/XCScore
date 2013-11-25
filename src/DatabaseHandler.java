import java.util.ArrayList;
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

    public void writeToDatabase(String query){
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

    public void readRunnerFinishTimesIntoDatabase(String fileName){
        try{
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            while (bufferedReader.ready()){
                String nextLine = bufferedReader.readLine();
                String[] runnerInfo = nextLine.split(",");

                recordFinishTime(runnerInfo[0],
                        runnerInfo[1],
                        runnerInfo[2]);

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

    public void recordFinishTime(String raceID, String bibNumber, String finishTime){
        if (finishTimeExists(raceID, bibNumber)){
            updateFinishTime(bibNumber, raceID, finishTime);
        } else {
            String values = raceID + ", " + bibNumber + ", '" + finishTime + "'";
            String query = "INSERT INTO Results " +
                    "(raceID, bibNumber, finishTime)" +
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