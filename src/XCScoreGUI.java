import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScoreGUI extends JPanel{
    private DatabaseHandler database = new DatabaseHandler();
    private boolean DEBUG;

    public XCScoreGUI(){
        super(new GridLayout(1,1));
        addAllTabsToWindow();
    }

    public XCScoreGUI(boolean DEBUG){
        super();
        this.DEBUG = DEBUG;
    }

    private void addAllTabsToWindow(){
        JTabbedPane mainTabGroup = new JTabbedPane();

        JComponent mainWindow = buildMainWindowTab();
        mainTabGroup.addTab("Main",mainWindow);
        mainTabGroup.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent runnerViewWindow = buildRunnerViewTab();
        mainTabGroup.addTab("Participant Info",runnerViewWindow);
        mainTabGroup.setMnemonicAt(1, KeyEvent.VK_1);

        JComponent teamViewWindow = buildTeamViewTab();
        mainTabGroup.addTab("Team Info",teamViewWindow);
        mainTabGroup.setMnemonicAt(2, KeyEvent.VK_1);

        JComponent raceViewWindow = buildRaceViewTab();
        mainTabGroup.addTab("Race Info",raceViewWindow);
        mainTabGroup.setMnemonicAt(3, KeyEvent.VK_1);

        add(mainTabGroup);

        mainTabGroup.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    }

    private JComponent buildMainWindowTab(){
        JPanel mainPanel = new JPanel(new GridLayout(1, 1));

        JBTable splitListTable = new JBTable(new RunnerSplitTableModel());
        splitListTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        splitListTable.setFillsViewportHeight(true);
        initColumnSizes(splitListTable);
        setUpTeamColumn(splitListTable, splitListTable.getColumnModel().getColumn(2));

        JButton split = new JButton("Split");

        JLabel placeholder = new JLabel("Placeholder");
        placeholder.setHorizontalAlignment(JLabel.CENTER);

        mainPanel.add(splitListTable);
        mainPanel.add(split);
        mainPanel.add(placeholder);
        return mainPanel;
    }

    private void setUpTeamColumn(JTable splitListTable, TableColumn column) {
        JComboBox teams = new JComboBox();
        String[] teamList = database.getListOfTeams();

        for (String team : teamList){
            teams.addItem(team);
        }
    }

    private void initColumnSizes(JBTable table) {
        RunnerSplitTableModel model = (RunnerSplitTableModel)table.getModel();
        TableColumn column = null;
        Component component = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < model.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            component = headerRenderer.getTableCellRendererComponent(
                    null, column.getHeaderValue(),
                    false, false, 0, 0);
            headerWidth = component.getPreferredSize().width;

            component = table.getDefaultRenderer(model.getColumnClass(i)).
                    getTableCellRendererComponent(
                            table, longValues[i],
                            false, false, 0, i);
            cellWidth = component.getPreferredSize().width;

            if (DEBUG) {
                System.out.println("Initializing width of column "
                        + i + ". "
                        + "headerWidth = " + headerWidth
                        + "; cellWidth = " + cellWidth);
            }

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    private JComponent buildRunnerViewTab(){
        JPanel mainPanel = new JPanel(false);
        JLabel placeholder = new JLabel("Placeholder");
        placeholder.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.setLayout(new GridLayout(1,1));
        mainPanel.add(placeholder);
        return mainPanel;
    }

    private JComponent buildTeamViewTab(){
        JPanel mainPanel = new JPanel(false);
        JLabel placeholder = new JLabel("Placeholder");
        placeholder.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.setLayout(new GridLayout(1,1));
        mainPanel.add(placeholder);
        return mainPanel;
    }

    private JComponent buildRaceViewTab(){
        JPanel mainPanel = new JPanel(false);
        JLabel placeholder = new JLabel("Placeholder");
        placeholder.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.setLayout(new GridLayout(1,1));
        mainPanel.add(placeholder);
        return mainPanel;
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("XCScore GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new XCScoreGUI(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UIManager.put("Swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }

}
