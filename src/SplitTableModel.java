import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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

public class SplitTableModel extends DefaultTableModel implements TableModelListener {
    private boolean DEBUG;

    public SplitTableModel(){
        this.DEBUG = false;
    }

    public SplitTableModel(boolean debug){
        super(new Object[]{"Bib", "Name", "Team", "Race", "Finish"}, 0);
        this.DEBUG = debug;
    }

    @Override
    public void tableChanged(TableModelEvent tableModelEvent) {
        System.out.println("Is this working how I think it does?");
    }

    public Class<?> getColumnClass(int col) {
        if (col == 1){ return JComboBox.class; }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the rowData/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col > 1 && col < 4) {
            return false;
        } else {
            return true;
        }
    }
}