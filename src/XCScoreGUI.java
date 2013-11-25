import javax.swing.*;

import com.intellij.ui.TableCell;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteComboBoxEditor;
import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.TableView;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class XCScoreGUI extends JPanel{
    private XCScore xcscore = new XCScore();
    private boolean DEBUG;
    private Timer timer;
    private int hour, minute, second, raceNumber;
    private long millisecond;

    final SplitTableModel splitTableModel = new SplitTableModel(DEBUG);
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
    final JComboBox bibNumbers = new JComboBox(xcscore.getBibNumbers());

    public XCScoreGUI(){
        this(false);
    }

    public XCScoreGUI(boolean DEBUG){
        addAllTabsToWindow();
        attachActionListeners();
        updateAllTablesFromDatabase();
        this.DEBUG = DEBUG;
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
        timer = new Timer(1, timerAction);

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
                splitButton.setEnabled( true );
                stopButton.setEnabled( true );
                startButton.setEnabled( false );
            }
        };
        startButton.addActionListener(startButtonListener);

        ActionListener stopButtonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                splitButton.setEnabled( false );
                stopButton.setEnabled( false );
                startButton.setEnabled( true );
                hour = minute = second = 0;
                millisecond = 0;
                timer.stop();
            }
        };
        stopButton.addActionListener(stopButtonListener);

        splitTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {
                Object row = tableModelEvent.getSource();
            }
        });

//        CellEditorListener bibChangeListener = new CellEditorListener() {
//            @Override
//            public void editingStopped(ChangeEvent changeEvent) {
//                ComboBoxCellEditor source = (ComboBoxCellEditor)changeEvent.getSource();
//                String bibNumber = (String)source.getCellEditorValue();
//
//                JComboBox cell = (JComboBox)source.getComponent();
//                Object item = cell.getEditor().getItem();
//
//                String[] runnerData = xcscore.getSingleRunnerData(bibNumber);
//
//                splitTableModel.fireTableDataChanged();
//
//                int column = 0;
//                for (String data : runnerData){
//                    splitTableModel.setValueAt(data, 1, column);
//                    column++;
//                }
//            }
//
//            @Override
//            public void editingCanceled(ChangeEvent changeEvent) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        };
//        splitTable.getColumnModel().getColumn(0).getCellEditor().addCellEditorListener(bibChangeListener);

    }

    private void addSplit(){
        splitTableModel.addRow(new Object[]{"","","","", timePassed(true)});
    }

    private String timePassed(boolean returnMilliseconds) {
        String hours, minutes, seconds;
            hours = minutes = seconds = "";
        String time = "";

        millisecond++;

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

        JComponent raceViewWindow = buildRaceViewTab();
        mainTabGroup.addTab("Races",raceViewWindow);
        mainTabGroup.setMnemonicAt(3, KeyEvent.VK_1);

        add(mainTabGroup);
    }

    private JComponent buildMainViewTab(){
        JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel tablePanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());

        splitButton.setEnabled( false );
        stopButton.setEnabled( false );

        splitButton.setFont(new Font("Helvetica", Font.BOLD, 24));
        timeClock.setFont(new Font("Helvetica", Font.BOLD, 40));
        stopButton.setFont(new Font("Helvetica", Font.BOLD, 20));
        startButton.setFont(new Font("Helvetica", Font.BOLD, 20));

        stopButton.setOpaque(true);
        stopButton.setBackground(new Color(255, 70, 70));
        stopButton.setForeground(new Color(255, 70, 70));
        startButton.setOpaque(true);
        startButton.setBackground(new Color(114, 255, 93));
        startButton.setForeground(new Color(114, 255, 93));

        // timer.start();

        tablePanel.add(createSplitTable(), Constraints.splitTable);
        buttonPanel.add(timeClock, Constraints.timeClock);
        buttonPanel.add(startButton, Constraints.startButton);
        buttonPanel.add(stopButton, Constraints.stopButton);
        buttonPanel.add(splitButton, Constraints.splitButton);
        mainPanel.add(tablePanel, Constraints.mainPanelTable);
        mainPanel.add(buttonPanel, Constraints.mainPanelButtons);
        return mainPanel;
    }

    public JBScrollPane createSplitTable(){
        JBScrollPane tablePane = new JBScrollPane(splitTable);
        tablePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("XCScore");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new XCScoreGUI(true), BorderLayout.CENTER);

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
