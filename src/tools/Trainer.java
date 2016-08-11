/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;


import model.pattern.Image;
import constants.PRConstants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.ANAChipImage.ANA_CellRecognition;
import main.java.com.imageProcessException.ChipInfoException;
import main.java.com.imageProcessException.ROIException;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.lis.constant.DiagnosisConstant.ANA_Pattern;
import test.howToUse_runANA_ROI;

/**
 *
 * @author mlei
 */
public class Trainer {

    private String training_folder;
    private String output_image_folder;
    private String raw_data;
    private String scaled_data;
    private String scale_parameter;
    private String model;

    private BufferedWriter out;
    private boolean only488;

    /*
    *1.model file
    *2.scale par
    *9.write file
    3.set file paths
        3.1.input image
        3.2.image log
        3.3.output:raw data(scaled data), model, scale par, 
    *4.train()
    *5.train(string path)
    *6.scale
    7.cross validation
    8.easy.py
     */
    private Trainer() {
        cleanEdge = false;
        enableWatershed = false;
        this.only488 = constants.PlateConstants.ONLY_488;

    }

    public Trainer(String training_folder, String output_image_folder, String raw_data, String scaled_data, String scale_parameter, String model) {
        cleanEdge = false;
        enableWatershed = false;
        this.only488 = constants.PlateConstants.ONLY_488;
        this.training_folder = training_folder;
        this.output_image_folder = output_image_folder;
        this.raw_data = raw_data;
        this.scaled_data = scaled_data;
        this.scale_parameter = scale_parameter;
        this.model = model;
    }

    private Trainer(Properties prop) {
        this();
        cleanEdge = constants.PlateConstants.CLEAN_EDGE;
        enableWatershed = constants.PlateConstants.ENABLE_WATERSHED;
        this.only488 = constants.PlateConstants.ONLY_488;
        this.training_folder = prop.getProperty("TRAINING_IMAGE_FOLDER");
        this.output_image_folder = prop.getProperty("TRAINING_IMAGE_LOG");
        this.raw_data = prop.getProperty("TRAINING_DATA_RAW");
        this.scaled_data = prop.getProperty("TRAINING_DATA_SCALED");
        this.scale_parameter = prop.getProperty("SCALE_PARAMETER");
        this.model = prop.getProperty("TRAINED_MODEL");
    }

    public Trainer trainerWithProperties(String properties) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(properties));
            PRConstants.check_properties(prop);
            return new Trainer(prop);
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PRConstants.InvalidPropertiesException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("fail to load properties");
        }
        return null;
    }

    public static Trainer defaultTrainer() {
        Properties p = PRConstants.defaultProperties();
        return new Trainer(p);
    }

    public boolean train() {
        boolean b;
        b = extractFeaturesFromImages(this.training_folder, this.raw_data);
        if (!b) {
            System.out.println("fail to extract features");
            return false;
        }
        b = scale(this.raw_data, this.scaled_data, this.scale_parameter);
        if (!b) {
            System.out.println("scale failed");
            return false;
        }
        b = train(this.scaled_data, this.model);
        if (!b) {
            System.out.println("train failed");
            return false;
        }
        System.out.println("train completed...");
        return b;
    }

    public boolean train(String scaled, String model) {
        if (!new File(scaled).exists()) {
            System.out.println("pass valid scaled training data");
            return false;
        }
        String[] train_cmd = {"-c", "8", "-g", "0.5", scaled, model};
        try {
            svm_train.main(train_cmd);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void AnaRoiResult2Line(ANA_ROI_Result roi, ANA_Pattern pattern, BufferedWriter bw) {
        String line;
        if (roi == null) {
            return;
        }
        if (bw == null) {
            return;
        }
        Image img=null;
        try {
            img = new Image(roi);
        } catch (exceptions.ROIException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(null==img)return;
//        ArrayList<ArrayList<Double>> features = img.getLBP_Features();
        ArrayList<ArrayList<Double>> features = img.getCombined_Features();
        try {
            for (ArrayList<Double> al : features) {
                line = pattern.getId() + "";
                for (int index = 1; index <= al.size(); index++) {
                    line += " " + index + ":" + al.get(index - 1);
                }
                bw.write(line);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean extractFeaturesFromImages(String trainingFolderPath, String rawDataPath) {
        // root//[p1,p2,...,p5]//[488,reflect]//
        File root = new File(trainingFolderPath);
        if (!root.exists()) {
            return false;
        }
        File f488;
        File fref;
        File mirror;
        ArrayList<File> fileList = new ArrayList<>();

        File outputImageFolder = new File(this.output_image_folder);
        if (!outputImageFolder.exists()) {
            outputImageFolder.mkdirs();
        }

        ANA_ROI_Result roi;
        ANA_Pattern pattern;

        BufferedWriter bw;
        try {
            bw = getWriter(rawDataPath);

            for (File folder : root.listFiles()) {
                pattern = getPatternByFolder(folder);
                if (pattern == null) {
                    continue;
                }
                f488 = new File(folder, "488");
                fref = new File(folder, "reflected");
                if (f488.exists() && fref.exists()) {
                    for (File pic : f488.listFiles()) {
                        mirror = new File(fref, pic.getName().replace("_0_1", "_0_0"));
                        fileList.clear();
                        fileList.add(mirror);
                        fileList.add(pic);

                        try {

                            roi = ANA_CellRecognition.runANA_ROI(fileList, outputImageFolder, cleanEdge, enableWatershed,only488);
                            if (roi != null) {
                                AnaRoiResult2Line(roi, pattern, bw);
                            }
                        } catch (ChipInfoException | IOException ex) {
                            Logger.getLogger(howToUse_runANA_ROI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {
                    return false;
                }
            }
        } catch (IOException ex) {
            System.out.println("fail to write data into assigned file");
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    private boolean cleanEdge;
    private boolean enableWatershed;

    public boolean scale(String raw, String scaled, String parameter) {
        if (!new File(raw).exists()) {
            System.out.println("pass a valid data file");
            return false;
        }
        String[] scale_cmd = {"-l", "0", "-u", "1","-s", parameter, raw};//"-l", "-1", "-u", "1",
        try {
            svm_scale.main(scale_cmd, scaled);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private ANA_Pattern getPatternByFolder(File folder) {
        if (folder == null) {
            return null;
        }
        ANA_Pattern pattern = null;
        try {
            int i = Integer.parseInt(folder.getName().substring(1));
            pattern = ANA_Pattern.get(i);
        } catch (NumberFormatException ex) {
            System.out.println(ex.getMessage());
        }
        return pattern;
    }

    private BufferedWriter getWriter(String resultPath) throws IOException {
        if (out != null) {
            out.close();
        }
        out = new BufferedWriter(new FileWriter(resultPath));

        return out;
    }

    public void grid() throws IOException {
//        String model=PRConstants.chooseTRAINED_MODEL();
//        String svmTr=PRConstants.getUserInput("svm train");
//        String scaled_data=PRConstants.getUserInput("scaled probelm");
        BufferedWriter writer = getWriter("C:\\Users\\mlei\\Documents\\NetBeansProjects\\svm\\libsvm-3.21\\tools\\scaled_train_data.txt");
        BufferedReader reader = new BufferedReader(new FileReader(this.scaled_data));
        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
            writer.flush();
        }
        writer.close();
        reader.close();
        String[] cmd = {"cmd.exe", "/c", "python grid.py -v 7 -log2c 5,-3,-1 -log2g 5,-15,-1 -svmtrain C:\\Users\\mlei\\Documents\\NetBeansProjects\\svm\\libsvm-3.21\\windows\\svm-train.exe C:\\Users\\mlei\\Documents\\NetBeansProjects\\svm\\libsvm-3.21\\tools\\scaled_train_data.txt"};

        ProcessBuilder builder = new ProcessBuilder(cmd);

        builder.directory(new File("C:\\Users\\mlei\\Documents\\NetBeansProjects\\svm\\libsvm-3.21\\tools"));

        Process p = builder.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((line = br.readLine()) != null) {
            if (line.startsWith("[")) {
                continue;
            } else {
                String[] str = line.split(" ");
//                System.out.println("len = "+str.length);
                System.out.println("c=" + str[0] + " g=" + str[1] + " accuracy=" + str[2]);
            }
        }
    }

    public String getTraining_folder() {
        return training_folder;
    }

    public String getOutput_image_folder() {
        return output_image_folder;
    }

    public String getRaw_data() {
        return raw_data;
    }

    public String getScaled_data() {
        return scaled_data;
    }

    public String getScale_parameter() {
        return scale_parameter;
    }

    public String getModel() {
        return model;
    }

}
