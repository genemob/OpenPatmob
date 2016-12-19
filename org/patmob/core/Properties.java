package org.patmob.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

/**
 * Access to global properties file
 * @author Piotr
 */
public class Properties {
    static java.util.Properties props;
    static File patmobData, propFile;
    static BufferedWriter bw;
    static BufferedReader br;
    static boolean initialized = false;

    private static void initialize() {
        // "patmob_data" directory in default system directory
        File defDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        patmobData = new File (defDir, "patmob_data");
        if (!patmobData.exists()) patmobData.mkdir();
        propFile = new File(patmobData + System.getProperty("file.separator") +
                "patmob.properties");
        loadFile();
        initialized = true;
    }

    private static void loadFile(){
        try {
            br = new BufferedReader(new FileReader(propFile));
            props.load(br);
            br.close();
        } catch (FileNotFoundException x) {
            //first run - create prop file
            System.out.println("Creating file: " + propFile);
            saveFile();
        } catch (IOException x) {
            System.out.println("PatmobProperties.loadFile: " + x);
        }
    }
    
    public static void saveFile(){
        if (!initialized) initialize();
        try {
            bw = new BufferedWriter(new FileWriter(propFile));
            props.store(bw, "org.patmob.core.PatmobProperties");
            bw.close();
        } catch (IOException x) {
            System.out.println("PatmobProperties.saveFile: " + x);
        }
    }
    
    /**
     * Sets and saves the named property.
     * @param key
     * @param value 
     */
    public static void set(String key, String value) {
        if (!initialized) initialize();
        props.setProperty(key, value);
    }
    
    /**
     * Sets and saves the named properties.
     * @param data 
     */
    public static void set(String[][] data) {
        if (!initialized) initialize();
        for (String[] data1 : data) {
            props.setProperty(data1[0], data1[1]);
        }
    }
    
    /**
     * Gets the named property.
     * @param key
     * @return 
     */
    public static String get(String key) {
        if (!initialized) initialize();
        return props.getProperty(key);
    }
}