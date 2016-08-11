/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package delegate;

import constants.PlateConstants;
import exceptions.PlateLayoutException;
import exceptions.WrongANAPlateException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import main.java.com.ANAChipImage.ANA_CellRecognition;
import main.java.com.dto.ANAPillarInfo;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.dto.ANASampleInfo;
import main.java.com.imageProcessException.ChipInfoException;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.com.imagemodel.ImagePixel;
import main.java.com.imagemodel.ROIClass;
import main.java.instruments.InstrumentConstants;
import main.java.lis.constant.DiagnosisConstant;

/**
 *
 * @author mlei
 */
public class Drone {
    public static ANAPillarInfo monopillarSampleGetPillar(ANASampleInfo sampleInfo) throws PlateLayoutException{
        if(sampleInfo==null)return null;
        HashMap<DiagnosisConstant.ANA_Titer, ANAPillarInfo> pillarInfoList = sampleInfo.getPillarInfoList();
        if(1!=pillarInfoList.size())throw new PlateLayoutException();
        return pillarInfoList.entrySet().iterator().next().getValue();
    }
    
    public static ANA_ROI_Result pillar2ROI(ANAPillarInfo pillar, boolean enableWatershed, boolean only488) throws ChipInfoException, IOException{
        pillar.getImageList();
        ArrayList<File> pair = new ArrayList<>();//准备放reflected和488两张图
        File fref = pillar.getImageList().get(InstrumentConstants.IMAGE_CHANNEL.CHANNELreflect).getImageFileList(); //reflected       
        File f488 = pillar.getImageList().get(InstrumentConstants.IMAGE_CHANNEL.CHANNEL488).getImageFileList();//488
        pair.add(fref);//放入reflected
        pair.add(f488);//放入488
        return ANA_CellRecognition.runANA_ROI(pair, null, cleanEdge,enableWatershed,only488);
    }
    private static boolean cleanEdge = constants.PlateConstants.CLEAN_EDGE;
    
    public static ANA_ROI_Result monopillarSampleGetROI(ANASampleInfo sampleInfo, boolean enableWatershed, boolean only488) throws PlateLayoutException, ChipInfoException, IOException{
        ANAPillarInfo monopillarSampleGetPillar = monopillarSampleGetPillar(sampleInfo);
        if(monopillarSampleGetPillar!=null)
            return pillar2ROI(monopillarSampleGetPillar,enableWatershed,only488);
        else 
            return null;
    }
    
    
    public static boolean validateAnaPlate(ANAPillarPlateInfo plate){
        if(plate==null)return false;
        boolean b=InstrumentConstants.ANA_PLATE_TYPE.TYPE_1.equals(plate.getPlateType());
        b=b||InstrumentConstants.ANA_PLATE_TYPE.TYPE_2.equals(plate.getPlateType());
        return b;
    }
    public static void validateAnaPlate_throws(ANAPillarPlateInfo plate) throws WrongANAPlateException{
        if(!validateAnaPlate(plate))throw new WrongANAPlateException();
    }
    
    
    // wander

    /*
    0. sum,step=0, all=.size()
    1. len
    2. mid=(min+max)/2 = _mid, origin-mid
    pos=mid,step 0:len-1
    3. pos=pos+(-1)^(step+1)*step
        if(sum+0.0/all>0.9) return step;
    return step; 
     */
    public static int waddle(double[] freqs) {
        if (freqs == null) {
            return PlateConstants.BIN_SIZE + 2;
        }
        int len = freqs.length;
        if (len != PlateConstants.BIN_SIZE) {
            System.out.println("erroneous bin size " + len);
        }
        int pos = len / 2;
        double cum_freq = 0;
        int direction = -1;
        int step;
        for (step = 0; step < len; step++) {
            pos = pos + direction * step;
            cum_freq += freqs[pos - 1];
            if (cum_freq > 0.9) {
                return step;
            }
            direction *= -1;
        }
        return step;
    }

    public static double[] freq(ANA_ROI_Result ROI) throws exceptions.ROIException {
        if (ROI == null) {
            return null;
        } else {
            double[] freqs = new double[PlateConstants.BIN_SIZE];
            int total = 0;
            int pos;
            ArrayList<ROIClass> nucleusList = ROI.getNucleusList();
            if (nucleusList == null) {
                throw new exceptions.ROIException();
            }
            for (ROIClass roi : ROI.getNucleusList()) {
                total += roi.getRoiPixels().size();
                for (ImagePixel pxl : roi.getEdgePixels().values()) {
                    pos = pxl.getGreyChannels().get(PlateConstants.CHANNEL488) * PlateConstants.BIN_SIZE / PlateConstants.MAX_SIGNAL;
                    if (pos >= PlateConstants.BIN_SIZE) {
                        System.out.println();
                        System.out.println("getting unusual reading in histogram " + pos);
                        System.out.println("signal = " + pxl.getGreyChannels().get(PlateConstants.CHANNEL488));
                        System.out.println();
                        pos = PlateConstants.BIN_SIZE;
                    }
                    freqs[pos]++;
                }
            }
            return freqs;
        }
    }
}
