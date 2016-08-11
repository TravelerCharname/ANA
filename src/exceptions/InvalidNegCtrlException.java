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
public class InvalidNegCtrlException extends Exception {

    public InvalidNegCtrlException() {
        super();
    }

    public InvalidNegCtrlException(String plateID) {
        super("Erroneous Negative Control reading on " + plateID + ".");
    }
}
