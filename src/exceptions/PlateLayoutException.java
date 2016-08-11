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
public class PlateLayoutException extends Exception{

    public PlateLayoutException() {
        super();
    }

    public PlateLayoutException(String pillarAddress) {
        super(pillarAddress + " has multiple pillars.");
    }
}
