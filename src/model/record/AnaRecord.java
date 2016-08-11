/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.record;

import java.util.HashSet;
import main.java.lis.constant.DiagnosisConstant.ANA_Result;

/**
 *
 * @author mlei
 */
public class AnaRecord {
// summary.xls may require error msgs

    private int sampID;
    private final String julien_barcode;
    private ANA_Result mannualANA3;
    private final ANA_Result plate1ANA3;
    private final ANA_Result plate2ANA3;
    private final String plate1ID;
    private final String plate2ID;
    private Result MvsP1;
    private Result MvsP2;
    private Result P1vsP2;
    private final HashSet<Comment> COMMENTS;

//    public static final String NO_MANNUAL_RESULT = "This sample is pending or does not exist in `vibrant_america_information`.`sample_data`; ";
//    public static final String UNDEFINED_MANNUAL_RESULT = "The value of this sample ANA3 in `vibrant_america_information`.`sample_data` is undefined; ";
//    public static final String NO_PLATE1 = "Error in plate1: plate1 result is missing; ";
//    public static final String NO_PLATE2 = "Error in plate2: plate2 result is missing;";
    public enum Comment {
        NO_MANNUAL_RESULT(1, "This sample is pending or does not exist in `vibrant_america_information`.`sample_data`"),
        UNDEFINED_MANNUAL_RESULT(2, "The value of this sample ANA3 in `vibrant_america_information`.`sample_data` is undefined"),
        NO_PLATE1(3, "Error in plate1: plate1 result is missing"),
        NO_PLATE2(4, "Error in plate2: plate2 result is missing");
        private final int id;
        private final String description;

        Comment(int id, String description) {
            this.id = id;
            this.description = description;
        }
    }

    public enum Result {
        PENDING,
        MISSING_PLATE1,
        MISSING_PLATE2,
        MISSING_PLATE1_AND_PLATE2,
        IDENTICAL,
        NONIDENTICAL;
    }

    public AnaRecord(String julien_barcode, ANA_Result plate1ANA3, ANA_Result plate2ANA3, String plate1ID, String plate2ID, String msg1, String msg2) {
        this.julien_barcode = julien_barcode;
        this.plate1ANA3 = plate1ANA3;
        this.plate2ANA3 = plate2ANA3;
        this.plate1ID = plate1ID;
        this.plate2ID = plate2ID;
        this.COMMENTS = new HashSet<>();
    }

    public void setSampID(int sampID) {
        this.sampID = sampID;
    }

    public String getJulien_barcode() {
        return julien_barcode;
    }

    public void setMannualANA3(ANA_Result mannualANA3) {
        this.mannualANA3 = mannualANA3;
    }

    public int getSampID() {
        return sampID;
    }

    public ANA_Result getMannualANA3() {
        return mannualANA3;
    }

    public ANA_Result getPlate1ANA3() {
        return plate1ANA3;
    }

    public ANA_Result getPlate2ANA3() {
        return plate2ANA3;
    }

    public String getPlate1ID() {
        return plate1ID;
    }

    public String getPlate2ID() {
        return plate2ID;
    }

    public void clearComment() {
        COMMENTS.clear();
    }

    public void addComment(Comment comment) {
        COMMENTS.add(comment);
    }

    @Override
    public String toString() {
        String selfIntro;
        selfIntro = "ID=" + this.sampID + " ";
        selfIntro += "barcode=" + this.julien_barcode + " ";
        if (this.mannualANA3 != null) {
            selfIntro += "mannualANA3=" + this.mannualANA3.name();
        } else {
            System.out.println("null");
        }
        selfIntro += "plate1 result=" + this.plate1ANA3.name();
        selfIntro += "plate2 result=" + this.plate2ANA3.name();
        selfIntro += "plate1 ID=" + this.plate1ID + " ";
        selfIntro += "plate2 ID=" + this.plate2ID + " ";
        if (this.mannualANA3 == ANA_Result.NO_RESULT || this.mannualANA3 == null) {
            selfIntro += " unknown ana3=" + Comment.NO_MANNUAL_RESULT.description;
        }
        return selfIntro;
    }

    /**
     *
     * @return false if
     * this.plate2ANA3.equals(this.mannualANA3)==false&&this.mannualANA3!=null
     * i.e. the final call from 2nd plate is inconsistent with manual judgment
     *
     * @return true otherwise, even if this.mannualANA3==null
     *
     * however, in case of this.mannualANA3 or this.plate2ANA3 being null, a
     * comment of "missing mannual/plate2 result" will be added to COMMENTS;
     *
     * in case of this.plate1ANA3.equals(this.plate2ANA3)==false, a comment of
     * "inconsistance between 1st plate and 2nd plate results" will be add to
     * COMMENTS.
     *
     */
    public void initAccuracyAndConsistency() {
        
        if (this.sampID == 0) {
            this.COMMENTS.add(Comment.NO_MANNUAL_RESULT);
        }
        
        if (ANA_Result.NO_RESULT.equals(this.mannualANA3) || this.mannualANA3 == null) {
            this.MvsP1 = Result.PENDING;
            this.MvsP2 = Result.PENDING;
            this.COMMENTS.add(Comment.NO_MANNUAL_RESULT);
            if (ANA_Result.NO_RESULT.equals(this.plate2ANA3) || this.plate2ANA3 == null) {
                this.P1vsP2=Result.PENDING;
            }else{
                this.P1vsP2=this.plate2ANA3.equals(this.plate1ANA3)?Result.IDENTICAL:Result.NONIDENTICAL;
            }       
        } else {
            //mvp1, p1vp2
            if (ANA_Result.NO_RESULT.equals(this.plate1ANA3) || this.plate1ANA3 == null) {
                this.MvsP1=Result.PENDING;
                
                if (ANA_Result.NO_RESULT.equals(this.plate2ANA3) || this.plate2ANA3 == null) {
                    this.P1vsP2 = null;
                    this.MvsP2=null;
                }else{
                    this.P1vsP2=Result.MISSING_PLATE1;
                    this.MvsP2=this.plate2ANA3.equals(this.mannualANA3)?Result.IDENTICAL:Result.NONIDENTICAL;
                }
            }else{
                this.MvsP1=this.mannualANA3.equals(this.plate1ANA3)?Result.IDENTICAL:Result.NONIDENTICAL;
                if (ANA_Result.NO_RESULT.equals(this.plate2ANA3) || this.plate2ANA3 == null) {
                    this.P1vsP2 = null;
                    this.MvsP2=null;
                }else{
                    this.MvsP2=this.plate2ANA3.equals(this.mannualANA3)?Result.IDENTICAL:Result.NONIDENTICAL;
                    this.P1vsP2 =this.plate2ANA3.equals(this.plate1ANA3)?Result.IDENTICAL:Result.NONIDENTICAL;
                }
            }
        }
        isValid();

    }

    public boolean isValid() {
        boolean b1 = (this.plate1ANA3 == null || ANA_Result.NO_RESULT.equals(this.plate1ANA3)) == (this.getPlate1ID() == null || "".equals(this.plate1ID));
        if (!b1) {
            this.MvsP1=Result.MISSING_PLATE1;
            if(Result.MISSING_PLATE2.equals(this.P1vsP2)){
                this.P1vsP2=Result.MISSING_PLATE1_AND_PLATE2;
            }else{
                this.P1vsP2=Result.MISSING_PLATE1;
            }
            
            System.out.println("Plate 1 result: " + this.plate1ANA3 + " absent: " + (this.plate1ANA3 == null || ANA_Result.NO_RESULT.equals(this.plate1ANA3)));
            System.out.println("Plate 1 ID: " + this.plate1ID + " absent: " + (this.getPlate1ID() == null));
            this.COMMENTS.add(Comment.NO_PLATE1);
        }
        boolean b2 = (this.plate2ANA3 == null || ANA_Result.NO_RESULT.equals(this.plate2ANA3)) == (this.getPlate2ID() == null || "".equals(this.plate2ID));
        if (!b2) {
            this.MvsP2=Result.MISSING_PLATE2;
            if(Result.MISSING_PLATE1.equals(this.P1vsP2)){
                this.P1vsP2=Result.MISSING_PLATE1_AND_PLATE2;
            }else{
                this.P1vsP2=Result.MISSING_PLATE2;
            }
            System.out.println("Plate 2 result: " + this.plate2ANA3 + " absent: " + (this.plate2ANA3 == null || ANA_Result.NO_RESULT.equals(this.plate2ANA3)));
            System.out.println("Plate 2 ID: " + this.plate2ID + " absent: " + (this.getPlate2ID() == null));
            this.COMMENTS.add(Comment.NO_PLATE2);
        }
        return b1 && b2;
    }
    

    public String concatComments() {
        if (this.COMMENTS.isEmpty()) {
            return "";
        }
        String msg = "";
        String echo = "";
        for (Comment cmnt : this.COMMENTS) {
            if(!"".equals(msg))msg+=",";
            msg += cmnt.name();
            echo += cmnt.name() + "\t";
        }
        System.out.println(this.julien_barcode + ":\t" + echo);
        return msg;
    }

    public Result getMvsP1() {
        return MvsP1;
    }

    public void setMvsP1(Result MvsP1) {
        this.MvsP1 = MvsP1;
    }

    public Result getMvsP2() {
        return MvsP2;
    }

    public void setMvsP2(Result MvsP2) {
        this.MvsP2 = MvsP2;
    }

    public Result getP1vsP2() {
        return P1vsP2;
    }

    public void setP1vsP2(Result P1vsP2) {
        this.P1vsP2 = P1vsP2;
    }
    
    
}
