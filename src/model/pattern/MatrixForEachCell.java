/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import java.util.HashMap;
import java.util.Iterator;
import main.java.com.imagemodel.ImagePixel;
import main.java.com.imagemodel.ROIClass;

/**
 *
 * @author Vibrant Sciences
 */
public class MatrixForEachCell {
    private ROIClass roiClass;

    public MatrixForEachCell(ROIClass roiClass) {
        this.roiClass = roiClass;
    }
    
    public int TransferGrayTone(ROIClass roiClass,int originalGrayLevel, int binCount){

        int tempGreyTone;
        
        int greyToneRange=roiClass.getMaxSignalList().get(1)-roiClass.getMinSignalList().get(1);
        tempGreyTone=(originalGrayLevel-roiClass.getMinSignalList().get(1))/greyToneRange*binCount;

        int greyTone=(int)tempGreyTone==binCount?(binCount-1):(int)(tempGreyTone);
//        System.out.println("greyTone: "+greyTone);
        if(greyTone>=binCount+1)System.out.println("Min:"+roiClass.getMinSignalList().get(1)+
                "\tMax:"+roiClass.getMaxSignalList().get(1)+"\tOrgSignal:"+originalGrayLevel+"\tTempTone:"+tempGreyTone);
        return greyTone;
    }

    public int[][] ConvertToMatrix(int binCount) {

        int maxX = roiClass.getMaxX();
        int maxY = roiClass.getMaxY();
        int minX = roiClass.getMinX();
        int minY = roiClass.getMinY();
        HashMap<Integer,ImagePixel> hashMap_imagePixel=roiClass.getRoiPixels();

        int[][] matrix = new int[maxX - minX + 1][maxY - minY + 1];//set matrix element to 0
        for (int i = 0; i < maxX - minX + 1; i++) {
            for (int j = 0; j < maxY - minY + 1; j++) {
                matrix[i][j] = -1;
            }

        }
        
        Iterator<Integer> it=hashMap_imagePixel.keySet().iterator();
        while(it.hasNext()){
            int key=it.next();
            ImagePixel imagePixel=hashMap_imagePixel.get(key);
        int grayTone=hashMap_imagePixel.get(key).getGreyChannels().get(1);
        matrix[imagePixel.getPixelX()-minX][imagePixel.getPixelY()-minY]=TransferGrayTone(roiClass,grayTone,binCount);
        
        }
        

        return matrix;
    }

    
}
