/*
 * Start using Java EE. Needs javax.json-1.0.4.jar
 */
package org.patmob.core;

import java.awt.Dimension;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Piotr
 */
public class Table {
    
//TODO *0* Generic table and Plugin Manager
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        JTable myTable = defaultTable();
        final JPanel panel = new JPanel();
        panel.add(myTable);
        panel.setPreferredSize(new Dimension(100, 100));
//        panel.setVisible(true);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("table");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                //Create and set up the content pane.
//                Converter converter = new Converter();
//                converter.mainPane.setOpaque(true); //content panes must be opaque
                frame.setContentPane(panel);

                //Display the window.
                frame.pack();
                frame.setVisible(true);            }
        });   
    }
        
        public static JTable defaultTable() {
            JsonObject jOb = null;
            try {
                jOb = Json.createObjectBuilder()
                        .add("Families", Json.createArrayBuilder()
                                .add(Json.createObjectBuilder()
                                        .add("PN", "EP123")
                                        .add("FN", "12345")
                                        .add("Description", "Hello")))
                        .build();
            } catch (Exception x) {
                System.out.println("MAIN: " + x);
            }
            JTable myTable = create(jOb, new String[]{"PN", "FN", "Description"}, 
                    "Families");
            return myTable;
        }
        
//            .add("Families", Json.createArrayBuilder()
//                .add(Json.createObjectBuilder()
//                    .add("PN", "EP123")
//                    .add("FN", "12345")
//                    .add("Description", "Hello")
//            .build();
//    }
    
    /**
     * Creates <code>JTable</code> to view the data.
     * @param jOb - Fields of this <code>JsonObject</code> contain general 
     * information. The field <b>arrayFieldName</b> contains a <code>
     * JsonArray</code> of <code>JsonObject</code>: one per column row.
     * @param colNames - Names of the table columns - have to correspond to 
     * fields of objects comprising the array in <b>arrayFieldName</b>.
     * @param arrayFieldName - the column data.
     * @return - The table, or null if not successful.
     */
    public static JTable create(JsonObject jOb, 
            String[] colNames, String arrayFieldName){
        JTable table = null;
        try {
            TableModel tableModel = getCustomModel(
                    jOb.getJsonArray(arrayFieldName), colNames);
            table = new JTable(tableModel);
        } catch (Exception x) {
            System.out.println("org.patmob.core.Table.create:" + x);
        }
        return table;
    }
    
    public static TableModel getCustomModel(JsonArray rowObjects, 
            String[] colNames) {
        
        Object[][] data = new Object[rowObjects.size()][];
        for (int i=0; i<rowObjects.size(); i++) {
            JsonObject o = rowObjects.getJsonObject(i);
            data[i] = new Object[colNames.length + 1];
            data[i][0] = false;
            for (int j=1; j<data[i].length; j++) {
                data[i][j] = o.getString(colNames[j-1]);
            }
        }
        
        String[] cols = new String[colNames.length + 1];
        cols[0] = "Select";
        for (int i=1; i<cols.length; i++) {
            cols[i] = colNames[i-1];
        }
        
        return new DefaultTableModel(data, cols){
            //override getColumnClass to render boolean as checkbox
            @Override
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }
        };
    }
}
