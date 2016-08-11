/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.instrumentInterface;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import main.java.com.dto.ANAPillarInfo;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.dto.ANASampleInfo;
import main.java.com.dto.SampleImage;
import main.java.common.utils.DataBaseUtility;
import main.java.constant.TSPSystemConstants;
import main.java.exceptions.TSPTestException;
import main.java.instruments.InstrumentConstants;
import main.java.instruments.InstrumentConstants.ANA_PLATE_TYPE;
import main.java.instruments.InstrumentConstants.IMAGE_CHANNEL;
import main.java.lis.constant.DiagnosisConstant.ANA_Titer;
import main.java.lis.constant.PConstants;

/**
 *
 * @author Kang Bei
 */
public class TSPANAPlateInterface {
    
//    private final String plateID;
//    
//    public TSPANAPlateInterface(String plateID){
//        this.plateID = plateID;
//    }
    public static final boolean DEBUG_MODE=true;
    private static String IMAGE_ROOT_PATH = "J:\\ANA\\";//DEBUG_MODE?"J:\\ANA\\":"P:\\Data\\TestDevice\\";
    private static final String IMAGE_FILE_TITLE = "Scanner_TestDevice_Plate_";
    private static final String IMAGE_FILE_CHIP_SECTION = "_Chip_";
    private static final String IMAGE_FILE_TAIL_reflect = "_IMAGE_0_0.tif";
    private static final String IMAGE_FILE_TAIL_488nm = "_IMAGE_0_1.tif";
    private static final String IMAGE_FILE_TAIL_647nm = "_IMAGE_0_2.tif";
    private static final HashMap<String,ANA_Titer> pillarToTiter = new HashMap<>();
    
    private static final int ANA_PLATE_TYPE_2_SAMPLE_COUNT = 6;
    
    static{
        pillarToTiter.put("1",ANA_Titer.ANA_1_40);
        pillarToTiter.put("2",ANA_Titer.ANA_1_80);
        pillarToTiter.put("3",ANA_Titer.ANA_1_160);
        pillarToTiter.put("4",ANA_Titer.ANA_1_320);
        pillarToTiter.put("5",ANA_Titer.ANA_1_640);
        pillarToTiter.put("6",ANA_Titer.ANA_1_1280);
        pillarToTiter.put("7",ANA_Titer.ANA_1_40);
        pillarToTiter.put("8",ANA_Titer.ANA_1_80);
        pillarToTiter.put("9",ANA_Titer.ANA_1_160);
        pillarToTiter.put("10",ANA_Titer.ANA_1_320);
        pillarToTiter.put("11",ANA_Titer.ANA_1_640);
        pillarToTiter.put("12",ANA_Titer.ANA_1_1280);
    }
    
    public static void setImageRootPath(String newRootPath){
        TSPANAPlateInterface.IMAGE_ROOT_PATH = newRootPath;
    }
    
    public static ANAPillarPlateInfo getANAPlateSampleInfo(String plateImageFolderName,String pillarPlateBarcode) throws SQLException, TSPTestException{
        
        ANAPillarPlateInfo returnValue = null;
        
        try(DataBaseUtility dbUtility = new DataBaseUtility("",PConstants.TSP_SERVER)){
            String getPillarPlate = "SELECT * FROM "+TSPSystemConstants.TSP_PILLAR_PLATE_INFO+" WHERE pillar_plate_id = ?";
            dbUtility.changeSQL(getPillarPlate);
            dbUtility.setString(1, pillarPlateBarcode);
            
            String testName = "N/A";
            String status = "N/A";
            String wellPlateID = "N/A";
            
            if(dbUtility.generateRecords_throwException()){
                ResultSet res = dbUtility.getRecords();
                if(res.next()){
                    wellPlateID = res.getString("well_plate_id");
                    testName = res.getString("test_name");
                    status = res.getString("status");
                }else{
                    throw new TSPTestException("Can't find pillar plate: "+pillarPlateBarcode);
                }
            }
            
            HashMap<String,HashMap<ANA_Titer,ANAPillarInfo>> samplePillarMap= new HashMap<>();
            
            String getWellInfo = "SELECT * FROM "+TSPSystemConstants.TSP_WELL_INFO+" WHERE well_plate_id = ?";
            dbUtility.changeSQL(getWellInfo);
            dbUtility.setString(1, wellPlateID);
            if(dbUtility.generateRecords_throwException()){
                ResultSet res = dbUtility.getRecords();
                while(res.next()){
                    String pillarRow = res.getString("well_row");
                    String pillarCol = res.getString("well_col");
                    String sampleBarcode = res.getString("julien_barcode");
                    
                    String pillarID = pillarRow+pillarCol;
                    if(pillarToTiter.containsKey(pillarCol)){
                        
                        HashMap<IMAGE_CHANNEL,SampleImage> sampleImageList = new HashMap<>();
                        
                        File imageReflect = new File(IMAGE_ROOT_PATH+plateImageFolderName+"\\"+pillarID+"\\"+
                                IMAGE_FILE_TITLE+pillarPlateBarcode+IMAGE_FILE_CHIP_SECTION+pillarID+IMAGE_FILE_TAIL_reflect);
                        
                        File image488nm = new File(IMAGE_ROOT_PATH+plateImageFolderName+"\\"+pillarID+"\\"+
                                IMAGE_FILE_TITLE+pillarPlateBarcode+IMAGE_FILE_CHIP_SECTION+pillarID+IMAGE_FILE_TAIL_488nm);
                        
                        File image647nm = new File(IMAGE_ROOT_PATH+plateImageFolderName+"\\"+pillarID+"\\"+
                                IMAGE_FILE_TITLE+pillarPlateBarcode+IMAGE_FILE_CHIP_SECTION+pillarID+IMAGE_FILE_TAIL_647nm);
                        
                        if(imageReflect.exists()){
                            sampleImageList.put(IMAGE_CHANNEL.CHANNELreflect,new SampleImage(IMAGE_CHANNEL.CHANNELreflect,imageReflect));
                        }else{
                            throw new TSPTestException("Can't find image file: "+imageReflect.getPath());
                        }
                        if(image488nm.exists()){
                            sampleImageList.put(IMAGE_CHANNEL.CHANNEL488,new SampleImage(IMAGE_CHANNEL.CHANNEL488,image488nm));
                        }else{
                            throw new TSPTestException("Can't find image file: "+imageReflect.getPath());
                        }
                        if(image647nm.exists()){
                            sampleImageList.put(IMAGE_CHANNEL.CHANNEL647,new SampleImage(IMAGE_CHANNEL.CHANNEL647,image647nm));
                        }else{
                            throw new TSPTestException("Can't find image file: "+imageReflect.getPath());
                        }
                        
                        if(samplePillarMap.containsKey(sampleBarcode)){
                            samplePillarMap.get(sampleBarcode).put(pillarToTiter.get(pillarCol),
                                    new ANAPillarInfo(pillarID,pillarRow,pillarCol,sampleBarcode,pillarToTiter.get(pillarCol),sampleImageList));
                        }else{
                            samplePillarMap.put(sampleBarcode, new HashMap<>());
                            samplePillarMap.get(sampleBarcode).put(pillarToTiter.get(pillarCol),
                                    new ANAPillarInfo(pillarID,pillarRow,pillarCol,sampleBarcode,pillarToTiter.get(pillarCol),sampleImageList));
                        }
                    }else{
                        throw new TSPTestException("Can't ANA titer for pillar col: "+pillarCol);
                    } 
                }
            }
            
            if(samplePillarMap.size()<=0){
                throw new TSPTestException("No sample on pillar plate:"+pillarPlateBarcode);
            }else{
                //Get ANA plate type
                ANA_PLATE_TYPE anaPlateType = checkANAPlateType(samplePillarMap);
                List<ANASampleInfo> sampleInfoList = new ArrayList<>();
                
                ANASampleInfo posCtrl = null;
                ANASampleInfo negCtrl = null;
                
                for(Entry<String, HashMap<ANA_Titer,ANAPillarInfo>> entry:samplePillarMap.entrySet()){
                    
                    if(entry.getKey().length()<10){
                        String getQC = "SELECT * FROM "+"`vibrant_qc_tracking`.`qc_training_set` WHERE cal_id = ? AND disease_name = 'ANA'";
                        dbUtility.changeSQL(getQC);
                        dbUtility.setString(1, entry.getKey());
                        if(dbUtility.generateRecords_throwException()){
                            ResultSet rs = dbUtility.getRecords();
                            if(rs.next()){
                                String qcType = rs.getString("cal_type");
                                if(qcType.equals("Pos_Ctrl")){
                                    posCtrl = new ANASampleInfo(entry.getKey(),entry.getValue());
                                }else if(qcType.equals("Neg_Ctrl")){
                                    negCtrl = new ANASampleInfo(entry.getKey(),entry.getValue()); 
                                }
                            }
                        }
                    }else{
                        sampleInfoList.add(new ANASampleInfo(entry.getKey(),entry.getValue()));
                    }
                }
                
                
                if(posCtrl!=null&&negCtrl!=null){
                    returnValue = new ANAPillarPlateInfo(pillarPlateBarcode,IMAGE_ROOT_PATH+plateImageFolderName+"\\", wellPlateID, testName, status, anaPlateType, sampleInfoList,posCtrl,negCtrl);
                }else{
                    throw new TSPTestException("Can't find Positive or Negative Controller.Neg_Ctrl is null:"+(negCtrl==null)+"\tPos_Ctrl is null:"+(posCtrl==null));
                }
            }

        }
        
        return returnValue;
        
    }
    
    
    private static ANA_PLATE_TYPE checkANAPlateType(HashMap<String,HashMap<ANA_Titer,ANAPillarInfo>> samplePillarMap){
        
        int countSampleLikeType2Plate = 0;
        for(Entry<String, HashMap<ANA_Titer,ANAPillarInfo>> entry:samplePillarMap.entrySet()){
            if(entry.getValue().size()==ANA_PLATE_TYPE_2_SAMPLE_COUNT){
                countSampleLikeType2Plate++;
            }
        }
        
        if(countSampleLikeType2Plate>=1){
            return ANA_PLATE_TYPE.TYPE_2;
        }else{
            return ANA_PLATE_TYPE.TYPE_1;
        }
    }
    
    
}
