/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.plate;

import constants.PlateConstants;
import static constants.PlateConstants.DEBUG_MODE;
import exceptions.MissingCtrlSampleException;
import constants.WarningMessage;
import delegate.Drone;
import exceptions.PlateLayoutException;
import exceptions.ROIException;
import exceptions.WrongANAPlateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.dto.ANASampleInfo;
import main.java.com.imageProcessException.ChipInfoException;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.instruments.InstrumentConstants;
import main.java.lis.constant.DiagnosisConstant;
import tools.IOHelper;
import tools.Predictor;
import tools.SQLHelper;

/**
 *
 * @author mlei
 */
public class ANAPlate extends Thread{

    private double plate2CriticalValue;

    private void updateFailedPlate() {
        if (InstrumentConstants.ANA_PLATE_TYPE.TYPE_1.equals(type)) {
            SQLHelper.updateFailedPlate1(this);
        }else{
            SQLHelper.updateFailedPlate2(this);
        }
    }
    private ANAPillarPlateInfo plate;
    private InstrumentConstants.ANA_PLATE_TYPE type;
//    private ANATestResult negCtrl;
    private ANATestResult posCtrl;
    private double negCtrlSignal;
//    private double posCtrlSignal;
    private double ctrlRatio;
    private double plate1CriticalValue;
    private ArrayList<ANATestResult> testResultList;

    private final HashSet<Integer> plateErr;
    private String plateID;
    private Predictor predictor;
    
    // factory methods
    private ANAPlate() {
        this.plateErr = new HashSet<>();
        this.testResultList = new ArrayList<>();
    }
    
    public ANAPlate(ANAPillarPlateInfo plate) throws WrongANAPlateException {
        this();
        if(Drone.validateAnaPlate(plate)){
            this.plate = plate;
            this.type = plate.getPlateType();
            if(plate.getPlateType().equals(InstrumentConstants.ANA_PLATE_TYPE.TYPE_2)){
                this.predictor=Predictor.defaultPredictor();
            }
            this.plateID=plate.getPillarPlateID();
        }else{
            throw new WrongANAPlateException();
        }
//        negCtrl=new ANATestResult(plate, "NegCtrl");
//        posCtrl=new ANATestResult(plate, "PosCtrl");
    }
    
    public boolean initCtrlSamples(boolean enableWatershed, boolean only488){
        ANASampleInfo sampleInfo;
        //neg
        sampleInfo=plate.getNegCtrl();
        if(sampleInfo==null){
            this.plateErr.add(WarningMessage.ControlQCFailed.getId());
            return false;
        }
//        try {
//            this.negCtrl.initNegCtrl(sampleInfo, enableWatershed);
//            if(negCtrl.getFirstPlateSignal()<=0)this.plateErr.add(WarningMessage.NegCtrlFailed.getId());
//        } catch (PlateLayoutException ex) {
//            this.plateErr.add(WarningMessage.WrongFirstPlateLayout.getId());
//            Logger.getLogger(ANAPlate.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ROIException ex) {
//            this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
//            Logger.getLogger(ANAPlate.class.getName()).log(Level.SEVERE, null, ex);
//        } 

        ANA_ROI_Result roi = null;
        try {
            roi = Drone.monopillarSampleGetROI(sampleInfo, enableWatershed,only488);
        } catch (ChipInfoException | IOException ex) {
            Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
            this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
        } catch (PlateLayoutException ex) {
            this.plateErr.add(WarningMessage.WrongFirstPlateLayout.getId());
            Logger.getLogger(ANAPlate.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (roi == null) {
            this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
            return false;
        }
        try {
            negCtrlSignal=roi.getSignal125();
        } catch (main.java.com.imageProcessException.ROIException ex) {
            this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
            this.plateErr.add(WarningMessage.ControlQCFailed.getId());
            return false;
        }
        if(negCtrlSignal<=0){
            this.plateErr.add(WarningMessage.NegCtrlFailed.getId());
            System.out.println("NegCtrl signal = "+negCtrlSignal);
            return false;
        }  
//        if(this.negCtrl==null)updateFailedPlate1();
        //pos
        roi=null;
        sampleInfo=plate.getPosCtrl();
        if(sampleInfo==null){
            this.plateErr.add(WarningMessage.ControlQCFailed.getId());
            return false;
        }
        posCtrl = new ANATestResult(plate, sampleInfo.getSampleBarcode());
        if (InstrumentConstants.ANA_PLATE_TYPE.TYPE_2.equals(type)) {
            posCtrl= new ANATestResult(plate, "PosCtrl");
            boolean b=posCtrl.initROI2(sampleInfo, enableWatershed,false, only488);
            if(b){
                b=posCtrl.initPosCtrl2(negCtrlSignal);
                this.plate2CriticalValue=posCtrl.getSecondPlateSignal()>negCtrlSignal*PlateConstants.NegativeCutOffRatio?negCtrlSignal*PlateConstants.NegativeCutOffRatio:posCtrl.getSecondPlateSignal();
            }
            this.plateErr.addAll(posCtrl.getWarningMessage());
            return b;
        }else{
            try {
                roi = Drone.monopillarSampleGetROI(sampleInfo, enableWatershed,only488);
            } catch (ChipInfoException | IOException ex) {
                Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
                this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
            } catch (PlateLayoutException ex) {
                this.plateErr.add(WarningMessage.WrongFirstPlateLayout.getId());
                Logger.getLogger(ANAPlate.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (roi == null) {
                this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
                return false;
            }
            double posCtrlSignal;
            try {
                posCtrlSignal = roi.getSignal125(); // titer=40
            } catch (main.java.com.imageProcessException.ROIException ex) {
                this.plateErr.add(WarningMessage.ANAROIResultNotFound.getId());
                this.plateErr.add(WarningMessage.ControlQCFailed.getId());
                return false;
            }
            ctrlRatio = posCtrlSignal / negCtrlSignal;
            System.out.println("PosCtrl:NegCtrl = " + ctrlRatio);
            if (ctrlRatio <= 1) {
                this.plateErr.add(WarningMessage.PosCtrlFailed.getId());
                return false;
            }
            if(ctrlRatio<PlateConstants.CTRL_RATIO_TH){
                System.out.println(WarningMessage.PosCtrlFailed.getMsg()+" : "+PlateConstants.CTRL_RATIO_TH);
            }
            posCtrl.setFirstPlateSignal(posCtrlSignal);
            
            this.plate1CriticalValue=posCtrlSignal*PlateConstants.PositiveCutOffRatio>negCtrlSignal*PlateConstants.NegativeCutOffRatio?negCtrlSignal*PlateConstants.NegativeCutOffRatio:posCtrlSignal*PlateConstants.PositiveCutOffRatio;
        }
        return true;
    }
    
    public void initSampleList(boolean enableWatershed, boolean only488) throws IOException{
        this.testResultList=new ArrayList<>();
        ANATestResult testResult = null;
        for(ANASampleInfo sampleInfo:plate.getSampleInfoList()){
            try {
                testResult = sampleInfo2TestResult(sampleInfo, enableWatershed,only488);
                testResultList.add(testResult);
            } catch (ROIException ex) {
//                log to failed samples.txt
System.out.println(plateID+" "+testResult.getJulien_barcode()+" failed");
            }
        }
    }
    
    // functions
    public void update2Database(){
        SQLHelper.updatePlate(this);
        SQLHelper.updateSamples(this);
    }
    
    public void write2xls(String outputFolder){
//        IOHelper.plate1ResultSheet(plate, "C:\\");
    }


    @Override
    public void run() {
        System.out.println(plate.getPillarPlateID()+": this is a "+plate.getPlateType()+" plate.....");
//        ANAPlate plate=new ANAPlate(plateInfo);
        boolean b,enableWatershed=constants.PlateConstants.ENABLE_WATERSHED,only488=constants.PlateConstants.ONLY_488;
        b = this.initCtrlSamples(enableWatershed,only488);
        if(b){
            try {
                this.initSampleList(enableWatershed,only488);
                if(!DEBUG_MODE)System.out.println("init ok...");
//            predict
//            write&update
this.update2Database();
                if(!DEBUG_MODE)System.out.println("update ok...");
IOHelper.generateAnaPositivityResult(this);
                if(!DEBUG_MODE)System.out.println("write file ok...");
//check null for signal when update 
            } catch (IOException ex) {
                Logger.getLogger(ANAPlate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            if(!DEBUG_MODE)System.out.println("init failed...");
            this.updateFailedPlate();
            if(!DEBUG_MODE)System.out.println("update ok...");
        }
    }

    public static void runOnePlate(ANAPillarPlateInfo plateInfo, String outputFolderPath, boolean enableWatershed, boolean only488) throws WrongANAPlateException, MissingCtrlSampleException, IOException{
        System.out.println(plateInfo.getPillarPlateID()+": this is a "+plateInfo.getPlateType()+" plate.....");
        ANAPlate plate=new ANAPlate(plateInfo);
        boolean b = plate.initCtrlSamples(enableWatershed,only488);
        if(b){
            plate.initSampleList(enableWatershed,only488);
//            predict
//            write&update
plate.update2Database();
IOHelper.generateAnaPositivityResult(plate,outputFolderPath);
        //check null for signal when update 
        }else{
            plate.updateFailedPlate();
        }
        
    }
    public static void runOnePlate(ANAPillarPlateInfo plateInfo, boolean enableWatershed, boolean only488) throws WrongANAPlateException, MissingCtrlSampleException, IOException{

        ANAPlate plate=new ANAPlate(plateInfo);
        boolean b = plate.initCtrlSamples(enableWatershed,only488);
        if(b){
            plate.initSampleList(enableWatershed,only488);
//            predict
//            write&update
plate.update2Database();
IOHelper.generateAnaPositivityResult(plate);
        //check null for signal when update 
        }else{
            plate.updateFailedPlate();
        }
    }
    
    // calculation
    
    // assembly
    public ANATestResult sampleInfo2TestResult(ANASampleInfo sampleInfo) throws ROIException, IOException{
        return sampleInfo2TestResult(sampleInfo,true,true);
    }
    public ANATestResult sampleInfo2TestResult(ANASampleInfo sampleInfo, boolean enableWatershed, boolean only488) throws ROIException, IOException{
        if(InstrumentConstants.ANA_PLATE_TYPE.TYPE_1.equals(this.type))return parseType1Sample(sampleInfo,enableWatershed,only488);
        else return parseType2Sample(sampleInfo,enableWatershed,only488);
    }
    public ANATestResult parseType1Sample(ANASampleInfo sampleInfo, boolean enableWatershed, boolean only488) throws ROIException{
        ANATestResult testResult = new ANATestResult(plate,sampleInfo.getSampleBarcode());
        if(testResult.initROI1(sampleInfo, enableWatershed,only488)){
            testResult.diagnose1(this.plate1CriticalValue);
            
            if(DEBUG_MODE&&this.ctrlRatio>=4.8){
                if(testResult.getFirstPlateSignal()>.275*this.posCtrl.getFirstPlateSignal()+.5*this.negCtrlSignal){
                    testResult.positivityCombined=DiagnosisConstant.ANA_Result.POSITIVE;
                }else{
                    testResult.positivityCombined=DiagnosisConstant.ANA_Result.NEGATIVE;
                }
            }
        }
        return testResult;
    }
    public ANATestResult parseType2Sample(ANASampleInfo sampleInfo, boolean enableWatershed,boolean only488) throws ROIException, IOException{
        ANATestResult testResult = new ANATestResult(plate,sampleInfo.getSampleBarcode());
        if(testResult.initROI2(sampleInfo, enableWatershed,true,only488)){
            testResult.diagnose2(this.plate2CriticalValue);
            predictor.predict(testResult, plate);
        }
        return testResult;
    }
    
    // inquiry

    public ANAPillarPlateInfo getPlate() {
        return this.plate;
    }

    public String getPlateId() {
        return plateID;
    }

    public int getSampleNumber() {
        return plate.getSampleInfoList().size();
    }

    public InstrumentConstants.ANA_PLATE_TYPE getType() {
        return type;
    }

    public ANATestResult getPosCtrl() {
        return posCtrl;
    }

    public double getNegCtrlSignal() {
        return negCtrlSignal;
    }

    public double getCtrlRatio() {
        return ctrlRatio;
    }

    public ArrayList<ANATestResult> getTestResultList() {
        return testResultList;
    }

    public HashSet<Integer> getPlateErr() {
        return plateErr;
    }
}
