package org.patmob.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Piotr
 */
public class PatmobProperties {
    static Properties props;
    static File defDir, propFile;
    static BufferedWriter bw;
    static BufferedReader br;

    public static void initialize(File directory) {
        props = new Properties();
        defDir = directory;
        propFile = new File(defDir + System.getProperty("file.separator") +
                "patmob.properties");
        loadFile();
    }

    public static void loadFile(){
        try {
            br = new BufferedReader(new FileReader(propFile));
            props.load(br);
            br.close();
        } catch (FileNotFoundException x) {
            //first run - create prop file
            System.out.println("Creating file: " + propFile);
            saveFile();
        } catch (IOException x) {
            System.out.println("PatmobProperties.saveFile: " + x);
        }
    }
    
    public static void saveFile(){
        try {
            bw = new BufferedWriter(new FileWriter(propFile));
            props.store(bw, "org.patmob.core.PatmobProperties");
            bw.close();
        } catch (IOException x) {System.out.println("PatmobProperties.saveFile: " + x);}
    }
    
    /**
     * Sets the named property.
     * @param key
     * @param value 
     */
    public static void set(String key, String value) {
        props.setProperty(key, value);
    }
    
    /**
     * Gets the named property.
     * @param key
     * @return 
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
}