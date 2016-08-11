/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import constants.PlateConstants;
import exceptions.MissingCtrlSampleException;
import exceptions.WrongANAPlateException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.instrumentInterface.TSPANAPlateInterface;
import main.java.exceptions.TSPTestException;
import model.plate.ANAPlate;
import tools.IOHelper;
import tools.OperationQueue;

/**
 *
 * @author LM&L
 */
public class Test1 {

    /**
     * @param args the command line arguments
     */
    /*
    trainer.getCombinedFeatures
    cell.radius
    cell.lbp/area
    
     */
    public static void main(String[] args) {
        boolean enableWatershed = constants.PlateConstants.ENABLE_WATERSHED;
        boolean only488 = constants.PlateConstants.ONLY_488;
        
        loopPredict(enableWatershed,only488);//"ANAC80020310000545",
//        try {
//            runOnePlate("ANAC80020310000545", "ANAC80020310000545_20160714100233", enableWatershed, only488);
//        } catch (WrongANAPlateException | MissingCtrlSampleException | IOException | SQLException | TSPTestException ex) {
//            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    public static double[] getOrderStat(ArrayList<Integer> pixelSignalArray, double[] orders) {
        int orderLen = orders.length;
        double max_order = orders[orderLen - 1];
        double sumQuantileMean = 0;
        Comparator comparator = Collections.reverseOrder();
        Collections.sort(pixelSignalArray, comparator);

        double[] results = new double[orderLen];

        int loops = (int) (max_order * pixelSignalArray.size());
        for (int i = 0, pos = 0; i <= loops; i++) {
            sumQuantileMean = sumQuantileMean + pixelSignalArray.get(i);

            while (pos < orderLen && i == (int) (orders[pos] * pixelSignalArray.size()) - 1) {
                results[pos] = sumQuantileMean / (i + 1);
                pos++;
            }
        }
        return results;
    }

    public static void arrayTest() {
        int[] arr = new int[4];
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += i;
            if (0 == sum % 2) {
                arr[i] = sum;
            } else {
                arr[i] = -sum;
            }
        }

        for (int i = 0; i < 4; i++) {
            System.out.println("i : " + arr[i]);
        }
    }

    

    

    public static void runOnePlate(String pillarPlateBarcode, String plateImageFolderName,boolean enableWatershed, boolean only488) throws WrongANAPlateException, MissingCtrlSampleException, IOException, SQLException, TSPTestException {
         
//        String plateImageFolderName = "ANAC80010230000178_20150904194500";
//        String pillarPlateBarcode = "ANAC80010230000178";
//            ANAPillarPlateInfo plateInfo = TSPANAPlateInterface.getANAPlateSampleInfo(plateImageFolderName, pillarPlateBarcode);
//            System.out.println("this is a " + plateInfo.getPlateType() + " plate");
//            ANAPlate.runOnePlate(plateInfo,enableWatershed,only488);
        OperationQueue queue = OperationQueue.getQueue();queue.run();
queue.enqueue(plateImageFolderName, pillarPlateBarcode);
        
//        
//loopPredict(); 
//        
//loopPredict();
    }

    public static void loopPredict(boolean enableWatershed, boolean only488) {
        String plateImageFolderName;
        String pillarPlateBarcode;
        ANAPillarPlateInfo plateInfo;
        
        OperationQueue queue = OperationQueue.getQueue();
        new Thread(queue).start();
    if(PlateConstants.DEBUG_MODE)System.out.println("Operation Queue started");
         
//        ArrayList<ANAPillarPlateInfo> p2=new ArrayList<>();
        for (File folder : new File("J:\\ANA").listFiles()) {
            
            plateImageFolderName = folder.getName();
            pillarPlateBarcode = folder.getName().split("_")[0];
            queue.enqueue(plateImageFolderName, pillarPlateBarcode); 
//            if(PlateConstants.DEBUG_MODE)System.out.println("enqueue "+plateImageFolderName);
        }

//        for(ANAPillarPlateInfo p:p2){
//            Predictor.defaultPredictor().predict(p);
//        }
    }

    public static void loopPredict(String from,boolean enableWatershed, boolean only488) {
        
        String plateImageFolderName;
        String pillarPlateBarcode;
        ANAPillarPlateInfo plateInfo;

//        ArrayList<ANAPillarPlateInfo> p2=new ArrayList<>();
        for (File folder : new File("J:\\ANA").listFiles()) {
             plateImageFolderName = folder.getName();
                pillarPlateBarcode = folder.getName().split("_")[0];
                
//            try {
//                plateImageFolderName = folder.getName();
//                pillarPlateBarcode = folder.getName().split("_")[0];
//                if (pillarPlateBarcode.compareTo(from) < 0) {
//                    continue;
//                }
//                plateInfo = TSPANAPlateInterface.getANAPlateSampleInfo(plateImageFolderName, pillarPlateBarcode);
//                ANAPlate.runOnePlate(plateInfo,enableWatershed,only488);
//
//            } catch (SQLException ex) {
//                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (WrongANAPlateException ex) {
//                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (MissingCtrlSampleException ex) {
//                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (TSPTestException ex) {
//                Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
//            }
if (pillarPlateBarcode.compareTo(from) < 0) {
                    continue;
                }
new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String plateImageFolderName;
                        String pillarPlateBarcode;
                        ANAPillarPlateInfo plateInfo;
                        plateImageFolderName = folder.getName();
                        pillarPlateBarcode = folder.getName().split("_")[0];
                        plateInfo = TSPANAPlateInterface.getANAPlateSampleInfo(plateImageFolderName, pillarPlateBarcode);
                        ANAPlate.runOnePlate(plateInfo,enableWatershed,only488);
                    } catch (SQLException ex) {
                        Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TSPTestException ex) {
                        Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (WrongANAPlateException ex) {
                        Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MissingCtrlSampleException ex) {
                        Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start(); 
        }

//        for(ANAPillarPlateInfo p:p2){
//            Predictor.defaultPredictor().predict(p);
//        }
    }

//    public static void testTrain() {
//        //        Constants.initDefaultSettings();
//        Trainer t = Trainer.defaultTrainer();
//        t.train();
//    }
//
//    public static void testScale() {
//        //        Constants.initDefaultSettings();
//        Trainer t = Trainer.defaultTrainer();
//        t.scale(t.getRaw_data(), t.getScaled_data(), t.getScale_parameter());
//    }
//
//    public static void inSamplePredict() {
//        Trainer d = Trainer.defaultTrainer();
//        String testingFolder = "C:\\Users\\mlei\\Desktop\\predict";
//        //String training_folder, String output_image_folder, String raw_data, String scaled_data, String scale_parameter, String model
//        Trainer t = new Trainer(testingFolder, d.getOutput_image_folder(), d.getRaw_data() + ".testing", d.getScaled_data() + ".testing", d.getScale_parameter() + ".testing", d.getModel() + ".testing");
//        t.extractFeaturesFromImages(t.getTraining_folder(), t.getRaw_data());
//
//        Predictor dp = Predictor.defaultPredictor();
//
//        Predictor p = new Predictor(dp.getScale_parameter(), dp.getModel(), dp.getResult_folder(), 0.7);
//        p.restore(t.getRaw_data(), "C:\\Users\\mlei\\Desktop\\predict_scaled.txt", dp.getScale_parameter());
//        p.predict("C:\\Users\\mlei\\Desktop\\predict_scaled.txt", d.getModel(), "C:\\Users\\mlei\\Desktop\\predict_result.txt");
//    }

}
