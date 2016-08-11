/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author mlei
 */
public class MissingCtrlSampleException extends Exception {

    public MissingCtrlSampleException(String pillarPlateID) {
        super("missing control samples on "+pillarPlateID);
    }
    
}
