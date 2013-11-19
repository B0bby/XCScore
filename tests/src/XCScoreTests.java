import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/16/13
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class XCScoreTests {
    public DatabaseHandler database = new DatabaseHandler();

    @Before
    public void setUp(){

    }

    @Test
    public void testShouldPass(){
        assert (1 == 1);
    }
    @Test
    public void testShouldFail(){
        assert (1 == 0);
    }
    @Test
    public void runnerAddedToDatabase(){

    }
    @Test
    public void teamIsAddedToDatabase(){

    }
    @Test
    public void playerTimeIsUpdated(){

    }
}
