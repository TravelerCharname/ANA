/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import main.java.com.instrumentInterface.TSPANAPlateInterface;
import main.java.lis.constant.DiagnosisConstant;

/**
 *
 * @author mlei
 */
public class PlateConstants {

    public static final double PositiveCutOffRatio = 0.3;//allCellPixelMean:1.8154473673924976;1st quantile:5
    public static final double NegativeCutOffRatio = 1.5;
    public static final int BIN_SIZE = 16;
    public static final int CHANNEL488 = 1;
    public static final int MAX_SIGNAL = 65536;
    public static final double R2_TH = 0.85;
    public static final DiagnosisConstant.ANA_Titer PLATE_1_TITER = DiagnosisConstant.ANA_Titer.ANA_1_40;
    public static final double CTRL_RATIO_TH = 3.5;
    public static final boolean ONLY_488 = false;
    public static final boolean ENABLE_WATERSHED = false;
    public static final boolean CLEAN_EDGE = false;

    public static final boolean DEBUG_MODE = TSPANAPlateInterface.DEBUG_MODE;

}
