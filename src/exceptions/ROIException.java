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
public class ROIException extends Exception {

    public ROIException() {
        super();
    }

    public ROIException(String address) {
        super("Null NucleusList " + address);
    }
}
