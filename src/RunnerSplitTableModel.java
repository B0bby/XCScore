import javax.swing.table.DefaultTableModel;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class RunnerSplitTableModel extends DefaultTableModel {
    private boolean DEBUG;
    private String[] columnNames = {"Name", "Bib Number", "Finish Time"};
    private Object[][] rowData = {{"Bob", "1234", "1234567"}};

    public final Object[] longValues = {"Jane", "Kathy",
            "None of the above",
            new Integer(20), Boolean.TRUE};

    public RunnerSplitTableModel(){
        super();
        this.DEBUG = false;
    }

    public RunnerSplitTableModel(boolean debug){
        super();
        this.DEBUG = debug;
    }

    public int getColumnCount() {
        return 3;
    }

    public int getRowCount() {
        return 1;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return rowData[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the rowData/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * rowData can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");
        }

        rowData[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of rowData:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i=0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
                System.out.print("  " + rowData[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}