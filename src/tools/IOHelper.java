package tools;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import constants.PRConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.common.utils.DataBaseUtility;
import main.java.instruments.InstrumentConstants;
import main.java.instruments.InstrumentConstants.ANA_PLATE_TYPE;
import main.java.lis.constant.DiagnosisConstant.ANA_Result;
import main.java.lis.constant.PConstants;
import model.plate.ANAPlate;
import model.plate.ANATestResult;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author mlei
 */
public class IOHelper {

    private static final String IMAGE_FILE_TITLE = "Scanner_TestDevice_Plate_";
    private static final String IMAGE_FILE_CHIP_SECTION = "_Chip_";
//    IMAGE_ROOT_PATH+plateImageFolderName+"\\"+pillarID+"\\"+IMAGE_FILE_TITLE+pillarPlateBarcode+IMAGE_FILE_CHIP_SECTION+pillarID
    private static IOHelper instance;

    private static BufferedWriter out;
    private static final String TIME = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());

    private static final String Tracking_Folder = constants.PRConstants.defaultProperties().getProperty("TRACKING_OUTPUT_FOLDER");
    private static final String ANA_RESULT_FOLDER = constants.PRConstants.defaultProperties().getProperty("PREDICTING_OUTPUT_FOLDER");

    private IOHelper() {

    }

    public static IOHelper getIO() {
        String time = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        if (instance == null || !time.equals(TIME)) {
            instance = new IOHelper();
        }
        return instance;
    }

    // Barcode  Sample Id   file path   comment
    // file path = 
    public void markDown(String barcode, String sampleId, String plateFolder, String pillarId, String comment) {
        String title = "Time\tBarcode\tSample Id\tFile Path\tError Message";
        String line = TIME + "\t" + barcode + "\t" + sampleId + "\t" + plateFolder + PRConstants.FILE_SEPARATOR + pillarId + "\t" + comment;
        File log = new File(Tracking_Folder);
        if (!log.exists()) {
            log.mkdirs();
        }
        log = new File(Tracking_Folder, "按图索骥.txt");
        BufferedWriter bw = null;
        try {
            bw = getWriter(log, true);
            bw.write(line);
            bw.newLine();
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    Logger.getLogger(Predictor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public void log(String err_msg) {
        File log = new File(ANA_RESULT_FOLDER, "error logs");
        if (!log.exists()) {
            log.mkdirs();
        }
        log = new File(log, TIME);
        BufferedWriter bw = null;
        try {
            bw = getWriter(log, true);
            bw.write(err_msg);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void log(String pillarPlateID, String sampleBarcode, String pillarId, String comment) {
        String line = TIME + "\t" + sampleBarcode + "\t" + pillarPlateID + "\t" + pillarId + "\t" + comment;
        log(line);
    }

//    public static boolean write2Workbook(HashMap<String, AnaRecord> records) throws IOException {
//        File outputFolder = new File(ANA_RESULT_FOLDER);
//        if (!outputFolder.exists()) {
//            outputFolder.mkdirs();
//        }
//        File outputFile = new File(outputFolder, TIME + ".xlsx");
//        Workbook excelFile = null;
//        if (outputFile.exists()) {
//            try {
////                OPCPackage pkg = OPCPackage.open(outputFile);
////                excelFile = new XSSFWorkbook(pkg);
//                excelFile = WorkbookFactory.create(outputFile);
//            } catch (EncryptedDocumentException ex) {
//                System.out.println("file with assigned name already exists but is encrypted...");
//                Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InvalidFormatException ex) {
//                Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            outputFile.createNewFile();
//            excelFile = new XSSFWorkbook();
//        }
//        if (excelFile == null) {
//            throw new RuntimeException("fail to create the xlsx file");
//        }
//
//        summarySheet(records, excelFile);
////        compareSheet(records, excelFile);
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(outputFile.getAbsolutePath());//,true
//            excelFile.write(fos);
//            return true;
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//        return false;
//
//    }
// plate error message
//    public static void summarySheet(HashMap<String, AnaRecord> records, Workbook excelFile) {
//        int rowCount = records.size();
//        //get instance of Workbook (add by sheet write by workbook/file)
//
//        String sheetName = "SUMMARY (" + rowCount + ")";
//        //create a working sheet 
//        Sheet sheet = excelFile.createSheet(sheetName);
//        //starting row & col
//        int rowIndex = 0;
//        int colIndex = 0;
//        int totalCol = 0;
//        XSSFFont fontTitle = (XSSFFont) excelFile.createFont();
//        fontTitle.setFontHeightInPoints((short) 10);
//        fontTitle.setFontName("Arial");
//        fontTitle.setColor(IndexedColors.GREEN.getIndex());
//        fontTitle.setBold(true);
//        fontTitle.setItalic(false);
//        XSSFCellStyle styleTitle = (XSSFCellStyle) excelFile.createCellStyle();
//        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
//        styleTitle.setFont(fontTitle);
//        Cell cell0 = sheet.createRow(rowIndex++).createCell(0);
//        cell0.setCellValue("Summary");
//        cell0.setCellStyle(styleTitle);
//
////        //optional set Cell Style
////        CellStyle styleTitle = null;
////        CellStyle style = null;
//        Row row = sheet.createRow(rowIndex++);
//        Cell column = row.createCell(colIndex++);
//        column.setCellValue("Sample ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Mannual Result");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Auto Result : Plate 1");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Auto Result : Plate 2");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 1 ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 2 ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 1 Warning Message");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 2 Warning Message");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Comments");
//        totalCol++;
//
//        for (AnaRecord record : records.values()) {
////            System.out.println(record);
//            row = sheet.createRow(rowIndex++);
//            colIndex = 0;
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getSampID());
//            column = row.createCell(colIndex++);
//            if (record.getMannualANA3() != null) {
//                column.setCellValue(record.getMannualANA3().name());
//            } else {
//                record.addComment(AnaRecord.Comment.NO_MANNUAL_RESULT);
//            }
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate1ANA3().name());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate2ANA3().name());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate1ID());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate2ID());
//            column = row.createCell(colIndex++);
////            column.setCellValue(record.getMsg1());
//            column = row.createCell(colIndex++);
////            column.setCellValue(record.getMsg2());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.concatComments());
////            column.setCellValue(record.getComment());
//
//        }
//        if (rowIndex - rowCount == 2) {
//
//            for (int i = 0; i < totalCol; i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//        } else {
//            System.out.println((rowIndex - 2) + " records are writen into file while " + rowCount + " are expected");
//        }
//    }
//    public static void spotBias(String anaRoot) {
//        String key;
//        HashMap<String, String> folderMap = new HashMap<>();
//        for (File f : new File(anaRoot).listFiles()) {
//            key = f.getName().split("_")[0];
//            folderMap.put(key, f.getAbsolutePath());
//        }
//        BufferedWriter bw = null;
//        try {
//            //sid, manualPN, plate1PN, signal, negCtrl, path
//            // local path-map plateId:plateFolder
//            //
//
//            bw = getWriter(new File(ANA_RESULT_FOLDER, "Naughty 1st Plates.txt"), true);
//            if (bw == null) {
//                System.out.println("fail to get writer");
//                return;
//            }
//            String path, line, ananame;
//            ANA_Result anares;
//            line = "Sample_id\tPlate1 Id\tManual Result\tPlate1 Result\tPlate1 Signal\tPlate NegCtrl\tPath";
//            bw.write(line);
//            bw.flush();
//            bw.newLine();
//            String sql = "SELECT a.sample_id, a.plate1_PN, a.plate1_signal, a.plate1_id,a.plate1_pillar_position, b.plate1_neg_ctrl FROM ana_plate_result.sample a, ana_plate_result.ana_plate_type1 b, ana_plate_result.ana_record_status c"
//                    + " WHERE c.`ManualResult vs Plate1Result`='NONIDENTICAL' AND a.sample_id=c.julien_barcode AND b.plate1_id=a.plate1_id AND c.comment=''";
//            try (DataBaseUtility dbUtility = new DataBaseUtility("", PConstants.TSP_SERVER)) {
//                dbUtility.changeSQL(sql);
//                if (dbUtility.generateRecords_throwException()) {
//                    ResultSet res = dbUtility.getRecords();
//                    while (res.next()) {
//                        line = res.getString("sample_id") + "\t";
//                        line += res.getString("plate1_id") + "\t";
//                        ananame = res.getString("plate1_PN");
//                        anares = ANA_Result.valueOf(ananame);
//                        if (anares == null) {
//                            System.out.println(res.getString("plate1_id") + "\t" + res.getString("sample_id") + "\t" + ananame);
//                            continue;
//                        } else if (anares.equals(ANA_Result.NEGATIVE)) {
//                            line += ANA_Result.POSITIVE.name() + "\t";
//                            line += ANA_Result.NEGATIVE.name() + "\t";
//                        } else if (anares.equals(ANA_Result.POSITIVE)) {
//                            line += ANA_Result.NEGATIVE.name() + "\t";
//                            line += ANA_Result.POSITIVE.name() + "\t";
//                        } else {
//                            System.out.println("PN " + ananame);
//                        }
//                        line += res.getString("plate1_signal") + "\t";
//                        line += res.getString("plate1_neg_ctrl") + "\t";
//                        path = folderMap.get(res.getString("plate1_id"));
//                        if (path == null) {
//                            System.out.println("file not found for plate " + res.getString("plate1_id"));
//                            line += "没找到";
//                        } else {
//                            line += path + "\\" + res.getString("plate1_pillar_position");//+pillar
//                        }
//                        System.out.println("line " + line);
//                        bw.write(line);
//                        bw.flush();
//                        bw.newLine();
//                    }
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (null != bw) {
//                try {
//                    bw.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//
//    }
    public static void generateAnaPositivityResult(ANAPlate plate) throws IOException {
        generateAnaPositivityResult(plate, ANA_RESULT_FOLDER);
    }

    public static void generateAnaPositivityResult(ANAPlate plate, String outputFolderPath) throws IOException {
        File outputFolder = new File(outputFolderPath, "Positivity");
        if (plate.getType().equals(ANA_PLATE_TYPE.TYPE_1)) {
            outputFolder = new File(outputFolder, "plate 1");
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            plate1ResultSheet(plate, outputFolder);
        } else {
            outputFolder = new File(outputFolder, "plate 2");
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }
            plate2ResultSheet(plate, outputFolder);
        }

    }

    public static void plate1ResultSheet(ANAPlate plate, File outputFolder) throws IOException {

        File outputFile = new File(outputFolder, plate.getPlateId() + "_" + TIME + ".xlsx");
        Workbook excelFile = null;
//        if (outputFile.exists()) {
//            try {
//                excelFile = WorkbookFactory.create(outputFile);
//            } catch (EncryptedDocumentException ex) {
//                System.out.println("file with assigned name already exists but is encrypted...");
//                Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (InvalidFormatException ex) {
//                Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            outputFile.createNewFile();
//            excelFile = new XSSFWorkbook();
//        }
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();
            excelFile = new XSSFWorkbook();
        if (excelFile == null) {
            throw new RuntimeException("fail to create the xlsx file");
        }
        int rowCount = plate.getSampleNumber();   //not including the 2 control samples + "_"+time
        String sheetName = plate.getPlateId();
        //create a working sheet 
        Sheet sheet = excelFile.createSheet(sheetName);
        //starting row & col
        int rowIndex = 0;
        int colIndex = 0;
        int totalCol = 0;
        int pos = 0, neg = 0, all = 0;
        XSSFFont fontTitle = (XSSFFont) excelFile.createFont();
        fontTitle.setFontHeightInPoints((short) 10);
        fontTitle.setFontName("Arial");
        fontTitle.setColor(IndexedColors.GREEN.getIndex());
        fontTitle.setBold(true);
        fontTitle.setItalic(false);
        XSSFCellStyle styleTitle = (XSSFCellStyle) excelFile.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        styleTitle.setFont(fontTitle);
        Cell cell0 = sheet.createRow(rowIndex++).createCell(0);
        cell0.setCellValue(plate.getPlateId() + " Summary");    //title
        cell0.setCellStyle(styleTitle);

//        //optional set Cell Style
//        CellStyle styleTitle = null;
//        CellStyle style = null;
        Row row = sheet.createRow(rowIndex++);  //names
        Cell column = row.createCell(colIndex++);
        column.setCellValue("Sample ID");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Chip Location");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Result");
        totalCol++;
        
//        column = row.createCell(colIndex++);
//        column.setCellValue("Positivity 0.3P");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Positivity0.275P+0.5N");
//        totalCol++;
        
        
        column = row.createCell(colIndex++);
        column.setCellValue("Signal");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Comments");
        totalCol++;

        for (ANATestResult result : plate.getTestResultList()) {
            row = sheet.createRow(rowIndex++);  //data
            colIndex = 0;
            column = row.createCell(colIndex++);
            column.setCellValue(result.getJulien_barcode());
            column = row.createCell(colIndex++);
            column.setCellValue(result.getPillarPosition());
            column = row.createCell(colIndex++);
            if (result.getPositivity() == null) {
                column.setCellValue("Null Result");
            } else {
                if (ANA_Result.POSITIVE.equals(result.getPositivity())) {
                    pos++;
                } else if (ANA_Result.NEGATIVE.equals(result.getPositivity())) {
                    neg++;
                }
                all++;
                column.setCellValue(result.getPositivity().name());
            }  //make sure all fis has not-null pn result
            
//            column = row.createCell(colIndex++);
//            if (result.positivity30 == null) {
//                column.setCellValue("Null Result");
//            } else {
//                if (ANA_Result.POSITIVE.equals(result.positivity30)) {
////                    pos++;
//                } else if (ANA_Result.NEGATIVE.equals(result.positivity30)) {
////                    neg++;
//                }
////                all++;
//                column.setCellValue(result.positivity30.name());
//            }  //make sure all fis has not-null pn result
//            column = row.createCell(colIndex++);
//            if (result.positivityCombined == null) {
//                column.setCellValue("Null Result");
//            } else {
//                if (ANA_Result.POSITIVE.equals(result.positivityCombined)) {
////                    pos++;
//                } else if (ANA_Result.NEGATIVE.equals(result.positivityCombined)) {
////                    neg++;
//                }
////                all++;
//                column.setCellValue(result.positivityCombined.name());
//            }  //make sure all fis has not-null pn result
            
            
            column = row.createCell(colIndex++);
            column.setCellValue(result.getFirstPlateSignal());
//            if(result.getFirstPlateSignal()<0){
//                column.setCellValue("ROI exception: unable to get signal for this sample");
//            }else{
//                column.setCellValue(result.getFirstPlateSignal());
//            }
            column = row.createCell(colIndex++);    //warning msg concat mthd; merge plateErr to sampErr
            column.setCellValue(result.concatWarningMsgs());
        }
        if (rowIndex - rowCount == 2) {
            for (int i = 0; i < totalCol; i++) {
                sheet.autoSizeColumn(i);
            }
        } else {
            System.out.println((rowIndex - 2) + " records are writen into file while " + rowCount + " are expected");
        }
        row = sheet.createRow(rowIndex++);  //total
        colIndex = 0;
        column = row.createCell(colIndex++);
        column.setCellValue("all samples");
        column = row.createCell(colIndex++);
        column.setCellValue(all);
        column = row.createCell(colIndex++);
        column.setCellValue("positive samples");
        column = row.createCell(colIndex++);
        column.setCellValue(pos);
        column = row.createCell(colIndex++);
        column.setCellValue("negative samples");
        column = row.createCell(colIndex++);
        column.setCellValue(neg);

        column = row.createCell(colIndex++);
        column = row.createCell(colIndex++);
        column = row.createCell(colIndex++);
        column.setCellValue("PosCtrl");
        column = row.createCell(colIndex++);
        column.setCellValue(plate.getPosCtrl().getFirstPlateSignal());
        column = row.createCell(colIndex++);
        column.setCellValue("NegCtrl");
        column = row.createCell(colIndex++);
        column.setCellValue(plate.getNegCtrlSignal());

        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile.getAbsolutePath());//,true
            excelFile.write(fos);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public static void plate2ResultSheet(ANAPlate plate, File outputFolder) throws IOException {

        File outputFile = new File(outputFolder, plate.getPlateId() + "_" + TIME + ".xlsx");
        Workbook excelFile = null;
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();
            excelFile = new XSSFWorkbook();
        if (excelFile == null) {
            throw new RuntimeException("fail to create the xlsx file");
        }
        int rowCount = plate.getSampleNumber();   //not including the 2 control samples + "_"+time
        String sheetName = plate.getPlateId();
        //create a working sheet 
        Sheet sheet = excelFile.createSheet(sheetName);
        //starting row & col
        int rowIndex = 0;
        int colIndex = 0;
        int totalCol = 0;
        int pos = 0, neg = 0, all = 0;
        XSSFFont fontTitle = (XSSFFont) excelFile.createFont();
        fontTitle.setFontHeightInPoints((short) 10);
        fontTitle.setFontName("Arial");
        fontTitle.setColor(IndexedColors.GREEN.getIndex());
        fontTitle.setBold(true);
        fontTitle.setItalic(false);
        XSSFCellStyle styleTitle = (XSSFCellStyle) excelFile.createCellStyle();
        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        styleTitle.setFont(fontTitle);
        Cell cell0 = sheet.createRow(rowIndex++).createCell(0);
        cell0.setCellValue(plate.getPlateId() + " Summary");    //title
        cell0.setCellStyle(styleTitle);

//        //optional set Cell Style
//        CellStyle styleTitle = null;
//        CellStyle style = null;

//            add column:Sample,Chip Location,Signal,Positivity,sample titer,plate titer, pattern, No of Cells, enableWatershed,comment
        Row row = sheet.createRow(rowIndex++);  //names
        Cell column = row.createCell(colIndex++);
        column.setCellValue("Sample ID");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Chip Location");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Signal");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Positivity");
        totalCol++;
        
        column = row.createCell(colIndex++);
        column.setCellValue("Sample Titer");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Plate Titer");
        totalCol++;
        
        column = row.createCell(colIndex++);
        column.setCellValue("Pattern");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Number of Cells");
        totalCol++;
        column = row.createCell(colIndex++);
        column.setCellValue("Comments");
        totalCol++;

        for (ANATestResult result : plate.getTestResultList()) {
            row = sheet.createRow(rowIndex++);  //data
            colIndex = 0;
            column = row.createCell(colIndex++);
            column.setCellValue(result.getJulien_barcode());
            column = row.createCell(colIndex++);
            column.setCellValue(result.getPillarPosition());
            column = row.createCell(colIndex++);
            column.setCellValue(result.getSecondPlateSignal());
            column = row.createCell(colIndex++);             
            if (result.getPositivity() == null) {
                column.setCellValue("Null Result");
            } else {
                if (ANA_Result.POSITIVE.equals(result.getPositivity())) {
                    pos++;
                } else if (ANA_Result.NEGATIVE.equals(result.getPositivity())) {
                    neg++;
                }
                all++;
                column.setCellValue(result.getPositivity().name());
            }  //make sure all fis has not-null pn result
            
            
            
            
            column = row.createCell(colIndex++);
            if(result.getTiter()!=null){
                column.setCellValue(result.getTiter().name());
            }
            column = row.createCell(colIndex++);
            if(plate.getPosCtrl().getTiter()!=null){
                column.setCellValue(plate.getPosCtrl().getTiter().name());
            }
            //pattern, No of Cells, enableWatershed,comment
            column = row.createCell(colIndex++);
            if(result.getPattern()!=null){
                column.setCellValue(result.getPattern().name());
            }
            column = row.createCell(colIndex++);
            column.setCellValue(result.cellCount());
            column = row.createCell(colIndex++);    //warning msg concat mthd; merge plateErr to sampErr
            column.setCellValue(result.concatWarningMsgs());
        }
        if (rowIndex - rowCount == 2) {
            for (int i = 0; i < totalCol; i++) {
                sheet.autoSizeColumn(i);
            }
        } else {
            System.out.println((rowIndex - 2) + " records are writen into file while " + rowCount + " are expected");
        }
        row = sheet.createRow(rowIndex++);  //total
        colIndex = 0;
        column = row.createCell(colIndex++);
        column.setCellValue("all samples");
        column = row.createCell(colIndex++);
        column.setCellValue(all);
        column = row.createCell(colIndex++);
        column.setCellValue("positive samples");
        column = row.createCell(colIndex++);
        column.setCellValue(pos);
        column = row.createCell(colIndex++);
        column.setCellValue("negative samples");
        column = row.createCell(colIndex++);
        column.setCellValue(neg);
        
        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile.getAbsolutePath());//,true
            excelFile.write(fos);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(IOHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public BufferedWriter getWriter(String resultPath) throws IOException {
        if (out != null) {
            out.close();
        }
        out = new BufferedWriter(new FileWriter(resultPath));

        return out;
    }

    public static BufferedWriter getWriter(File logPath, boolean append) throws IOException {
        if (out != null) {
            out.close();
        }
        out = new BufferedWriter(new FileWriter(logPath, append));

        return out;
    }

    public String getTime() {
        return TIME;
    }

    public static String getTracking_Folder() {
        return Tracking_Folder;
    }

    public static String getDiagnose_Folder() {
        return ANA_RESULT_FOLDER;
    }

}
//    private static void compareSheet(HashMap<Integer, AnaRecord> records, Workbook excelFile) {
//        int rowCount = 0;
//        //get instance of Workbook (add by sheet write by workbook/file)
//
//        String sheetName = "INCONSISTANT RECORDS (" + rowCount + ")";
//        //create a working sheet 
//        Sheet sheet = excelFile.createSheet(sheetName);
//        //starting row & col
//        int rowIndex = 0;
//        int colIndex = 0;
//        int totalCol = 0;
//        XSSFFont fontTitle = (XSSFFont) excelFile.createFont();
//        fontTitle.setFontHeightInPoints((short) 10);
//        fontTitle.setFontName("Arial");
//        fontTitle.setColor(IndexedColors.GREEN.getIndex());
//        fontTitle.setBold(true);
//        fontTitle.setItalic(false);
//        XSSFCellStyle styleTitle = (XSSFCellStyle) excelFile.createCellStyle();
//        styleTitle.setAlignment(CellStyle.ALIGN_CENTER);
//        styleTitle.setFont(fontTitle);
//        Cell cell0 = sheet.createRow(rowIndex++).createCell(0);
//        cell0.setCellValue("Inconsistant Records");
//        cell0.setCellStyle(styleTitle);
//
////        //optional set Cell Style
////        CellStyle styleTitle = null;
////        CellStyle style = null;
//        Row row = sheet.createRow(rowIndex++);
//        Cell column = row.createCell(colIndex++);
//        column.setCellValue("Sample ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Mannual Result");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Auto Result : Plate 1");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Auto Result : Plate 2");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 1 ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 2 ID");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 1 Warning Message");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Plate 2 Warning Message");
//        totalCol++;
//        column = row.createCell(colIndex++);
//        column.setCellValue("Comments");
//        totalCol++;
//
//        for (AnaRecord record : records.values()) {
//            if (!record.initAccuracyAndConsistency()) {
//                continue;
//            }
//
//            rowCount++;
//            row = sheet.createRow(rowIndex++);
//            colIndex = 0;
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getSampID());
//            column = row.createCell(colIndex++);
//            if (record.getMannualANA3() != null) {
//                column.setCellValue(record.getMannualANA3().name());
//            } else {
//                record.setComment(AnaRecord.UNKNOWN_MANNUAL_RESULT);
//            }
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate1ANA3().name());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate2ANA3().name());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate1ID());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getPlate2ID());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getMsg1());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getMsg2());
//            column = row.createCell(colIndex++);
//            column.setCellValue(record.getComment());
//        }
//
//        for (int i = 0; i < totalCol; i++) {
//            sheet.autoSizeColumn(i);
//        }
//    }
