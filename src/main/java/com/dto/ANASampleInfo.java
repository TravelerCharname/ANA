/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.dto;

import java.util.HashMap;
import java.util.List;
import main.java.lis.constant.DiagnosisConstant.ANA_Titer;

/**
 *
 * @author Kang Bei
 */
public class ANASampleInfo {
    
    private final String sampleBarcode;
    private final HashMap<ANA_Titer,ANAPillarInfo> pillarInfoList;
    
    public ANASampleInfo(String sampleBarcode,HashMap<ANA_Titer,ANAPillarInfo> pillarInfoList){
        this.sampleBarcode = sampleBarcode;
        this.pillarInfoList = pillarInfoList;
    }

    /**
     * @return the sampleBarcode
     */
    public String getSampleBarcode() {
        return sampleBarcode;
    }

    /**
     * @return the pillarInfoList
     */
    public HashMap<ANA_Titer,ANAPillarInfo> getPillarInfoList() {
        return pillarInfoList;
    }
    
    
    
}
