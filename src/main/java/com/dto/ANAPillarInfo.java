/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.dto;

import java.util.HashMap;
import java.util.List;
import main.java.instruments.InstrumentConstants.IMAGE_CHANNEL;
import main.java.lis.constant.DiagnosisConstant.ANA_Titer;

/**
 *
 * @author Kang Bei
 */
public class ANAPillarInfo {
    
    private final String pillarID;
    private final String row;
    private final String col;
    private final String sampleBarcode;
    
    private final ANA_Titer ANATiter;

    private final HashMap<IMAGE_CHANNEL,SampleImage> imageList;
    
    public ANAPillarInfo(String pillarID,String row, String col, String sampleBarcode,ANA_Titer ANATiter,HashMap<IMAGE_CHANNEL,SampleImage> imageList){
        this.pillarID = pillarID;
        this.sampleBarcode = sampleBarcode;
        this.ANATiter = ANATiter;
        this.imageList = imageList;
        this.row = row;
        this.col = col;
        
    }

    /**
     * @return the pillarID
     */
    public String getPillarID() {
        return pillarID;
    }

    /**
     * @return the sampleBarcode
     */
    public String getSampleBarcode() {
        return sampleBarcode;
    }

    /**
     * @return the ANATiter
     */
    public ANA_Titer getANATiter() {
        return ANATiter;
    }

    /**
     * @return the imageList
     */
    public HashMap<IMAGE_CHANNEL,SampleImage> getImageList() {
        return imageList;
    }

    /**
     * @return the row
     */
    public String getRow() {
        return row;
    }

    /**
     * @return the col
     */
    public String getCol() {
        return col;
    }
    
    
    
}
