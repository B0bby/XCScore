/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScore {

    public static void main(String args[]){
        Race race = new Race();
        DatabaseHandler database = new DatabaseHandler();

        database.readTeamCSVInfoIntoDatabase("data/teams.csv");
        database.readRunnerCSVInfoIntoDatabase("data/runners.csv");
    }
}
