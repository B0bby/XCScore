import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: b0b
 * Date: 11/21/13
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */

public class SplitTableModel extends DefaultTableModel {
    private boolean DEBUG;
    private XCScore xcscore;

    public SplitTableModel(){
        this.DEBUG = false;
    }

    public SplitTableModel(boolean debug, XCScore xcscore){
        super(new Object[]{"Bib", "Name", "Team", "Finish"}, 0);
        this.DEBUG = debug;
        this.xcscore = xcscore;
    }

    public Class<?> getColumnClass(int col) {
        if (col == 1){ return JComboBox.class; }
        return String.class;
    }

    public void setValueAt(Object value, int row, int col){
        super.setValueAt(value, row, col);

        if (col == 0){
            String[] runnerData = xcscore.getSingleRunnerData((String)value);
            for (String data : runnerData){
                col++;
                super.setValueAt(data, row, col);
            }
        }
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