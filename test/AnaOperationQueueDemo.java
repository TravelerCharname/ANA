
import constants.PlateConstants;
import java.io.File;
import java.util.LinkedList;
import main.java.com.dto.ANAPillarPlateInfo;
import tools.OperationQueue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mlei
 */
public class AnaOperationQueueDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        NormalCase();
ThreadSafetyTest();
    }

    public static void ThreadSafetyTest() {
        int ind=0;
        String plateImageFolderName;
        String pillarPlateBarcode;
        
        
        if(PlateConstants.DEBUG_MODE)System.out.println("Operation Queue started");
        
//        ArrayList<ANAPillarPlateInfo> p2=new ArrayList<>();
for (File folder : new File("J:\\ANA").listFiles()) {
    if (folder.getName().compareTo("ANAC80020310000545_20160714100233") < 0) {
                    continue;
                }
    
    if(ind++%3==0){
        
        new Thread(OperationQueue.getQueue()).start();}
    plateImageFolderName = folder.getName();
    pillarPlateBarcode = folder.getName().split("_")[0];
    OperationQueue.getQueue().enqueue(plateImageFolderName, pillarPlateBarcode,null); 
//            if(PlateConstants.DEBUG_MODE)System.out.println("enqueue "+plateImageFolderName);
}
    }

    public static void NormalCase() {
        OperationQueue queue = OperationQueue.getQueue();
        new Thread(queue).start();
        if (PlateConstants.DEBUG_MODE) {
            System.out.println("Operation Queue started");
        }

        String plateImageFolderName;
        String pillarPlateBarcode;
        plateImageFolderName = "ANAC80020310000545_20160714100233";
        pillarPlateBarcode = plateImageFolderName.split("_")[0];
        queue.enqueue(plateImageFolderName, pillarPlateBarcode,null);
    }
    
    

}
