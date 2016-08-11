/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 *
 * @author mlei
 */
public class PRConstants {

    public static final String USER = System.getProperty("user.name");
    public static final String PROJECT = System.getProperty("user.dir");
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    public static final String HOME = "C:"+FILE_SEPARATOR+"JAVA";
    public static final String DEFAULT_PROPERTIES;
    public static final String CUSTOM_PROPERTIES;
    private static Properties default_settings;
    public static final HashSet<String> DEFAULT_PROPERTIE_NAMES;
    
    static{
        DEFAULT_PROPERTIES =HOME+FILE_SEPARATOR+"ANA data"+FILE_SEPARATOR+"resource" + FILE_SEPARATOR + "default_prop.properties";//PROJECT + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "resource" + FILE_SEPARATOR + "default_prop.properties"
        CUSTOM_PROPERTIES = PROJECT + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "resource" + FILE_SEPARATOR + "custom_prop.properties";
        DEFAULT_PROPERTIE_NAMES=new HashSet<>();
        DEFAULT_PROPERTIE_NAMES.add("DECISION_TH");
        DEFAULT_PROPERTIE_NAMES.add("TRAINING_IMAGE_FOLDER");
        DEFAULT_PROPERTIE_NAMES.add("TRAINING_DATA_RAW");
        DEFAULT_PROPERTIE_NAMES.add("TRAINING_DATA_SCALED");
        DEFAULT_PROPERTIE_NAMES.add("SCALE_PARAMETER");
        DEFAULT_PROPERTIE_NAMES.add("TRAINING_IMAGE_LOG");
        DEFAULT_PROPERTIE_NAMES.add("TRAINED_MODEL");
        DEFAULT_PROPERTIE_NAMES.add("PREDICTING_OUTPUT_FOLDER");
        DEFAULT_PROPERTIE_NAMES.add("TRACKING_OUTPUT_FOLDER");
    }

    public static Properties defaultProperties() {
        if (default_settings == null) {
            try {
                initDefaultSettings();
            } catch (InvalidPropertiesException ex) {
                Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return default_settings;
    }

    public static void initDefaultSettings() throws InvalidPropertiesException {
        default_settings=new Properties();
        
        //load
        default_settings=loadSettings(DEFAULT_PROPERTIES);
        //save
//        default_settings.setProperty("DECISION_TH", 0.7 + "");
//        default_settings.setProperty("PREDICTING_OUTPUT_FOLDER", HOME + FILE_SEPARATOR + "ana result");
//        default_settings.setProperty("TRAINING_IMAGE_FOLDER", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "train");
//        default_settings.setProperty("TRAINING_DATA_RAW", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "train_data.txt");
//        default_settings.setProperty("TRAINING_DATA_SCALED", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "scaled_train_data.txt");
//        default_settings.setProperty("SCALE_PARAMETER", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "scale_parameter.txt");
//        default_settings.setProperty("TRAINING_IMAGE_LOG", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "ij log");
//        default_settings.setProperty("TRAINED_MODEL", HOME + FILE_SEPARATOR + "ANA Data" + FILE_SEPARATOR + "trained_model.txt");
//        default_settings.setProperty("TRACKING_OUTPUT_FOLDER", HOME + FILE_SEPARATOR + "ANA Results Tracking");
//
//        BufferedWriter bw = null;
//
//        try {
//            bw = new BufferedWriter(new FileWriter(DEFAULT_PROPERTIES));
//            default_settings.store(bw, "default settings");
//        } catch (IOException ex) {
//            Logger.getLogger(PlateConstants.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (bw != null) {
//                try {
//                    bw.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(PlateConstants.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }


        if(PlateConstants.DEBUG_MODE)default_settings.list(System.out);
    }

    private void saveCustomTrainSettings(Properties prop) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(CUSTOM_PROPERTIES));
            prop.store(bw, "custom train settings");
        } catch (IOException ex) {
            Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        prop.list(System.out);
    }
    

    public static Properties loadSettings(String properties) throws InvalidPropertiesException  {// throws FileNotFoundException, IOException
        BufferedReader br = null;
        File source = new File(properties);
        if (!source.exists()) {
            return defaultProperties();
        }
        Properties p = new Properties();

        try {
            br = new BufferedReader(new FileReader(source));
            p.load(br);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Logger.getLogger(PRConstants.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        check_properties(p);
        return p;
    }

    public static void check_properties(Properties p) throws InvalidPropertiesException {
        for(String key:DEFAULT_PROPERTIE_NAMES){
            if(p.getProperty(key)==null){
                throw new InvalidPropertiesException("input properties has missing field : "+key);
            }
        }
    }

    public static String getUserInput(String key) {
        JPanel jp = new JPanel();
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.showDialog(jp, "select " + key);

        File selected = jfc.getSelectedFile();

        return selected.getAbsolutePath();

    }

    public static String getUSER() {
        return USER;
    }

    public static String getPROJECT() {
        return PROJECT;
    }

    public static String getFILE_SEPARATOR() {
        return FILE_SEPARATOR;
    }

    public static String getHOME() {
        return HOME;
    }
    
    public static double getDECISION_TH(){
        String val=default_settings.getProperty("DECISION_TH");
        return Double.valueOf(val);
    }

    public static String getDEFAULT_PROPERTIES() {
        return DEFAULT_PROPERTIES;
    }

    public static String getCUSTOM_PROPERTIES() {
        return CUSTOM_PROPERTIES;
    }

    public static HashSet<String> getDefault_properties() {
        return DEFAULT_PROPERTIE_NAMES;
    }

    public static class InvalidPropertiesException extends Exception {

        public InvalidPropertiesException(String msg) {
            super(msg);
        }
    }
}
