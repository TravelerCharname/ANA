/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import exceptions.ROIException;
import java.util.ArrayList;
import java.util.HashMap;
import main.java.com.imagemodel.ANA_ROI_Result;
import main.java.com.imagemodel.ROIClass;

/**
 *
 * @author mlei
 */
public class Image {

    /*
    used as the label in svm
    coding: p1=1.0, p2=2.0, etc.
    if not be initialized the default is -1
     */
    public ArrayList<Cell> cells = new ArrayList<>();
    private ArrayList<ArrayList<Double>> LBP_Features;
    private ArrayList<ArrayList<Double>> GLCM_Features;
    private ArrayList<ArrayList<Double>> Combined_Features;

    private  Image() {
    }
    
    public void initFromANA_ROI_Result(ANA_ROI_Result result) throws ROIException {
        if(null==result.getNucleusList()||result.getNucleusList().isEmpty())
            throw new ROIException();
        for (ROIClass roi : result.getNucleusList()) {
            Cell c = new Cell();
            c.initWithROIClass(roi);
            this.cells.add(c);
        }
    }

    public Image(ANA_ROI_Result result) throws ROIException {
        this();
        if(null==result.getNucleusList()||result.getNucleusList().isEmpty())
            throw new ROIException();
        for (ROIClass roi : result.getNucleusList()) {
            Cell c = new Cell();
            c.initWithROIClass(roi);
            this.cells.add(c);
        }
    }
    
    

    public int getCellCount() {
        return this.cells.size();
    }

    public ArrayList<ArrayList<Double>> getLBP_Features() {
        if (this.LBP_Features == null) {
            this.setLBP_Features();
        }
        return LBP_Features;
    }

    public void setLBP_Features() {
        this.LBP_Features = new ArrayList<>();
        for (Cell c : this.cells) {
            this.LBP_Features.add(c.getLBP_Features());
        }
    }

    public ArrayList<ArrayList<Double>> getGLCM_Features() {
        if (this.GLCM_Features == null) {
            this.setGLCM_Features();
        }
        return GLCM_Features;
    }

    public void setGLCM_Features() {
        this.GLCM_Features = new ArrayList<>();
        for (Cell c : this.cells) {
            this.GLCM_Features.add(c.getGLCM_Features());
        }
//        System.out.println("size of glcm feature list: " + this.GLCM_Features.size());
//        this.GLCM_Features = GLCM_Features;
    }

    public ArrayList<ArrayList<Double>> getCombined_Features() {
        if (this.Combined_Features == null) {
            this.setCombined_Features();
        }
        return Combined_Features;
    }

    public void setCombined_Features() {
        this.Combined_Features = new ArrayList<>();
        for (Cell c : this.cells) {
            this.Combined_Features.add(c.getCombined_Features());
        }
//        System.out.println("size of Combined Features list: " + this.Combined_Features.size());
//        this.Combined_Features = Combined_Features;
    }
}
