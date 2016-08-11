/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.dto;

import java.util.List;
import main.java.instruments.InstrumentConstants.ANA_PLATE_TYPE;


/**
 *
 * @author VSIT2
 */
public class ANAPillarPlateInfo {
    
    private final String pillarPlateID;
    private final String pillarPlateFilePath;
    private final String wellPlateID;
    private final String plateTestName;
    private final String plateStatus;
    private final ANA_PLATE_TYPE plateType;
    private final List<ANASampleInfo> sampleInfoList;
    private final ANASampleInfo posCtrl;
    private final ANASampleInfo negCtrl;
    
    public ANAPillarPlateInfo(String pillarPlateID,String pillarPlateFilePath,String wellPlateID, String plateTestName,
            String plateStatus, ANA_PLATE_TYPE plateType,List<ANASampleInfo> sampleInfoList,ANASampleInfo posCtrl, ANASampleInfo negCtrl){
        this.pillarPlateID = pillarPlateID;
        this.pillarPlateFilePath=pillarPlateFilePath;
        this.wellPlateID = wellPlateID;
        this.plateType = plateType;
        this.sampleInfoList = sampleInfoList;
        this.plateTestName = plateTestName;
        this.plateStatus = plateStatus;
        this.posCtrl = posCtrl;
        this.negCtrl = negCtrl;
    }

    /**
     * @return the plateID
     */
    public String getPillarPlateID() {
        return pillarPlateID;
    }

    /**
     *
     * @return the path of the plate folder
     */
    public String getPillarPlateFilePath() {
        return pillarPlateFilePath;
    }

    /**
     * @return the plateType
     */
    public ANA_PLATE_TYPE getPlateType() {
        return plateType;
    }

    /**
     * @return the sampleInfoList
     */
    public List<ANASampleInfo> getSampleInfoList() {
        return sampleInfoList;
    }

    /**
     * @return the wellPlateID
     */
    public String getWellPlateID() {
        return wellPlateID;
    }

    /**
     * @return the plateTestName
     */
    public String getPlateTestName() {
        return plateTestName;
    }

    /**
     * @return the plateStatus
     */
    public String getPlateStatus() {
        return plateStatus;
    }

    /**
     * @return the posCtrl
     */
    public ANASampleInfo getPosCtrl() {
        return posCtrl;
    }

    /**
     * @return the negCtrl
     */
    public ANASampleInfo getNegCtrl() {
        return negCtrl;
    }
    
    
    
}
