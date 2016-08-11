/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import constants.PlateConstants;
import exceptions.MissingCtrlSampleException;
import exceptions.WrongANAPlateException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarPlateInfo;
import main.java.com.instrumentInterface.TSPANAPlateInterface;
import main.java.exceptions.TSPTestException;
import model.plate.ANAPlate;

/**
 *
 * @author mlei
 */
public class OperationQueue implements Runnable {

    public static boolean enableWatershed = constants.PlateConstants.ENABLE_WATERSHED;
    public static boolean only488 = constants.PlateConstants.ONLY_488;
    private final LinkedList<String[]> PlateFolderName_PlateBarcode;
    private static final OperationQueue instance;
    public static final int PLATE_PREV_SIZE = 3;

    static {
        instance = new OperationQueue();
    }

    private OperationQueue() {
        PlateFolderName_PlateBarcode = new LinkedList<>();
    }

    public static OperationQueue getQueue() {
        return instance;
    }

    @Override
    public void run() {
        /*
        empty?wait
        else dequeue
        
            enqueue
                linkedlist.add, notify
        
            dequeue
                empty?wait
                else new plateInfo,notify,runOnePlate
        
                
         */
        
        while (true) {
            
            if (!PlateFolderName_PlateBarcode.isEmpty()) {
                dequeue();
            } else {
                synchronized (PlateFolderName_PlateBarcode) {
                    try {
                        PlateFolderName_PlateBarcode.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(OperationQueue.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public synchronized void dequeue() {
        String[] folderName_plateId;
        if (PlateFolderName_PlateBarcode != null && !PlateFolderName_PlateBarcode.isEmpty()) {
            folderName_plateId = PlateFolderName_PlateBarcode.remove();
            try {
                ANAPillarPlateInfo plateInfo = TSPANAPlateInterface.getANAPlateSampleInfo(folderName_plateId[0], folderName_plateId[1]);
                ANAPlate.runOnePlate(plateInfo, enableWatershed, only488);

                if (!PlateConstants.DEBUG_MODE) {
                    System.out.println(Thread.currentThread() + " processed " + plateInfo.getPillarPlateID());
                    String str="";String[] name;int len=PlateFolderName_PlateBarcode.size()<PLATE_PREV_SIZE?PlateFolderName_PlateBarcode.size():PLATE_PREV_SIZE;
                    for(int i=0;i<len;i++){
                        name = PlateFolderName_PlateBarcode.get(i);
                        if(name!=null)str+=name[1]+";";
                    }
                    
                    System.out.println("remaining plates (first "+PLATE_PREV_SIZE+ ") :"+str);
                    System.out.println("");
                }
            } catch (SQLException | TSPTestException | WrongANAPlateException | MissingCtrlSampleException | IOException ex) {
                Logger.getLogger(OperationQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void enqueue(String plateImageFolderName, String pillarPlateBarcode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (PlateFolderName_PlateBarcode) {
                    PlateFolderName_PlateBarcode.add(new String[]{plateImageFolderName, pillarPlateBarcode});
                    System.out.println("");
                    System.out.println("enque " + plateImageFolderName);
//                    System.out.println(PlateFolderName_PlateBarcode.size() + " remaining");
                    String str="";String[] name;int len=PlateFolderName_PlateBarcode.size()<PLATE_PREV_SIZE?PlateFolderName_PlateBarcode.size():PLATE_PREV_SIZE;
                    for(int i=0;i<len;i++){
                        name = PlateFolderName_PlateBarcode.get(i);
                        if(name!=null)str+=name[1]+";";
                    }
                    System.out.println("remaining plates (first "+PLATE_PREV_SIZE+ ") :"+str);
                    PlateFolderName_PlateBarcode.notify();
                }
            }
        }).start();

    }

}
