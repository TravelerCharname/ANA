/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import java.util.HashMap;

/**
 *
 * @author Xinchen Cai
 */
public enum WarningMessage {
   
    PositiveControlLinearity(1,"non-linear positive control"),//R2 th=0.85
    PositiveNegativeControlComparison(2,"Positive Control Titer is lower than 320"),
    SampleLinearity(3,"non-linear 2nd plate sample"),
    ChangeToNegative(4,"Changed sample Status to negative"),
    ImagePixelRatio(5,"Image pixel ratio less than 0.3"),//pxl ratio th=0.3
    UndecidedPatern(6,"All founded pattern percentage less than threshold"),
    NoSupportingFile1(7,"Supporting file(s) of plate type 1 not found"),
    NoSupportingFile2(8,"Supporting file(s) of plate type 2 not found"),
    UnknownPredictResult(9,"Unknown predict result"),
    WeakPositive(10,"Report titer is 40"),
    ANAROIResultNotFound(11,"ANA ROI result not found"),
    WrongFirstPlateLayout(12,"Type 1 Sample or Control Sample has multiple pillars."), 
    WrongSecondPlateLayout(13,"Number of pillars is not 6 for this Type 2 Sample."),
    NegCtrlFailed(14,"Erroneous reading on Negative Control"),
    PosCtrlFailed(15,"critical ratio PosCtrl:NegCtrl is less than defined threshold"),//posSignal<negSignal*coeff, ratio_th=3.5
    ControlQCFailed(16,"Control Samples failed or missing");
    int id;
    String msg;
    private static HashMap<Integer,WarningMessage> map;
    static{
        map=new HashMap<>();
        for(WarningMessage wm:values()){
            map.put(wm.id, wm);
        }
    }
    WarningMessage(int i,String str){
    this.id=i;
    this.msg=str;
    }

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }
    
    public static WarningMessage getWarningMessageById(int id){
        return map.get(id);
    }
    
    public static String getNameById(int id){
        return map.get(id).name();
    }
}
