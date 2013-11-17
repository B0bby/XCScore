import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseConnection {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void readFromDatabase(){
        try
        {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String dbName = "xcscore";
            String myUrl = "jdbc:mysql://192.168.1.10:3306/" + dbName;
            String user = "xcscore";
            String password = "password";

            Connection connection = DriverManager.getConnection(myUrl, user, password);

            String query = "INSERT INTO races (id, name) VALUES ('1','Warrior Dash')";
            Statement statement = connection.createStatement();

            statement.executeUpdate(query);

            connection.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception! ");
            System.err.println(e.fillInStackTrace());
            e.printStackTrace();
        }
    }
}
