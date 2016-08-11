/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.dto;

import java.io.File;
import main.java.instruments.InstrumentConstants.IMAGE_CHANNEL;

/**
 *
 * @author Kang Bei
 */
public class SampleImage {
    
    private final IMAGE_CHANNEL imageChannel;
    private final File imageFileList;
    
    public SampleImage(IMAGE_CHANNEL imageChannel, File imageFileList){
        this.imageChannel = imageChannel;
        this.imageFileList = imageFileList;
    }

    /**
     * @return the sampleBarcode
     */
    public IMAGE_CHANNEL getImageChannel() {
        return imageChannel;
    }

    /**
     * @return the imageFileList
     */
    public File getImageFileList() {
        return imageFileList;
    }
    
    
    
}
