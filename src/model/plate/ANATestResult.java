/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.plate;

import constants.PlateConstants;
import static constants.PlateConstants.DEBUG_MODE;
import constants.WarningMessage;
import delegate.Drone;
import exceptions.InvalidNegCtrlException;
import exceptions.PlateLayoutException;
import exceptions.ROIException;
import exceptions.WrongANAPlateException;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarInfo;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.dto.ANASampleInfo;
import main.java.com.imageProcessException.ChipInfoException;
//import main.java.com.imageProcessException.ROIException;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.instruments.InstrumentConstants;
import main.java.lis.constant.DiagnosisConstant;
import main.java.lis.constant.DiagnosisConstant.ANA_Result;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author mlei
 */

/*
R2 is a useful par. but need not to report
    private double plate2PositiveControl40;
    private double plate2PositiveControl80;
    private double plate2PositiveControl160;
    private double plate2PositiveControl320;
    private double plate2PositiveControl640;
    private double plate2PositiveControl1280;

plate -> type -> report plate1 -> report belonging samples
              -> report plate2 -> choose ANA_ROI_Result -> (get pattern related data) -> report belonging samples
 */
public class ANATestResult {

    //link to parent
    ANAPillarPlateInfo plate;
    private String plateID;    //the ANAPillarPlateInfo is kept in the chooser instance, the link is plateID
    private InstrumentConstants.ANA_PLATE_TYPE type;
    //info
    private String julien_barcode;
    private String pillarPosition;  //first position of all pillar that belong to this sample
//    private String sampleID;
    private HashSet<Integer> warningMessage;

    //result
    private ANA_ROI_Result roi;
    private HashMap<DiagnosisConstant.ANA_Titer, Double> signals;
    ///p1 result
    private ANA_Result positivity;
    public ANA_Result positivity30,positivityCombined;
    ///p2 result
    private double r2, pixelRatio;
    private DiagnosisConstant.ANA_Titer titer;  //report
    private DiagnosisConstant.ANA_Titer t4p;    //titer for pattern
    private DiagnosisConstant.ANA_Pattern pattern;
    private HashMap<DiagnosisConstant.ANA_Pattern, Double> pattDistMap;

    //constants
    private ANATestResult(String julien_barcode) {
        this.julien_barcode = julien_barcode;
        this.signals=new HashMap<>();
        this.warningMessage = new HashSet<>();
    }

    public ANATestResult(ANAPillarPlateInfo plate, String julien_barcode) {
        this(julien_barcode);
        this.plate = plate;
        this.plateID = plate.getPillarPlateID();
        this.type = plate.getPlateType();
    }

//    public ANATestResult(ANAPillarPlateInfo plate, String julien_barcode, String pillarPosition){
//        this(plate,julien_barcode);
//        this.pillarPosition = pillarPosition;
//    }
    public boolean initROI1(ANASampleInfo sampleInfo, boolean enableWatershed, boolean only488) {
        ANAPillarInfo pillar = null;
        try {
            pillar = Drone.monopillarSampleGetPillar(sampleInfo);

        } catch (PlateLayoutException ex) { 
            Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pillar == null) {
            this.warningMessage.add(WarningMessage.WrongFirstPlateLayout.getId());
            System.out.println(sampleInfo.getSampleBarcode() + " failed to init ROI");
            return false;
        }
        this.pillarPosition = pillar.getPillarID();
        roi = null;
        try {
            roi = Drone.pillar2ROI(pillar, enableWatershed,only488);
        } catch (ChipInfoException | IOException ex) {
            Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (roi == null) {
            this.warningMessage.add(WarningMessage.ANAROIResultNotFound.getId());
            System.out.println(sampleInfo.getSampleBarcode() + " failed to init ROI");
            return false;
        }
        return true;
    }

    public void diagnose1(double control) throws ROIException {
        double signal;
        try {
            signal = roi.getSignal125();
        } catch (main.java.com.imageProcessException.ROIException ex) {
            this.warningMessage.add(WarningMessage.ANAROIResultNotFound.getId());
            throw new exceptions.ROIException(plateID+this.pillarPosition);
        }
        this.signals.put(PlateConstants.PLATE_1_TITER, signal);
        if (signal < 0) {
            System.out.println(this.julien_barcode + " signal=" + signal);
            this.positivity = ANA_Result.NO_RESULT;
        } else if (signal < control) {// * PlateConstants.PositiveCutOffRatio
            this.positivity = ANA_Result.NEGATIVE;
        } else {
            this.positivity = ANA_Result.POSITIVE;
        }
        
        if (DEBUG_MODE&&signal < control * .3) {
            this.positivity30 = ANA_Result.NEGATIVE;
        } else {
            this.positivity30 = ANA_Result.POSITIVE;
        }
        
        
        roi = null;
    }
    public void diagnose2(double control){
        //titer,positivity,r2,pattern
        final Comparator<DiagnosisConstant.ANA_Titer> titerComparator = new Comparator<DiagnosisConstant.ANA_Titer>() {
            @Override
            public int compare(DiagnosisConstant.ANA_Titer t, DiagnosisConstant.ANA_Titer t1) {
                if (t.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                return t.getId() < t1.getId() ? -1 : t.getId() == t1.getId() ? 0 : 1;
            }
        };
        TreeMap<DiagnosisConstant.ANA_Titer, Double> decreasingSignals = new TreeMap<>(titerComparator);
        decreasingSignals.putAll(signals);
        SimpleRegression regression = new SimpleRegression();
        Iterator<DiagnosisConstant.ANA_Titer> it = decreasingSignals.keySet().iterator();
        DiagnosisConstant.ANA_Titer t;
        Double signal;
        while (it.hasNext()) {
            t = it.next();
            signal = decreasingSignals.get(t);
            if(signal==null)continue;
//            posCtrl=signal>posCtrl?signal:posCtrl; 以后假如1:40跪了, 按最亮的判
            regression.addData((double) t.getId(), signal);
            if (signal > control) {// * PlateConstants.PositiveCutOffRatio
                titer = t;
            }
        }
        
        r2 = regression.getRSquare();
        if (r2 < PlateConstants.R2_TH) {
            warningMessage.add(WarningMessage.SampleLinearity.getId());
        }
        if(titer == null)titer = DiagnosisConstant.ANA_Titer.ANA_LESS_1_40;
        if (DiagnosisConstant.ANA_Titer.ANA_LESS_1_40.equals(titer) || titer.getId() < 2) {//1:40
            
            System.out.println();
            for (DiagnosisConstant.ANA_Titer t1 : decreasingSignals.keySet()) {
                System.out.println(this.julien_barcode + " Sample vs Control (th="+PlateConstants.PositiveCutOffRatio+")");
                System.out.println(t1 + ": signal=" + decreasingSignals.get(t1) + "\tv.s.\tcontrol=" + control + " (" + decreasingSignals.get(t1) / control + ")");
            }
            System.out.println();
            positivity = DiagnosisConstant.ANA_Result.NEGATIVE;
            warningMessage.add(WarningMessage.WeakPositive.getId());

        } else {
            positivity = DiagnosisConstant.ANA_Result.POSITIVE;
        }
    }

    public boolean initROI2(ANASampleInfo sampleInfo, boolean enableWatershed, boolean isSample, boolean only488){

        final HashMap<DiagnosisConstant.ANA_Titer, ANAPillarInfo> pillarInfoList = sampleInfo.getPillarInfoList();
        if (pillarInfoList == null || pillarInfoList.size() != 6) {
            this.warningMessage.add(WarningMessage.WrongSecondPlateLayout.getId());
            return false;
        }
        this.signals = new HashMap<>();
        int step = PlateConstants.BIN_SIZE + 1;
        double[] freqs;
        ANA_ROI_Result roiResult;
        double signal;
        for (DiagnosisConstant.ANA_Titer t : pillarInfoList.keySet()) {
            ANAPillarInfo pillar = pillarInfoList.get(t);

            roiResult = null;
            try {
                roiResult = Drone.pillar2ROI(pillar, enableWatershed,only488);
            } catch (ChipInfoException | IOException ex) {
                Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (roiResult == null) {
                System.out.println(plateID + pillar.getPillarID() + " fail to get ROI");
                continue;
            }
            try {
                signal = roiResult.getSignal125();
                if (signal <= 0) {
                    System.out.println(plateID + pillar.getPillarID() + " signal=" + signal);
                }
                signals.put(t, signal);
            } catch (main.java.com.imageProcessException.ROIException ex) {
                this.warningMessage.add(WarningMessage.ANAROIResultNotFound.getId());
            }
            
            if (isSample) {
                freqs = null;
                try {
                    freqs = Drone.freq(roiResult);
                    if (step > Drone.waddle(freqs)) {
                    this.t4p = t;
                    this.roi = roiResult;
                    this.pillarPosition = pillar.getPillarID(); // PillarPosition is PillarForPatternPosition, not ReportDilutionPosition
                    this.pixelRatio = roiResult.getCellPixelRatio();
                }
                } catch (ROIException ex) { 
                    Logger.getLogger(ANATestResult.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            if (isSample&&roi == null) {
                    this.warningMessage.add(WarningMessage.ANAROIResultNotFound.getId());
                    System.out.println(sampleInfo.getSampleBarcode() + " failed to init ROI for pattern recognition");
                    System.out.println();
                    return false;
                }

        }
        return true;
    }

    /*
    take 1:40 as the PosCtrl as 
    */
    public boolean initPosCtrl2(double negControl) {
        final Comparator<DiagnosisConstant.ANA_Titer> titerComparator = new Comparator<DiagnosisConstant.ANA_Titer>() {
            @Override
            public int compare(DiagnosisConstant.ANA_Titer t, DiagnosisConstant.ANA_Titer t1) {
                if (t.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                return t.getId() < t1.getId() ? -1 : t.getId() == t1.getId() ? 0 : 1;
            }
        };
        TreeMap<DiagnosisConstant.ANA_Titer, Double> decreasingSignals = new TreeMap<>(titerComparator);
        decreasingSignals.putAll(signals);
        SimpleRegression regression = new SimpleRegression();
        Iterator<DiagnosisConstant.ANA_Titer> it = decreasingSignals.keySet().iterator();
        DiagnosisConstant.ANA_Titer t;
        double signal, posCtrl = getFirstPlateSignal();
        
        while (it.hasNext()) {
            t = it.next();
            signal = decreasingSignals.get(t);
//            posCtrl=signal>posCtrl?signal:posCtrl; 以后假如1:40跪了, 按最亮的判
            regression.addData((double) t.getId(), signal);
            if (signal > posCtrl * PlateConstants.PositiveCutOffRatio||signal > negControl * PlateConstants.NegativeCutOffRatio) {
                titer = t;
                
            }
        }
        if (titer.getId() >= DiagnosisConstant.ANA_Titer.ANA_1_320.getId()) {
                    positivity = DiagnosisConstant.ANA_Result.POSITIVE;
                    System.out.println("found titer for " + plateID + " : " + titer);
                    System.out.println();
                    System.out.println();
                }
        r2 = regression.getRSquare();
        if (r2 < PlateConstants.R2_TH) {
            warningMessage.add(WarningMessage.PositiveControlLinearity.getId());
        }
        if (titer == null || titer.getId() < DiagnosisConstant.ANA_Titer.ANA_1_320.getId()) {//1:40
            titer = DiagnosisConstant.ANA_Titer.ANA_LESS_1_40;
            System.out.println();
            for (DiagnosisConstant.ANA_Titer t1 : decreasingSignals.keySet()) {
                System.out.println(plateID + " Control Sample Compare");
                System.out.println(t1 + ": posCtrl=" + decreasingSignals.get(t1) + "\tv.s.\tnegCtrl=" + negControl + " (" + decreasingSignals.get(t1) / negControl + ")");
            }
            System.out.println();
            positivity = DiagnosisConstant.ANA_Result.NEGATIVE;
System.out.println("barcode "+this.julien_barcode);
            warningMessage.add(WarningMessage.PositiveNegativeControlComparison.getId());

        } else {
            positivity = DiagnosisConstant.ANA_Result.POSITIVE;
        }
        if (posCtrl < negControl * PlateConstants.CTRL_RATIO_TH) {
            this.warningMessage.add(WarningMessage.PosCtrlFailed.getId());
            return false;
        }
        return true;
    }

    public String concatWarningMsgs() {
        if (warningMessage == null || warningMessage.isEmpty()) {
            return null;
        }
        String msg = "";
        for (Integer id : warningMessage) {
            if (!msg.equals("")) {
                msg += ", ";
            }
            msg += WarningMessage.getWarningMessageById(id).getMsg();
        }
        return msg;
    }

    public double getSignalByTiter(DiagnosisConstant.ANA_Titer titer) {
        return signals.get(titer)==null?-999:signals.get(titer);
    }

    public double getFirstPlateSignal() {
        return getSignalByTiter(constants.PlateConstants.PLATE_1_TITER);
    }

    public double getSecondPlateSignal() {
        if (titer == null) {
            System.out.println("Can't get signal for " + this.julien_barcode + ". Must report its titer first");
        }
        return getSignalByTiter(titer);
    }

    public double getSignal() {
        if (InstrumentConstants.ANA_PLATE_TYPE.TYPE_1.equals(type)) {
            return getFirstPlateSignal();
        } else {
            return getSecondPlateSignal();
        }
    }

    public HashSet<Integer> getWarningMessage() {
        return warningMessage;
    }

    public void setPillarPosition(String pillarPosition) {
        this.pillarPosition = pillarPosition;
    }

    public void setRoi(ANA_ROI_Result roi) {
        this.roi = roi;
    }

    public void setFirstPlateSignal(double signal) {
        this.signals.put(PlateConstants.PLATE_1_TITER, signal);
    }
    
//    public AnaRecord toRecord(){
//        
//    }

    public String getJulien_barcode() {
        return this.julien_barcode;
    }

    public String getPillarPosition() {
        return pillarPosition;
    }

    public ANAPillarPlateInfo getPlate() {
        return plate;
    }

    public String getPlateID() {
        return plateID;
    }

    public InstrumentConstants.ANA_PLATE_TYPE getType() {
        return type;
    }

    public ANA_ROI_Result getRoi() {
        return roi;
    }

    public HashMap<DiagnosisConstant.ANA_Titer, Double> getSignals() {
        return signals;
    }

    public ANA_Result getPositivity() {
        return positivity;
    }

    public double getR2() {
        return r2;
    }

    public double getPixelRatio() {
        return pixelRatio;
    }

    public DiagnosisConstant.ANA_Titer getTiter() {
        return titer;
    }

    public DiagnosisConstant.ANA_Titer getTiter4Pattern() {
        return t4p;
    }

    public DiagnosisConstant.ANA_Pattern getPattern() {
        return pattern;
    }

    public HashMap<DiagnosisConstant.ANA_Pattern, Double> getPattDistMap() {
        return pattDistMap;
    }

    public void setPattern(DiagnosisConstant.ANA_Pattern pattern) {
        this.pattern = pattern;
    }

    public void setPattDistMap(HashMap<DiagnosisConstant.ANA_Pattern, Double> pattDistMap) {
        this.pattDistMap = pattDistMap;
    }

    public void setPositivity(ANA_Result positivity) {
        this.positivity = positivity;
    }

    public int cellCount() {
        if(roi==null)return 0;
        return roi.getNucleusList()==null?0:roi.getNucleusList().size();
    }

}
