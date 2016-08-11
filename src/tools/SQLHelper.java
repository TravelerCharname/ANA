/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import constants.WarningMessage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.common.utils.DataBaseUtility;
import main.java.instruments.InstrumentConstants;
import main.java.lis.constant.DiagnosisConstant;
import main.java.lis.constant.DiagnosisConstant.ANA_Result;
import main.java.lis.constant.PConstants;
import model.plate.ANAPlate;
import model.plate.ANATestResult;
import model.record.AnaRecord;
import model.record.AnaRecord.Comment;

/**
 *
 * @author mlei
 */
public class SQLHelper {

    public static final int MAX_FAIL = 3;
    public static final boolean CONFIRM = true;
    public static final boolean REVERSE = false;

    public static int updatePlate(ANAPlate plate) {

        if (null != plate.getType()) {
            switch (plate.getType()) {
                case TYPE_1:
                    return updatePlate1(plate);
                case TYPE_2:
                    return updatePlate2(plate);
                default:
                    throw new RuntimeException("Unknown plate type: " + plate.getType());
            }
        }
        return -1;
    }

    public static int updatePlate1(ANAPlate plate) {
        String errorMessage = "";
        for (int id : plate.getPlateErr()) {
            if (!"".equals(errorMessage)) {
                errorMessage += ",";
            }
            errorMessage += id;
        }
        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
            String sqlQuery = "INSERT INTO `ana_plate_result`.`ana_plate_type1` "
                    + "(plate1_id,plate1_pos_ctrl,plate1_neg_ctrl,sample_number,error_message,finish_time) "
                    + "VALUES (?,?,?,?,?,now()) ON DUPLICATE KEY "
                    + "UPDATE plate1_pos_ctrl=?,plate1_neg_ctrl=?,sample_number=?,error_message=?,finish_time=now()";
            dbUtility.changeSQL(sqlQuery);

            dbUtility.setString(1, plate.getPlateId());
            if (plate.getPosCtrl() == null) {
                System.out.println();
                System.out.println("================================================================================");
                System.out.println(plate.getPlateId() + " POS CTRL IS NULL !!!");
                System.out.println("================================================================================");
                System.out.println();
                dbUtility.setDouble(2, -1);
                dbUtility.setDouble(6, -1);
            } else {
                dbUtility.setDouble(2, plate.getPosCtrl().getFirstPlateSignal());
                dbUtility.setDouble(6, plate.getPosCtrl().getFirstPlateSignal());
            }

            dbUtility.setDouble(3, plate.getNegCtrlSignal());
            dbUtility.setDouble(7, plate.getNegCtrlSignal());
            dbUtility.setInt(4, plate.getSampleNumber());
            dbUtility.setInt(8, plate.getSampleNumber());
            dbUtility.setString(5, errorMessage);
            dbUtility.setString(9, errorMessage);
            return dbUtility.executeUpdate_throwException();
        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static int updateFailedPlate1(ANAPlate plate) {
        String errorMessage = "";
        for (int id : plate.getPlateErr()) {
            if (!"".equals(errorMessage)) {
                errorMessage += ",";
            }
            errorMessage += id;
        }
        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
            String sqlQuery = "INSERT INTO `ana_plate_result`.`ana_plate_type1` "
                    + "(plate1_id,plate1_pos_ctrl,plate1_neg_ctrl,sample_number,error_message,finish_time) "
                    + "VALUES (?,?,?,?,?,now()) ON DUPLICATE KEY "
                    + "UPDATE plate1_pos_ctrl=?,plate1_neg_ctrl=?,sample_number=?,error_message=?,finish_time=now()";
            dbUtility.changeSQL(sqlQuery);

            dbUtility.setString(1, plate.getPlateId());
            if (plate.getPosCtrl() == null) {
                dbUtility.setNull(2, java.sql.Types.DOUBLE);
                dbUtility.setNull(6, java.sql.Types.DOUBLE);
            } else {
                dbUtility.setDouble(2, plate.getPosCtrl().getFirstPlateSignal());
                dbUtility.setDouble(6, plate.getPosCtrl().getFirstPlateSignal());
            }
            dbUtility.setDouble(3, plate.getNegCtrlSignal());
            dbUtility.setDouble(7, plate.getNegCtrlSignal());
            dbUtility.setInt(4, plate.getSampleNumber());
            dbUtility.setInt(8, plate.getSampleNumber());
            dbUtility.setString(5, errorMessage);
            dbUtility.setString(9, errorMessage);
            return dbUtility.executeUpdate_throwException();
        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static int updatePlate2(ANAPlate plate) {
        String errorMessage = "";

        for (int id : plate.getPlateErr()) {
            if (!"".equals(errorMessage)) {
                errorMessage += ",";
            }
            errorMessage += id;
        }

        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL

            String sqlQuery = "INSERT INTO `ana_plate_result`.`ana_plate_type2` "
                    + "(plate2_id,plate2_pos_ctrl40,plate2_pos_ctrl80,plate2_pos_ctrl160,plate2_pos_ctrl320,plate2_pos_ctrl640,plate2_pos_ctrl1280,"
                    + "plate2_neg_ctrl,sample_number,error_message,finish_time) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,now()) ON DUPLICATE KEY "
                    + "UPDATE plate2_pos_ctrl40=?,plate2_pos_ctrl80=?,plate2_pos_ctrl160=?,plate2_pos_ctrl320=?,plate2_pos_ctrl640=?,plate2_pos_ctrl1280=?,"
                    + "plate2_neg_ctrl=?,sample_number=?,error_message=?,finish_time=now()";

            dbUtility.changeSQL(sqlQuery);

            dbUtility.setString(1, plate.getPlateId());

            dbUtility.setDouble(2, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
            dbUtility.setDouble(11, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
            dbUtility.setDouble(3, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
            dbUtility.setDouble(12, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
            dbUtility.setDouble(4, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
            dbUtility.setDouble(13, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
            dbUtility.setDouble(5, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
            dbUtility.setDouble(14, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
            dbUtility.setDouble(6, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
            dbUtility.setDouble(15, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
            dbUtility.setDouble(7, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));
            dbUtility.setDouble(16, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));
            dbUtility.setDouble(8, plate.getNegCtrlSignal());
            dbUtility.setDouble(17, plate.getNegCtrlSignal());
            dbUtility.setInt(9, plate.getSampleNumber());
            dbUtility.setInt(18, plate.getSampleNumber());
            dbUtility.setString(10, errorMessage);
            dbUtility.setString(19, errorMessage);

            return dbUtility.executeUpdate_throwException();

        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static int updateFailedPlate2(ANAPlate plate) {
        String errorMessage = "";
        for (int id : plate.getPlateErr()) {
            if (!"".equals(errorMessage)) {
                errorMessage += ",";
            }
            errorMessage += id;
        }

        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL

            String sqlQuery = "INSERT INTO `ana_plate_result`.`ana_plate_type2` "
                    + "(plate2_id,plate2_pos_ctrl40,plate2_pos_ctrl80,plate2_pos_ctrl160,plate2_pos_ctrl320,plate2_pos_ctrl640,plate2_pos_ctrl1280,"
                    + "plate2_neg_ctrl,sample_number,error_message,finish_time) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,now()) ON DUPLICATE KEY "
                    + "UPDATE plate2_pos_ctrl40=?,plate2_pos_ctrl80=?,plate2_pos_ctrl160=?,plate2_pos_ctrl320=?,plate2_pos_ctrl640=?,plate2_pos_ctrl1280=?,"
                    + "plate2_neg_ctrl=?,sample_number=?,error_message=?,finish_time=now()";

            dbUtility.changeSQL(sqlQuery);

            dbUtility.setString(1, plate.getPlateId());

            //2-7,11-16
            if (plate.getPosCtrl() == null) {
                dbUtility.setNull(2, java.sql.Types.DOUBLE);
                dbUtility.setNull(3, java.sql.Types.DOUBLE);
                dbUtility.setNull(4, java.sql.Types.DOUBLE);
                dbUtility.setNull(5, java.sql.Types.DOUBLE);
                dbUtility.setNull(6, java.sql.Types.DOUBLE);
                dbUtility.setNull(7, java.sql.Types.DOUBLE);
                dbUtility.setNull(11, java.sql.Types.DOUBLE);
                dbUtility.setNull(12, java.sql.Types.DOUBLE);
                dbUtility.setNull(13, java.sql.Types.DOUBLE);
                dbUtility.setNull(14, java.sql.Types.DOUBLE);
                dbUtility.setNull(15, java.sql.Types.DOUBLE);
                dbUtility.setNull(16, java.sql.Types.DOUBLE);
            } else {
                dbUtility.setDouble(2, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
                dbUtility.setDouble(11, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
                dbUtility.setDouble(3, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
                dbUtility.setDouble(12, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
                dbUtility.setDouble(4, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
                dbUtility.setDouble(13, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
                dbUtility.setDouble(5, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
                dbUtility.setDouble(14, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
                dbUtility.setDouble(6, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
                dbUtility.setDouble(15, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
                dbUtility.setDouble(7, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));
                dbUtility.setDouble(16, plate.getPosCtrl().getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));
            }
            dbUtility.setDouble(8, plate.getNegCtrlSignal());
            dbUtility.setDouble(17, plate.getNegCtrlSignal());
            dbUtility.setInt(9, plate.getSampleNumber());
            dbUtility.setInt(18, plate.getSampleNumber());
            dbUtility.setString(10, errorMessage);
            dbUtility.setString(19, errorMessage);

            return dbUtility.executeUpdate_throwException();

        } catch (SQLException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public static int updateSamples(ANAPlate plate) {
        int rows = 0;
        int result;
        String msg;
        if (InstrumentConstants.ANA_PLATE_TYPE.TYPE_1.equals(plate.getType())) {
            System.out.println("updating sample for " + plate.getType() + " plate " + plate.getPlateId());
//            ANATestResult1 fsi1 = null;

            for (ANATestResult testResult : plate.getTestResultList()) {
                msg = "";
                try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
                    String sqlQuery = "INSERT INTO `ana_plate_result`.`sample` "
                            + "(sample_id,plate1_id,plate1_signal,plate1_PN,plate1_error_message,plate1_pillar_position,finish_time) "
                            + "VALUES (?,?,?,?,?,?,now()) ON DUPLICATE KEY "
                            + "UPDATE plate1_id=?,plate1_signal=?,plate1_PN=?, plate1_error_message=?,plate1_pillar_position=?,finish_time=now()";
                    dbUtility.changeSQL(sqlQuery);
                    for (int id : testResult.getWarningMessage()) {
                        if (!"".equals(msg)) {
                            msg += ",";
                        }
                        msg += id;
                    }
                    dbUtility.setString(1, testResult.getJulien_barcode());

                    dbUtility.setString(2, plate.getPlateId());
                    dbUtility.setString(7, plate.getPlateId());

                    dbUtility.setDouble(3, testResult.getFirstPlateSignal());
                    dbUtility.setDouble(8, testResult.getFirstPlateSignal());

                    if (testResult.getPositivity() == null) {
                        dbUtility.setString(4, ANA_Result.NO_RESULT.name());
                        dbUtility.setString(9, ANA_Result.NO_RESULT.name());
                    } else {
                        dbUtility.setString(4, testResult.getPositivity().name());
                        dbUtility.setString(9, testResult.getPositivity().name());
                    }

                    dbUtility.setString(5, msg);
                    dbUtility.setString(10, msg);

                    dbUtility.setString(6, testResult.getPillarPosition());
                    dbUtility.setString(11, testResult.getPillarPosition());

                    // handle 0,-1 results
                    result = dbUtility.executeUpdate_throwException();
                    if (result <= 0) {
                        System.out.println(testResult + " rows affected for sample" + testResult.getJulien_barcode() + " @plate " + plate.getPlateId() + testResult.getPillarPosition());
                        System.out.println();
                        System.out.println();
                        result = 0;
                    }
                    rows += result;
                } catch (SQLException ex) {
                    Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (InstrumentConstants.ANA_PLATE_TYPE.TYPE_2.equals(plate.getType())) {
            System.out.println("updating sample for " + plate.getType() + " plate " + plate.getPlateId());
            for (ANATestResult testResult : plate.getTestResultList()) {
                msg = "";
                try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
                    String sqlQuery = "INSERT INTO `ana_plate_result`.`sample` "
                            + "(sample_id,plate2_PN,plate2_id,pattern_id,report_titer_id,titer_40_signal,titer_80_signal,titer_160_signal,titer_320_signal,titer_640_signal,titer_1280_signal,"
                            + "pattern_used_titer_id,centromere_percentage,homogeneous_percentage,nucleolar_percentage,peripheral_percentage,speckled_percentage,"
                            + "image_pixel_ratio,plate2_error_message,plate2_pillar_position,finish_time,`record status`) "
                            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),'PENDING') ON DUPLICATE KEY "
                            + "UPDATE plate2_PN=?,plate2_id=?,pattern_id=?,report_titer_id=?,"
                            + "titer_40_signal=?,titer_80_signal=?,titer_160_signal=?,titer_320_signal=?,titer_640_signal=?,titer_1280_signal=?,"
                            + "pattern_used_titer_id=?,centromere_percentage=?,homogeneous_percentage=?,nucleolar_percentage=?,peripheral_percentage=?,speckled_percentage=?,"
                            + "image_pixel_ratio=?,plate2_error_message=?,plate2_pillar_position=?,finish_time=now(),`record status`='PENDING'";
                    dbUtility.changeSQL(sqlQuery);

                    dbUtility.setString(1, testResult.getJulien_barcode());
                    if (testResult.getPositivity() == null) {
                        dbUtility.setString(2, ANA_Result.NO_RESULT.name());
                        dbUtility.setString(21, ANA_Result.NO_RESULT.name());
                    } else {
                        dbUtility.setString(2, testResult.getPositivity().name());
                        dbUtility.setString(21, testResult.getPositivity().name());
                    }
                    dbUtility.setString(3, plate.getPlateId());
                    dbUtility.setString(22, plate.getPlateId());
                    if (testResult.getPattern() != null) {
                        dbUtility.setInt(4, testResult.getPattern().getId());
                        dbUtility.setInt(23, testResult.getPattern().getId());
                    } else {
                        dbUtility.setInt(4, DiagnosisConstant.ANA_Pattern.NO_RESULT.getId());
                        dbUtility.setInt(23, DiagnosisConstant.ANA_Pattern.NO_RESULT.getId());
                    }
                    if (testResult.getTiter() == null) {
                        dbUtility.setInt(5, DiagnosisConstant.ANA_Titer.NO_RESULT.getId());
                        dbUtility.setInt(24, DiagnosisConstant.ANA_Titer.NO_RESULT.getId());
                    } else {
                        dbUtility.setInt(5, testResult.getTiter().getId());
                        dbUtility.setInt(24, testResult.getTiter().getId());
                    }
                    if (testResult.getSignals() == null || testResult.getSignals().isEmpty()) {
                        dbUtility.setDouble(6, -1);
                        dbUtility.setDouble(7, -1);
                        dbUtility.setDouble(8, - 1);
                        dbUtility.setDouble(9, -1);
                        dbUtility.setDouble(10, -1);
                        dbUtility.setDouble(11, -1);

                        dbUtility.setDouble(25, -1);
                        dbUtility.setDouble(26, -1);
                        dbUtility.setDouble(27, -1);
                        dbUtility.setDouble(28, -1);
                        dbUtility.setDouble(29, -1);
                        dbUtility.setDouble(30, -1);
                    } else {
                        dbUtility.setDouble(6, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
                        dbUtility.setDouble(7, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
                        dbUtility.setDouble(8, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
                        dbUtility.setDouble(9, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
                        dbUtility.setDouble(10, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
                        dbUtility.setDouble(11, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));

                        dbUtility.setDouble(25, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_40));
                        dbUtility.setDouble(26, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_80));
                        dbUtility.setDouble(27, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_160));
                        dbUtility.setDouble(28, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_320));
                        dbUtility.setDouble(29, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_640));
                        dbUtility.setDouble(30, testResult.getSignalByTiter(DiagnosisConstant.ANA_Titer.ANA_1_1280));
                    }
                    if (testResult.getTiter4Pattern() == null) {
                        dbUtility.setInt(12, DiagnosisConstant.ANA_Titer.NO_RESULT.getId());
                        dbUtility.setInt(31, DiagnosisConstant.ANA_Titer.NO_RESULT.getId());
                    } else {
                        dbUtility.setInt(12, testResult.getTiter4Pattern().getId());
                        dbUtility.setInt(31, testResult.getTiter4Pattern().getId());
                    }
                    if (testResult.getPattDistMap() == null || testResult.getPattDistMap().isEmpty()) {
                        dbUtility.setDouble(13, -1);
                        dbUtility.setDouble(14, -1);
                        dbUtility.setDouble(15, -1);
                        dbUtility.setDouble(16, -1);
                        dbUtility.setDouble(17, -1);

                        dbUtility.setDouble(32, -1);
                        dbUtility.setDouble(33, -1);
                        dbUtility.setDouble(34, -1);
                        dbUtility.setDouble(35, -1);
                        dbUtility.setDouble(36, -1);
                    } else {
                        dbUtility.setDouble(13, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.CENTROMERE));
                        dbUtility.setDouble(14, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.HOMOGENEOUS));
                        dbUtility.setDouble(15, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.NUCLEOLAR));
                        dbUtility.setDouble(16, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.PERIPHERAL));
                        dbUtility.setDouble(17, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.SPECKLED));

                        dbUtility.setDouble(32, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.CENTROMERE));
                        dbUtility.setDouble(33, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.HOMOGENEOUS));
                        dbUtility.setDouble(34, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.NUCLEOLAR));
                        dbUtility.setDouble(35, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.PERIPHERAL));
                        dbUtility.setDouble(36, testResult.getPattDistMap().get(DiagnosisConstant.ANA_Pattern.SPECKLED));
                    }
                    dbUtility.setDouble(18, testResult.getPixelRatio());
                    dbUtility.setDouble(37, testResult.getPixelRatio());
                    if (testResult.getWarningMessage() != null) {
                        for (int id : testResult.getWarningMessage()) {
                            if (!"".equals(msg)) {
                                msg += ",";
                            }
                            msg += id;
                        }
                    }
                    dbUtility.setString(19, msg);
                    dbUtility.setString(38, msg);

                    dbUtility.setString(20, testResult.getPillarPosition());
                    dbUtility.setString(39, testResult.getPillarPosition());

                    result = dbUtility.executeUpdate_throwException();
                    if (result <= 0) {
                        System.out.println(result + " rows affected for sample" + testResult.getJulien_barcode() + " @plate " + plate.getPlateId() + testResult.getPillarPosition());
                        System.out.println();
                        System.out.println();
                        result = 0;
                    } else {
                        System.out.println(testResult.getJulien_barcode() + ":" + result);
                    }
                    rows += result;
                } catch (SQLException ex) {
                    Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            throw new RuntimeException("Unknown plate type: " + plate.getPlateId());
        }

        return rows;
    }

    /*
    update/insert into `ana_plate_result`.`ana_record_status`
    update `ana_plate_result`.`sample` set status='recorded' where a.sample_id=b.julien_barcode
    
    columns:
    julien_barcode, sample_id, accuracy, consistency
     */
    public static int updateStatus(HashMap<String, AnaRecord> records) {
        int update;
        int confirm;
        int failed = 0;
        int success = 0;

        if (null == records) {
            System.out.println("Null input records...");
            return 0;
        }

        for (AnaRecord record : records.values()) {
            update = -2;
            confirm = -3;

            try {
//                record.isConsistant();
                update = updateRecordStatus(record);
            } catch (SQLException ex) {
                Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, "fail to update records to `ana_plate_result`.`ana_record_status`", ex);
            }

            if (update <= 0) {
                System.out.println("update failed for " + record);
                failed++;

            } else {

                try {
                    confirm = confirmUpdate(record, CONFIRM);
                } catch (SQLException ex) {
                    Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, "fail to change status in original table", ex);
                }

                if (confirm < 0) {
                    System.out.println(update + " records has been updated while " + confirm + " records has been confirmed for " + record
                            + "\r\nTrying to reverse change ...");
                    confirm = -3;
                    try {
                        confirm = confirmUpdate(record, REVERSE);
                    } catch (SQLException ex) {
                        Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (confirm < 0) {
                        throw new RuntimeException("fail to reverse unsuccessful update to `ana_plate_result`.`sample`");
                    }
                    System.out.println("Change has been reversed. (" + confirm + ")");
                } else {
                    success++;
                }
            }
        }

        System.out.println(success + " records have successfully been updated while " + failed + " have failed");
        return success;
    }

    public static int updateRecordStatus(AnaRecord record) throws SQLException {
        record.initAccuracyAndConsistency();

        String sql;
        int result = -1;

        try (DataBaseUtility db = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
            for (int fails = 0; fails < MAX_FAIL && result < 0; fails++) {

                sql = "INSERT INTO `ana_plate_result`.`ana_record_status` "
                        + "(julien_barcode, sample_id, `ManualResult vs Plate1Result`, `ManualResult vs Plate2Result`, `Plate1Result vs Plate2Result`, comment, update_time) VALUES (?,?,?,?,?,?,now()) "
                        + "ON DUPLICATE KEY UPDATE sample_id=?, `ManualResult vs Plate1Result`=?,`ManualResult vs Plate2Result`=?,`Plate1Result vs Plate2Result`=?,comment=?,update_time=now();";
                db.changeSQL(sql);
                db.setString(1, record.getJulien_barcode());

                if (record.getSampID() == 0) {
                    db.setNull(2, java.sql.Types.INTEGER);
                    db.setNull(7, java.sql.Types.INTEGER);
                } else {
                    db.setInt(2, record.getSampID());
                    db.setInt(7, record.getSampID());
                }

                if (record.getMvsP1() == null) {
                    db.setNull(3, java.sql.Types.VARCHAR);
                    db.setNull(8, java.sql.Types.VARCHAR);
                } else {
                    db.setString(3, record.getMvsP1().name());
                    db.setString(8, record.getMvsP1().name());
                }
                if (record.getMvsP2() == null) {
                    db.setNull(4, java.sql.Types.VARCHAR);
                    db.setNull(9, java.sql.Types.VARCHAR);
                } else {
                    db.setString(4, record.getMvsP2().name());
                    db.setString(9, record.getMvsP2().name());
                }
                if (record.getP1vsP2() == null) {
                    db.setNull(5, java.sql.Types.VARCHAR);
                    db.setNull(10, java.sql.Types.VARCHAR);
                } else {
                    db.setString(5, record.getP1vsP2().name());
                    db.setString(10, record.getP1vsP2().name());
                }
                db.setString(6, record.concatComments());
                db.setString(11, record.concatComments());
                result = db.executeUpdate_throwException();
                if (result > 0) {
                    return result;
                }
            }

            return db.executeUpdate_throwException();
        }
    }

    public static int confirmUpdate(AnaRecord record, boolean b) throws SQLException {
        String sql;
        int result = -1;
        try (DataBaseUtility db = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
            for (int fails = 0; fails < MAX_FAIL && result < 0; fails++) {
                sql = "UPDATE `ana_plate_result`.`sample` SET `record status`=? WHERE sample_id=?;";
                db.changeSQL(sql);
                if (b) {
                    db.setString(1, "UP TO DATE");
                } else {
                    db.setString(1, "PENDING");
                }
                db.setString(2, record.getJulien_barcode());

                result = db.executeUpdate_throwException();
                if (result > 0) {
                    return result;
                }

            }
            return result;
        }
    }

    //get auto then compare with mannual
    public static HashMap<String, AnaRecord> history() throws SQLException {
        ArrayList<String> sampleIdPool = new ArrayList<>();
        HashMap<String, AnaRecord> records = new HashMap<>();
        String sql;

        int sampID;
        String julien;
        ANA_Result mannualANA3;
        ANA_Result plate1ANA3;
        ANA_Result plate2ANA3;
        String plate1ID;
        String plate2ID;
        String msg1;
        String msg2;

        String anaResult;
        try (DataBaseUtility db = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
            sql = "SELECT sample_id,plate1_PN,plate2_PN,plate1_id,plate2_id,plate1_error_message,plate2_error_message "
                    + "FROM `ana_plate_result`.`sample` WHERE `record status`='PENDING';"; //SELECT * FROM vibrant_america_information.sample_data where julien_barcode in (1604210027,1604210205,1604210209);
            db.changeSQL(sql);
//            db.setInt(1, 0);
            if (db.generateRecords_throwException()) {
                ResultSet res = db.getRecords();
                while (res.next()) {
                    julien = res.getString("sample_id");
                    anaResult = res.getString("plate1_PN");
                    if (null == anaResult) {
                        plate1ANA3 = null;
                    } else {
                        plate1ANA3 = ANA_Result.valueOf(anaResult);
                    }
                    anaResult = res.getString("plate2_PN");
                    if (null == anaResult) {
                        plate2ANA3 = null;
                    } else {
                        plate2ANA3 = ANA_Result.valueOf(anaResult);
                    }
                    plate1ID = res.getString("plate1_id");
                    plate2ID = res.getString("plate2_id");
                    msg1 = res.getString("plate1_error_message");
                    msg2 = res.getString("plate2_error_message");

                    sampleIdPool.add(julien);
                    records.put(julien, new AnaRecord(julien, plate1ANA3, plate2ANA3, plate1ID, plate2ID, msg1, msg2));
                }
                System.out.println("no of records : " + records.size());
            } else {
                throw new RuntimeException("fail to get records from `ana_plate_result`.`sample`");
            }
        }
        AnaRecord record;
        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.SERVER)) {
            if (sampleIdPool.size() > 0) {
                sql = "SELECT b.julien_barcode,b.sample_id,a.ANA3 "
                        + "FROM `vibrant_america_test_result`.`result_rf_panel` a,`vibrant_america_information`.`sample_data` b "
                        + "WHERE a.sample_id =b.sample_id AND b.julien_barcode in (";//?;
                for (int i = 0; i < sampleIdPool.size(); i++) {
                    sql += "?,";
                }
                sql = sql.substring(0, sql.length() - 1) + ");";
                dbUtility.changeSQL(sql);
                for (int i = 0; i < sampleIdPool.size(); i++) {
                    dbUtility.setString(i + 1, sampleIdPool.get(i));
                }
            } else {
                System.out.println("No pending records, all records are up-to-date.");
                return null;
            }

            if (dbUtility.generateRecords_throwException()) {
                ResultSet res = dbUtility.getRecords();
                while (res.next()) {
                    record = records.get(res.getString("julien_barcode"));
                    sampID = res.getInt("sample_id");
                    if (record != null) {
                        record.setSampID(sampID);
                        mannualANA3 = ANA_Result.get(res.getInt("ANA3"));
                        if (mannualANA3 == null) {
                            record.setMannualANA3(ANA_Result.NO_RESULT);
                            record.addComment(Comment.UNDEFINED_MANNUAL_RESULT);
                        } else {
                            record.setMannualANA3(mannualANA3);
                        }
                    }
                }
            }
        }

        return records;
    }

    public static void queryPlate1PNResult() throws SQLException {
        String logPath = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop\\pn training data.txt";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(logPath));
            String header = "SampleId\tPlateId\tAuto\tManual\tValue\tErrorMessage";
            bw.write(header);
            bw.newLine();
            bw.flush();
            int errid, count = 0;
            String sampleId, errId, msg, line;
            ANA_Result manual;
            String[] ids;
            HashSet<Integer> idsss;
            double plate1sig, negCtrl, value;
            try (DataBaseUtility db = new DataBaseUtility("", PConstants.TSP_SERVER)) {//.LOCAL
                String sql = "SELECT a.sample_id,a.plate1_PN,a.plate1_id,a.plate1_signal,b.plate1_neg_ctrl,a.plate1_error_message, b.error_message "
                        + "FROM `ana_plate_result`.`sample` a, `ana_plate_result`.`ana_plate_type1` b "
                        + "WHERE a.plate1_id=b.plate1_id AND a.`finish_time`>'2016-06-27 00:56:48';"; //SELECT * FROM vibrant_america_information.sample_data where julien_barcode in (1604210027,1604210205,1604210209);
                db.changeSQL(sql);
                if (db.generateRecords_throwException()) {
                    ResultSet res = db.getRecords();
                    while (res.next()) {
                        idsss = new HashSet<>();
                        msg = "";
                        sampleId = res.getString("sample_id");
                        line = res.getString("sample_id") + "\t";
                        line += res.getString("plate1_id") + "\t";
                        line += res.getString("plate1_PN") + "\t";
                        manual = ANA_Result.get(qManualPN(sampleId));

                        line += (manual == null ? "UNKNOWN_RESULT" : manual.name()) + "\t";
                        plate1sig = res.getDouble("plate1_signal");
                        negCtrl = res.getDouble("plate1_neg_ctrl");
                        value = negCtrl == 0 ? -99 : plate1sig / negCtrl;
                        line += value + "\t";
                        errId = res.getString("error_message");
                        if (null != errId && !"".equals(errId)) {
                            ids = errId.split(",");
                        } else {
                            ids = null;
                        }
                        if (null != ids) {
                            for (String str : ids) {
                                try {
                                    errid = Integer.parseInt(str);
                                    if (!idsss.contains(errid)) {
                                        if (!"".equals(msg)) {
                                            msg += ", ";
                                        }
                                        msg += WarningMessage.getNameById(errid);
                                        idsss.add(errid);
                                    }

                                } catch (java.lang.NumberFormatException e) {
                                    System.out.println(sampleId + " " + str);
                                }
                            }
                            line += msg;
                        }

                        errId = res.getString("plate1_error_message");
                        if (null != errId && !"".equals(errId)) {
                            ids = errId.split(",");
                        } else {
                            ids = null;
                        }

                        if (null != ids) {
                            for (String str : ids) {
                                try {
                                    errid = Integer.parseInt(str);
                                    if (!idsss.contains(errid)) {
                                        if (!"".equals(msg)) {
                                            msg += ", ";
                                        }
                                        msg += WarningMessage.getNameById(errid);
                                        idsss.add(errid);
                                    }
                                } catch (java.lang.NumberFormatException e) {
                                    System.out.println(sampleId + " " + str);
                                }
                            }
                            line += msg;
                        }

                        bw.write(line);
                        bw.newLine();
                        bw.flush();
                        count++;

                    }

                    System.out.println("no of records : " + count);
                } else {
                    throw new RuntimeException("fail to get records from `ana_plate_result`.`sample`");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static int qManualPN(String sampleBarcode) throws SQLException {
        //sampleBarcode -> sampID -> result
        int ana3 = -1;
        String sql = "SELECT ANA3 FROM `vibrant_america_test_result`.`result_rf_panel` a, \n"
                + "`vibrant_america_information`.`sample_data` b WHERE  \n"
                + "a.sample_id=b.sample_id AND b.julien_barcode=?";
        try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.SERVER)) {
            dbUtility.changeSQL(sql);
            dbUtility.setString(1, sampleBarcode);
            if (dbUtility.generateRecords_throwException()) {
                ResultSet res = dbUtility.getRecords();
                if (res.next()) {
                    ana3 = res.getInt("ANA3");

                } else {
                    System.out.println(res.first());
                    dbUtility.printSQL();
//                    throw new RuntimeException("No record for sample " + sampleBarcode);
                }
            }
            System.out.println("JulienBarcode = " + sampleBarcode + " ANA3 = " + ana3);
        }
        return ana3;
    }

}
