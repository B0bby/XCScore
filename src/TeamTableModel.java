import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class TeamTableModel extends DefaultTableModel {
    private boolean DEBUG;

    public TeamTableModel(){
        this.DEBUG = false;
    }

    public TeamTableModel(boolean debug){
        super(new Object[]{"Team Number", "Team Name"}, 0);
        this.DEBUG = debug;
    }

    public void readArrayIntoTable(String[][] array){
        if (array.length == 0){}
        else {
            for (String[] rowData : array){
                this.addRow(rowData);
            }
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the rowData/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }
}