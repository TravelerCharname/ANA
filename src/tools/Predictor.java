/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;


import constants.PRConstants;
import constants.WarningMessage;
import exceptions.ROIException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.lis.constant.DiagnosisConstant;
import main.java.lis.constant.DiagnosisConstant.ANA_Pattern;
import model.pattern.Image;
import model.plate.ANATestResult;

/**
 *
 * @author mlei
 */
public class Predictor {

    private double decision_th;
//    private String tempData;
    private String scale_parameter;
    private String model;
    private String result_folder; // desk//ana result//plate//sample_id_result.txt
    /*
    *1.get&validate model file
    *2.get&validate scale par
    *3.set file paths
        3.1.input image
        3.2.image log
        3.3.output:raw data(scaled data), predict result
    x4.pred()
    5.pred(string path)
    *7.pred(ANA_ROI_Result)
    *6.pred(plate plate)
    *8.pattern decision
    9.upload(FinalSampleInfo)
     */
    private IOHelper io;
//    private BufferedWriter out;
//    private final String time;

    //make sure all par valid
    private Predictor() {
        io = IOHelper.getIO();
//        this.time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

    }

    public Predictor(String scale_parameter, String model, String result_folder, double decision_th) {
        io = IOHelper.getIO();
        this.scale_parameter = scale_parameter;
        this.model = model;
        this.result_folder = result_folder;
//        this.time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        this.decision_th = decision_th;
    }

    private Predictor(Properties prop) {
        this();
        this.result_folder = prop.getProperty("PREDICTING_OUTPUT_FOLDER")+ constants.PRConstants.FILE_SEPARATOR +"Pattern";
        this.scale_parameter = prop.getProperty("SCALE_PARAMETER");
        this.model = prop.getProperty("TRAINED_MODEL");
        this.decision_th = Double.valueOf(prop.getProperty("DECISION_TH"));
    }

    public static Predictor predictorWithProperties(String properties) {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader(properties));
            PRConstants.check_properties(prop);
            return new Predictor(prop);
        } catch (IOException ex) {
            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PRConstants.InvalidPropertiesException ex) {
            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("fail to load properties");
        }
        return null;
    }

    public static Predictor defaultPredictor() {
        Properties p = PRConstants.defaultProperties();
        return new Predictor(p);
    }

    public boolean predict(ANA_ROI_Result roi, String sampleID, String plateID) throws ROIException {
        boolean b = false;
//        this.tempData =null;
        if (roi == null) {
            System.out.println("fail to get roi from sample : " + sampleID + " @" + plateID);
            log("fail to get roi from sample : " + sampleID + " @" + plateID);
            return false;
        }
        File outputFolder = new File(this.result_folder, plateID);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        String rawData = new File(outputFolder, sampleID + "_raw.txt").getAbsolutePath();
        String scaledData = new File(outputFolder, sampleID + "_scaled.txt").getAbsolutePath();
        String result = new File(outputFolder, sampleID + "_result.txt").getAbsolutePath();
        try {
            //        this.tempData = result;
            b = extractFeaturesFromAnaRoiResult(roi, rawData);
        } catch (ROIException ex) {
            throw new ROIException(sampleID + "@" + plateID);
        }
        if (!b) {
            System.out.println("fail to extract features for sample " + sampleID + " @" + plateID);
            return false;
        }
        b = restore(rawData, scaledData, this.scale_parameter);
        if (!b) {
            System.out.println("scale failed for sample " + sampleID + " @" + plateID);
            return false;
        }
        b = predict(scaledData, this.model, result);
        if (!b) {
            System.out.println("predict failed for sample " + sampleID + " @" + plateID);
            return false;
        }
        System.out.println("predict completed for sample " + sampleID);
        return b;
    }

    public boolean predict(ANATestResult testResult, ANAPillarPlateInfo plateInfo) throws IOException, ROIException {
        ANA_ROI_Result roi;
        File resultFile;
        String msg;
        HashMap<ANA_Pattern, Double> hm;
        roi = testResult.getRoi();
        if (null == roi) {
            log(plateInfo.getPillarPlateID(), testResult.getJulien_barcode(), testResult.getPillarPosition(), "Unable to precess the image.");
            markDownIffyPillar(testResult.getJulien_barcode(), null, plateInfo.getPillarPlateFilePath(), testResult.getPillarPosition(), "Unable to precess the image.");
            throw new ROIException(testResult.getJulien_barcode() + "@" + plateInfo.getPillarPlateID() + testResult.getPillarPosition());
        }

        try {
            if (predict(roi, testResult.getJulien_barcode(), plateInfo.getPillarPlateID())) {
                resultFile = new File(this.result_folder, plateInfo.getPillarPlateID());
                resultFile = new File(resultFile, testResult.getJulien_barcode() + "_result.txt");

                msg = "" + testResult.getJulien_barcode();
                double max = 0, d;
                ANA_Pattern pattern = ANA_Pattern.NA;
                try {
                    hm = patternDistribution(resultFile.getAbsolutePath());
                    testResult.setPattDistMap(hm);
                    d = hm.get(ANA_Pattern.CENTROMERE);
                    msg += " CENTROMERE " + d;
                    if (d > max) {
                        max = d;
                        pattern = ANA_Pattern.CENTROMERE;
                    }
                    d = hm.get(ANA_Pattern.HOMOGENEOUS);
                    msg += " HOMOGENEOUS " + d;
                    if (d > max) {
                        max = d;
                        pattern = ANA_Pattern.HOMOGENEOUS;
                    }
                    d = hm.get(ANA_Pattern.NUCLEOLAR);
                    msg += " NUCLEOLAR " + d;
                    if (d > max) {
                        max = d;
                        pattern = ANA_Pattern.NUCLEOLAR;
                    }
                    d = hm.get(ANA_Pattern.PERIPHERAL);
                    msg += " PERIPHERAL " + d;
                    if (d > max) {
                        max = d;
                        pattern = ANA_Pattern.PERIPHERAL;
                    }
                    d = hm.get(ANA_Pattern.SPECKLED);
                    msg += " SPECKLED " + d;
                    if (d > max) {
                        max = d;
                        pattern = ANA_Pattern.SPECKLED;
                    }
                    if (max > this.decision_th) {
                        testResult.setPattern(pattern);
                        if (pattern.equals(ANA_Pattern.HOMOGENEOUS) && (testResult.getTiter().equals(DiagnosisConstant.ANA_Titer.ANA_1_40) || testResult.getTiter().equals(DiagnosisConstant.ANA_Titer.ANA_1_80))) {
                            testResult.getWarningMessage().add(WarningMessage.WeakPositive.getId());
                            testResult.setPositivity(DiagnosisConstant.ANA_Result.NEGATIVE);
                        }
                    } else {
                        testResult.getWarningMessage().add(WarningMessage.UndecidedPatern.getId());
                        markDownIffyPillar(testResult.getJulien_barcode(), null, plateInfo.getPillarPlateFilePath(), testResult.getPillarPosition(), "Unable to recognise the pattern.");
                    }
                    System.out.println(msg);
                    System.out.println("plate " + plateInfo.getPillarPlateID() + " sample " + testResult.getJulien_barcode() + " pixel ratio = " + testResult.getPixelRatio());
                    return true;
                } catch (FileNotFoundException ex) {
                    testResult.getWarningMessage().add(WarningMessage.ANAROIResultNotFound.getId());
                    testResult.getWarningMessage().add(WarningMessage.NoSupportingFile2.getId());
                    log("plate : " + plateInfo.getPillarPlateID() + "\tsample : " + testResult.getJulien_barcode() + "\t" + ex.getMessage());
                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnknownPredictResultException ex) {
                    testResult.getWarningMessage().add(WarningMessage.UnknownPredictResult.getId());
                    log("plate : " + plateInfo.getPillarPlateID() + "\tsample : " + testResult.getJulien_barcode() + "\t" + ex.getMessage());
                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (EmptyResultFileException ex) {
                    testResult.getWarningMessage().add(WarningMessage.ANAROIResultNotFound.getId());
                    testResult.getWarningMessage().add(WarningMessage.NoSupportingFile2.getId());
                    log("plate : " + plateInfo.getPillarPlateID() + "\tsample : " + testResult.getJulien_barcode() + "\t" + ex.getMessage());
                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                System.out.println("predict failed for sample : " + testResult.getJulien_barcode() + " @" + testResult.getJulien_barcode());
                log("predict failed for sample : " + testResult.getJulien_barcode() + " @" + testResult.getPlateID());
//                        samples.remove(sampleInfo);
            }
        } catch (ROIException ex) {
            log(plateInfo.getPillarPlateID(), testResult.getJulien_barcode(), testResult.getPillarPosition(), "No cell was found");
            markDownIffyPillar(testResult.getJulien_barcode(), null, plateInfo.getPillarPlateFilePath(), testResult.getPillarPosition(), "No cell was found.");
            throw new ROIException(testResult.getJulien_barcode() + "@" + plateInfo.getPillarPlateID() + testResult.getPillarPosition());
        }
        return false;
    }

    public boolean predict(String scaled, String model, String result) {
        if (!new File(scaled).exists()) {
            System.out.println("pass valid scale parameters");
            return false;
        }
        if (!new File(model).exists()) {
            System.out.println("pass valid model file");
            return false;
        }
        String[] predict_cmd = {scaled, model, result};//"-b", "1",
        try {
            svm_predict.main(predict_cmd);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            log(ex.getMessage() + " data = " + scaled);
        }
        return false;
    }

    public boolean extractFeaturesFromAnaRoiResult(ANA_ROI_Result roi, String rawData) throws ROIException {
        if (roi == null) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = getWriter(rawData);
            AnaRoiResult2Line(roi, DiagnosisConstant.ANA_Pattern.NA, bw);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return false;
    }

    public boolean restore(String raw_data, String scaled_data, String scale_parameter) {
        if (!new File(raw_data).exists()) {
            System.out.println("pass a valid data file");
            return false;
        }
        if (!new File(scale_parameter).exists()) {
            System.out.println("pass a valid parameter file");
            return false;
        }
        String[] restore_cmd = {"-r", scale_parameter, raw_data};

        try {
            svm_scale.main(restore_cmd, scaled_data);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Trainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void AnaRoiResult2Line(ANA_ROI_Result roi, ANA_Pattern pattern, BufferedWriter bw) throws ROIException {
        String line;
        if (roi == null) {
            return;
        }
        if (bw == null) {
            return;
        }
        Image img = new Image(roi);
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

    private BufferedWriter getWriter(String resultPath) throws IOException {
        if (io == null) {
            io = IOHelper.getIO();
        }
        return io.getWriter(resultPath);
    }
//    private BufferedWriter getWriter(String resultPath) throws IOException {
//        if (out != null) {
//            out.close();
//        }
//        out = new BufferedWriter(new FileWriter(resultPath));
//
//        return out;
//    }

    private BufferedWriter getWriter(File logPath, boolean append) throws IOException {
        if (io == null) {
            io = IOHelper.getIO();
        }
        return io.getWriter(logPath, append);
    }
//    private BufferedWriter getWriter(File logPath, boolean append) throws IOException {
//        if (out != null) {
//            out.close();
//        }
//        out = new BufferedWriter(new FileWriter(logPath, append));
//
//        return out;
//    }

//    private void log(String err_msg) {
//        File log = new File(this.result_folder, "error logs");
//        if (!log.exists()) {
//            log.mkdirs();
//        }
//        log = new File(log, time);
//        BufferedWriter bw = null;
//        try {
//            bw = getWriter(log, true);
//            bw.write(err_msg);
//            bw.newLine();
//            bw.flush();
//        } catch (IOException ex) {
//            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (bw != null) {
//                try {
//                    bw.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
    public void log(String err_msg) {
        if (null == io) {
            io = IOHelper.getIO();
        }
        io.log(err_msg);
    }

    public void log(String pillarPlateID, String sampleBarcode, String pillarId, String comment) {
        if (null == io) {
            io = IOHelper.getIO();
        }
        io.log(pillarPlateID, sampleBarcode, pillarId, comment);
    }

    public void markDownIffyPillar(String barcode, String sampleId, String plateFolder, String pillarId, String comment) {
        if (null == io) {
            io = IOHelper.getIO();
        }
        io.markDown(barcode, sampleId, plateFolder, pillarId, comment);
    }

//    1.return hashmap: loop map, init FinalSampleInfo fields, report pattern
//    2.input FinalSampleInfo,return void
    public static HashMap<ANA_Pattern, Double> patternDistribution(String predictResultPath) throws FileNotFoundException, UnknownPredictResultException, EmptyResultFileException, IOException {
        BufferedReader bufr = new BufferedReader(new FileReader(predictResultPath));
        int sum = 0;
        HashMap<ANA_Pattern, Double> pattern_counts = new HashMap<>();
        for (ANA_Pattern p : ANA_Pattern.values()) {
            pattern_counts.put(p, 0.0);
        }
        String line;
        ANA_Pattern p;
        while ((line = bufr.readLine()) != null) {
            p = ANA_Pattern.get(Integer.valueOf(line.split("\\.")[0]));

            if (p == null) {
                throw new UnknownPredictResultException("unknown result: " + line + " @file: " + predictResultPath);
            }
            pattern_counts.put(p, pattern_counts.get(p) + 1);
            sum++;
        }
        if (sum == 0) {
            throw new EmptyResultFileException("empty result file: " + predictResultPath);
        }
        double ratio;

        for (ANA_Pattern pa : pattern_counts.keySet()) {
            ratio = pattern_counts.get(pa) / sum;
            pattern_counts.put(pa, ratio);
//            max=max>ratio?max:ratio;
        }
        return pattern_counts;
    }

    public double getDecision_th() {
        return decision_th;
    }

//    public String getTempData() {
//        return tempData;
//    }
    public String getScale_parameter() {
        return scale_parameter;
    }

    public String getModel() {
        return model;
    }

    public String getResult_folder() {
        return result_folder;
    }

    public String getTime() {
        if (io == null) {
            io = IOHelper.getIO();
        }
        return io.getTime();
    }

    public void submit() {
        /*
        1.get pattern ratio
        2.if any exception, handle + log
        3.loop map, init fields in FinalSampleInfo
        4.find and report pattern
         */
    }

    public static class UnknownPredictResultException extends Exception {

        public UnknownPredictResultException(String msg) {
            super(msg);
        }
    }

    public static class EmptyResultFileException extends Exception {

        public EmptyResultFileException(String msg) {
            super(msg);
        }
    }
}
