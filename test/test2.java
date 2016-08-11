
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.ANAChipImage.ANA_CellRecognition;
import main.java.com.imageProcessException.ChipInfoException;
import main.java.com.imageProcessException.ROIException;
import main.java.com.imagemodel.ANA_ROI_Result;
import tools.IOHelper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mlei
 */
public class test2 {
    public static final String PREF="Scanner_TestDevice_Plate_";
    public static final String SUFF_488="_IMAGE_0_1.tif";
    public static final String SUFF_REF="_IMAGE_0_0.tif";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            patternCV();
        } catch (ChipInfoException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ROIException ex) {
            Logger.getLogger(test2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void patternCV() throws ChipInfoException, IOException, ROIException{
        String root="C:\\Users\\mlei\\Desktop\\pn\\try patterns";
        
        BufferedWriter bw = null;
        File output;
        boolean cleanEdge=false,enableWatershed=false,only488=false;
        String line;
        String[] arr;
        String plateId,chipLocation,pattern;
        
        File pref;
        ANA_ROI_Result roi;
        for(File folder:new File(root).listFiles()){
            if(folder.isDirectory()){
                pattern = folder.getName();
                output=new File(folder,pattern+".txt");
                bw = IOHelper.getWriter(output, false);
                line="plate\tchip location\tpattern\tmean\tstd\tcv\tmean of mean\tmean of 12p\tmean of quant";
                bw.write(line);bw.flush();bw.newLine();
                
                //Scanner_TestDevice_Plate_ANAC80020190000369_Chip_C5_IMAGE_0_1
                for(File p488:folder.listFiles()){
                    if(p488.getName().endsWith(SUFF_488)){
                        arr=p488.getName().split("_");
                        plateId=arr[3];
                        chipLocation=arr[5];
                        pref=new File(p488.getAbsolutePath().replace(SUFF_488, SUFF_REF));
                        ArrayList<File> fileList = new ArrayList<>();
                        fileList.add(pref);fileList.add(p488);
                        
                        roi = ANA_CellRecognition.runANA_ROI(fileList, null, cleanEdge,enableWatershed,only488);
                        line=plateId+DEL+chipLocation+DEL+pattern+DEL+roi.getStat_Mean()+DEL+roi.getStat_Std()+DEL+roi.getStat_CV()+DEL+roi.getSignalMean()+DEL+roi.getSignal125()+DEL+roi.getSignalQ();
                        bw.write(line);bw.flush();bw.newLine();
                    }
                }
            }
        }
        if(bw!=null)bw.close();
    }
    public static final String DEL = "\t";
    
    
}
