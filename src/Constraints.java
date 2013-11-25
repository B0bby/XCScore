import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/23/13
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constraints {
    private GridBagConstraints constraints = new GridBagConstraints();

    public static GridBagConstraints defaults = new GridBagConstraints(
            0,  // gridx
            0,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.BOTH,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            0   // ipady
    );

    public static GridBagConstraints splitTable = new GridBagConstraints(
            0,  // gridx
            0,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.BOTH,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            0   // ipady
    );

    public static GridBagConstraints otherTable = new GridBagConstraints(
            0,  // gridx
            0,  // gridy
            2,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.BOTH,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            0   // ipady
    );

    public static GridBagConstraints timeClock = new GridBagConstraints(
            0,  // gridx
            0,  // gridy
            2,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.NORTH,  // anchor
            GridBagConstraints.NONE,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            50   // ipady
    );

    public static GridBagConstraints startButton = new GridBagConstraints(
            0,  // gridx
            1,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.PAGE_END,  // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            40   // ipady
    );

    public static GridBagConstraints stopButton = new GridBagConstraints(
            1,  // gridx
            1,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.PAGE_END,  // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            40   // ipady
    );

    public static GridBagConstraints splitButton = new GridBagConstraints(
            0,  // gridx
            2,  // gridy
            2,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.PAGE_END,  // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            40   // ipady
    );

    public static GridBagConstraints addButton = new GridBagConstraints(
            0,  // gridx
            1,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            0,  // weighty
            GridBagConstraints.SOUTH,  // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            24   // ipady
    );

    public static GridBagConstraints delButton = new GridBagConstraints(
            1,  // gridx
            1,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            0,  // weighty
            GridBagConstraints.SOUTH,  // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            24   // ipady
    );

    public static GridBagConstraints mainPanelTable = new GridBagConstraints(
            0,  // gridx
            0,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.BOTH,  // fill
            new Insets(0,0,0,0),
            200,  // ipadx
            0   // ipady
    );

    public static GridBagConstraints mainPanelButtons = new GridBagConstraints(
            1,  // gridx
            0,  // gridy
            1,  // gridwidth
            1,  // gridheight
            1,  // weightx
            1,  // weighty
            GridBagConstraints.NORTH,  // anchor
            GridBagConstraints.BOTH,  // fill
            new Insets(0,0,0,0),
            0,  // ipadx
            50   // ipady
    );
}
