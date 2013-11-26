import javax.swing.*;

import com.intellij.ui.TableCell;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteComboBoxEditor;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.TableView;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScoreGUI extends JPanel{
    private int TIMER_GRANULARITY = 10;
    private boolean DEBUG = false;

    private XCScore xcscore = new XCScore();
    private Timer timer;
    private int hour, minute, second;
    private long millisecond;

    final SplitTableModel splitTableModel = new SplitTableModel(DEBUG, xcscore);
    final RunnerTableModel runnerTableModel = new RunnerTableModel(DEBUG);
    final TeamTableModel teamTableModel = new TeamTableModel(DEBUG);
    final RaceTableModel raceTableModel = new RaceTableModel(DEBUG);
    final JBTable splitTable = new JBTable(splitTableModel);
    final JLabel timeClock = new JLabel("00:00:00");
    final JButton startButton = new JButton("Start");
    final JButton stopButton = new JButton("Stop");
    final JButton splitButton = new JButton("Split");
    final JButton addRunnerButton = new JButton("Add");
    final JButton delRunnerButton = new JButton("Delete");
    final JButton addTeamButton = new JButton("Add");
    final JButton delTeamButton = new JButton("Delete");
    final JButton addRaceButton = new JButton("Add");
    final JButton delRaceButton = new JButton("Delete");
    final JButton scoreRaceButton = new JButton("Score Race");
    final JComboBox bibNumbers = new JComboBox(xcscore.getBibNumbers());

    public XCScoreGUI(){
        addAllTabsToWindow();

        attachActionListeners();

        updateAllTablesFromDatabase();
    }

    private void updateAllTablesFromDatabase(){
        String[][] runnerData = xcscore.getAllRunnerData();
        runnerTableModel.readArrayIntoTable(runnerData);

        String[][] teamData = xcscore.getAllTeamData();
        teamTableModel.readArrayIntoTable(teamData);

        String[][] raceData = xcscore.getAllRaceData();
        raceTableModel.readArrayIntoTable(raceData);
    }

    private void attachActionListeners(){
        ActionListener timerAction = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                timeClock.setText(timePassed(false));
            }
        };
        timer = new Timer(TIMER_GRANULARITY, timerAction);

        ActionListener splitButtonAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addSplit();
            }
        };
        splitButton.addActionListener(splitButtonAction);

        ActionListener startButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                timer.start();

                deleteAllSplits();

                scoreRaceButton.setEnabled( false );
                scoreRaceButton.setFont(new Font("Helvetica", Font.PLAIN, 20));

                splitButton.setEnabled( true );
                splitButton.setFont(new Font("Helvetica", Font.BOLD, 24));

                stopButton.setEnabled( true );
                stopButton.setFont(new Font("Helvetica", Font.BOLD, 20));

                startButton.setEnabled( false );
                startButton.setFont(new Font("Helvetica", Font.PLAIN, 20));
            }
        };
        startButton.addActionListener(startButtonListener);

        ActionListener stopButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (splitTableModel.getRowCount() > 0){
                    scoreRaceButton.setEnabled( true );
                    scoreRaceButton.setFont(new Font("Helvetica", Font.BOLD, 20));
                }

                splitButton.setEnabled( false );
                splitButton.setFont(new Font("Helvetica", Font.PLAIN, 24));

                stopButton.setEnabled( false );
                stopButton.setFont(new Font("Helvetica", Font.PLAIN, 20));

                startButton.setEnabled( true );
                startButton.setFont(new Font("Helvetica", Font.BOLD, 20));

                hour = minute = second = 0;
                millisecond = 0;
                timer.stop();
            }
        };
        stopButton.addActionListener(stopButtonListener);

        ActionListener scoreRaceButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayResults();
            }
        };
        scoreRaceButton.addActionListener(scoreRaceButtonListener);
    }

    private String[][] packageTableData(DefaultTableModel table){
        int filledRowCount = 0;
        int columnCount = table.getColumnCount();
        Vector tableModelData = table.getDataVector();
        Object[] tableModelArray = tableModelData.toArray();
        String[][] finalDataArray;

        filledRowCount = countFilledRows(tableModelArray);
        finalDataArray = new String[filledRowCount][columnCount];

        int row = 0;
        for (Object rowArray : tableModelArray){
            Vector rowDataVector = (Vector)rowArray;
            Object[] rowDataArray = rowDataVector.toArray();
            if (!rowDataArray[0].equals("")){
                for (int column = 0; column < columnCount; column++){
                    finalDataArray[row][column] = (String)rowDataArray[column];
                }
                row++;
            }
        }
        return finalDataArray;
    }

    private int countFilledRows(Object[] tableModelArray){
        int filledRowCount = 0;

        for (Object rowArray : tableModelArray){
            Vector rowDataVector = (Vector)rowArray;
            Object[] rowDataArray = rowDataVector.toArray();
            if (!rowDataArray[0].equals("")){
                filledRowCount++;
            }
        }
        return filledRowCount;
    }

    private void deleteAllSplits(){
        int rowTotal = splitTableModel.getRowCount();
        int rowIndex = 0;
        while (rowIndex < rowTotal){
            splitTableModel.removeRow(0);
            rowIndex++;
        }
    }

    private void addSplit(){
        splitTableModel.addRow(new Object[]{"","","", timePassed(true)});
    }

    private String timePassed(boolean returnMilliseconds) {
        String hours, minutes, seconds;
            hours = minutes = seconds = "";
        String time = "";

        millisecond += TIMER_GRANULARITY;

        if ((millisecond % 1000) == 0){ second++;}
        if (second == 60){ minute++; second = 0; }
        if (minute == 60){ hour++; minute = 0; }

        if (hour < 10){ hours = "0" + hour; }
        else { hours = "" + hour; }
        if (minute < 10){ minutes = ":0" + minute; }
        else { minutes = ":" + minute; }
        if (second < 10){seconds = ":0" + second; }
        else { seconds = ":" + second; }

        if (returnMilliseconds){
            time = "" + millisecond;
        } else {
            time = hours + minutes + seconds;
        }

        return time;
    }

    private void addAllTabsToWindow(){
        JTabbedPane mainTabGroup = new JTabbedPane();
        mainTabGroup.setPreferredSize(new Dimension(640, 480));

        JComponent mainWindow = buildMainViewTab();
        mainTabGroup.addTab("Main",mainWindow);
        mainTabGroup.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent runnerViewWindow = buildRunnerViewTab();
        mainTabGroup.addTab("Participants",runnerViewWindow);
        mainTabGroup.setMnemonicAt(1, KeyEvent.VK_1);

        JComponent teamViewWindow = buildTeamViewTab();
        mainTabGroup.addTab("Teams",teamViewWindow);
        mainTabGroup.setMnemonicAt(2, KeyEvent.VK_1);

//        JComponent raceViewWindow = buildRaceViewTab();
//        mainTabGroup.addTab("Races",raceViewWindow);
//        mainTabGroup.setMnemonicAt(3, KeyEvent.VK_1);

        add(mainTabGroup);
    }

    private JComponent buildMainViewTab(){
        JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel tablePanel = new JPanel(new GridBagLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JPanel startAndStopButtons = new JPanel(new GridLayout());
        JPanel splitAndScoreButtons = new JPanel(new GridLayout(2,1));

        splitButton.setEnabled( false );
        stopButton.setEnabled( false );
        scoreRaceButton.setEnabled( false );

        splitButton.setFont(new Font("Helvetica", Font.PLAIN, 24));
        stopButton.setFont(new Font("Helvetica", Font.PLAIN, 20));
        startButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        scoreRaceButton.setFont(new Font("Helvetica", Font.PLAIN, 20));
        timeClock.setFont(new Font("Helvetica", Font.BOLD, 40));

        stopButton.setOpaque(true);
        stopButton.setBackground(new Color(255, 70, 70));
        stopButton.setForeground(new Color(255, 70, 70));
        startButton.setOpaque(true);
        startButton.setBackground(new Color(114, 255, 93));
        startButton.setForeground(new Color(114, 255, 93));
        scoreRaceButton.setOpaque(true);
        scoreRaceButton.setBackground(new Color(118, 221, 255));
        splitButton.setOpaque(true);
        splitButton.setBackground(new Color(118, 221, 255));

        tablePanel.add(createSplitTable(), Constraints.splitTable);

        startAndStopButtons.add(startButton);
        startAndStopButtons.add(stopButton);
        splitAndScoreButtons.add(scoreRaceButton);
        splitAndScoreButtons.add(splitButton);
        buttonPanel.add(startAndStopButtons, Constraints.startButton);
        buttonPanel.add(splitAndScoreButtons, Constraints.splitButton);

        controlPanel.add(buttonPanel, Constraints.splitButton);
        controlPanel.add(timeClock, Constraints.timeClock);

        mainPanel.add(tablePanel, Constraints.mainPanelTable);
        mainPanel.add(controlPanel, Constraints.mainPanelButtons);
        return mainPanel;
    }

    public JBScrollPane createSplitTable(){
        JBScrollPane tablePane = new JBScrollPane(splitTable);
        tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        splitTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        splitTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        splitTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        splitTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        TableColumn bibNumberColumn = splitTable.getColumnModel().getColumn(0);

        bibNumberColumn.setCellEditor(new ComboBoxCellEditor(bibNumbers));

        return tablePane;
    }

    private JComponent buildRunnerViewTab(){
        JPanel runnerViewPanel = new JPanel(new GridBagLayout());
        JBScrollPane runnerTable = createTablePanelFromDataModel(runnerTableModel);

        runnerViewPanel.add(addRunnerButton, Constraints.addButton);
        runnerViewPanel.add(delRunnerButton, Constraints.delButton);
        runnerViewPanel.add(runnerTable, Constraints.otherTable);

        return runnerViewPanel;
    }

    private JComponent buildTeamViewTab(){
        JPanel teamViewPanel = new JPanel(new GridBagLayout());
        JBScrollPane teamTable = createTablePanelFromDataModel(teamTableModel);

        teamViewPanel.add(addTeamButton, Constraints.addButton);
        teamViewPanel.add(delTeamButton, Constraints.delButton);
        teamViewPanel.add(teamTable, Constraints.otherTable);

        return teamViewPanel;
    }

    private JComponent buildRaceViewTab(){
        JPanel raceViewPanel = new JPanel(new GridBagLayout());
        JBScrollPane raceTable = createTablePanelFromDataModel(raceTableModel);

        raceViewPanel.add(addRaceButton, Constraints.addButton);
        raceViewPanel.add(delRaceButton, Constraints.delButton);
        raceViewPanel.add(raceTable, Constraints.otherTable);

        return raceViewPanel;
    }

    public JBScrollPane createTablePanelFromDataModel(TableModel tableModel) {
        JBTable table = new JBTable(tableModel);
        JBScrollPane tablePane = new JBScrollPane(table);
        tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return tablePane;
    }

    public void displayResults(){
        JFrame resultsFrame = new JFrame("Results");
        JPanel resultsPanel = new JPanel(new GridBagLayout());
        JTextPane results = new JTextPane();

        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[][] resultsData = packageTableData(splitTableModel);
        String finalRanking = xcscore.scoreRace(resultsData);

        results.setText(finalRanking);

        resultsPanel.add(results);
        resultsFrame.add(resultsPanel);

        resultsFrame.pack();
        resultsFrame.setVisible(true);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("XCScore");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new XCScoreGUI(), BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                createAndShowGUI();
            }
        });
    }

}
