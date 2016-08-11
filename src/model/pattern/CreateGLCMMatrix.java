/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import static java.lang.Math.log10;
import java.util.ArrayList;
import main.java.com.imagemodel.ROIClass;

/**
 *
 * @author Vibrant Sciences
 */
public class CreateGLCMMatrix {

    private MatrixForEachCell matrixForEachCell;
    private ROIClass roi;
    private int[][] cellMatrixFromImage;

    public CreateGLCMMatrix(ROIClass roi) {
        this.roi = roi;
        this.matrixForEachCell=new MatrixForEachCell(roi);
        this.cellMatrixFromImage=this.matrixForEachCell.ConvertToMatrix(binCount);
    }
    
    

    private ArrayList<Integer> distance = new ArrayList<>();

    private int binCount = 256;
    
    

    public ArrayList<Double> caiCalc(ROIClass roi){
        distance.add(4);
        //distance.add(6);
        distance.add(8);
        matrixForEachCell = new MatrixForEachCell(roi);
        ArrayList<Double> arr_oneCell = new ArrayList<>();

            for (int j = 0; j < distance.size(); j++) {
                double secondMoment = 0;
                double contrast = 0;
                //double correlation = 0;
                double entropy = 0;
                double homogenity = 0;

                ArrayList<Double> arr_forRangeComaprisonSeconMoment = new ArrayList<>();
                ArrayList<Double> arr_forRangeComparisonContrast = new ArrayList<>();
                //ArrayList<Double> arr_forRangeComparisonCorrelation = new ArrayList<>();
                ArrayList<Double> arr_forRangeComparisonEntropy = new ArrayList<>();
                ArrayList<Double> arr_forRangeComparisonHomogenity = new ArrayList<>();
                //0 degree
                double[][] GLCMMatrix_0_degree = GLCMMatrix_0_degree(roi, distance.get(j));
                double secondMoment_0_degree = getSecondMoment(GLCMMatrix_0_degree);
                double contrast_0_degree = getContrast(GLCMMatrix_0_degree);
                //double correlation_0_degree = getCorrelation(GLCMMatrix_0_degree);
                double entropy_0_degree = getEntropy(GLCMMatrix_0_degree);
                double homogenity_0_degree = getHomogenity(GLCMMatrix_0_degree);

                secondMoment += secondMoment_0_degree;
                contrast += contrast_0_degree;
                //correlation += correlation_0_degree;
                entropy += entropy_0_degree;
                homogenity += homogenity_0_degree;

                arr_forRangeComaprisonSeconMoment.add(secondMoment);
                arr_forRangeComparisonContrast.add(contrast);
                //arr_forRangeComparisonCorrelation.add(correlation);
                arr_forRangeComparisonEntropy.add(entropy);
                arr_forRangeComparisonHomogenity.add(homogenity);

                //45 degree
                double[][] GLCMMatrix_45_degree = GLCMMatrix_45_degree(roi, distance.get(j));
                double secondMoment_45_degree = getSecondMoment(GLCMMatrix_45_degree);
                double contrast_45_degree = getContrast(GLCMMatrix_45_degree);
                //double correlation_45_degree = getCorrelation(GLCMMatrix_45_degree);
                double entropy_45_degree = getEntropy(GLCMMatrix_45_degree);
                double homogenity_45_degree = getHomogenity(GLCMMatrix_45_degree);

                secondMoment += secondMoment_45_degree;
                contrast += contrast_45_degree;
                //correlation += correlation_45_degree;
                entropy += entropy_45_degree;
                homogenity += homogenity_45_degree;

                arr_forRangeComaprisonSeconMoment.add(secondMoment);
                arr_forRangeComparisonContrast.add(contrast);
                //arr_forRangeComparisonCorrelation.add(correlation);
                arr_forRangeComparisonEntropy.add(entropy);
                arr_forRangeComparisonHomogenity.add(homogenity);

                //90 degree
                double[][] GLCMMatrix_90_degree = GLCMMatrix_90_degree(roi, distance.get(j));
                double secondMoment_90_degree = getSecondMoment(GLCMMatrix_90_degree);
                double contrast_90_degree = getContrast(GLCMMatrix_90_degree);
                //double correlation_90_degree = getCorrelation(GLCMMatrix_90_degree);
                double entropy_90_degree = getEntropy(GLCMMatrix_90_degree);
                double homogenity_90_degree = getHomogenity(GLCMMatrix_90_degree);

                secondMoment += secondMoment_90_degree;
                contrast += contrast_90_degree;
                //correlation += correlation_90_degree;
                entropy += entropy_90_degree;
                homogenity += homogenity_90_degree;

                arr_forRangeComaprisonSeconMoment.add(secondMoment);
                arr_forRangeComparisonContrast.add(contrast);
                //arr_forRangeComparisonCorrelation.add(correlation);
                arr_forRangeComparisonEntropy.add(entropy);
                arr_forRangeComparisonHomogenity.add(homogenity);

                //135 degree
                double[][] GLCMMatrix_135_degree = GLCMMatrix_135_degree(roi, distance.get(j));
                double secondMoment_135_degree = getSecondMoment(GLCMMatrix_135_degree);
                double contrast_135_degree = getContrast(GLCMMatrix_135_degree);
                //double correlation_135_degree = getCorrelation(GLCMMatrix_135_degree);
                double entropy_135_degree = getEntropy(GLCMMatrix_135_degree);
                double homogenity_135_degree = getHomogenity(GLCMMatrix_135_degree);

                secondMoment += secondMoment_135_degree;
                contrast += contrast_135_degree;
                //correlation += correlation_135_degree;
                entropy += entropy_135_degree;
                homogenity += homogenity_135_degree;

                arr_forRangeComaprisonSeconMoment.add(secondMoment);
                arr_forRangeComparisonContrast.add(contrast);
                //arr_forRangeComparisonCorrelation.add(correlation);
                arr_forRangeComparisonEntropy.add(entropy);
                arr_forRangeComparisonHomogenity.add(homogenity);

                secondMoment = secondMoment / 4;
                contrast = contrast / 4;
//                correlation = correlation / 4;
                entropy = entropy / 4;
                homogenity = homogenity / 4;

                double maxSecondMoment = arr_forRangeComaprisonSeconMoment.get(0);
                double maxContrast = arr_forRangeComparisonContrast.get(0);
                //double maxCorrelation = arr_forRangeComparisonCorrelation.get(0);
                double maxEntropy = arr_forRangeComparisonEntropy.get(0);
                double maxHomogenity = arr_forRangeComparisonHomogenity.get(0);

                double minSecondMoment = arr_forRangeComaprisonSeconMoment.get(0);
                double minContrast = arr_forRangeComparisonContrast.get(0);
                //double minCorrelation = arr_forRangeComparisonCorrelation.get(0);
                double minEntropy = arr_forRangeComparisonEntropy.get(0);
                double minHomogenity = arr_forRangeComparisonHomogenity.get(0);

                for (int k = 0; k < arr_forRangeComaprisonSeconMoment.size() - 1; k++) {
                    if (minSecondMoment > arr_forRangeComaprisonSeconMoment.get(k + 1)) {
                        minSecondMoment = arr_forRangeComaprisonSeconMoment.get(k + 1);
                    }
                    if (minContrast > arr_forRangeComparisonContrast.get(k + 1)) {
                        minContrast = arr_forRangeComparisonContrast.get(k + 1);
                    }
//                    if (minCorrelation > arr_forRangeComparisonCorrelation.get(k + 1)) {
//                        minCorrelation = arr_forRangeComparisonCorrelation.get(k + 1);
//                    }
                    if (minEntropy > arr_forRangeComparisonEntropy.get(k + 1)) {
                        minEntropy = arr_forRangeComparisonEntropy.get(k + 1);
                    }
                    if (minHomogenity > arr_forRangeComparisonHomogenity.get(k + 1)) {
                        minHomogenity = arr_forRangeComparisonHomogenity.get(k + 1);
                    }

                }

                for (int k = 0; k < arr_forRangeComaprisonSeconMoment.size() - 1; k++) {
                    if (maxSecondMoment < arr_forRangeComaprisonSeconMoment.get(k + 1)) {
                        maxSecondMoment = arr_forRangeComaprisonSeconMoment.get(k + 1);
                    }
                    if (maxContrast < arr_forRangeComparisonContrast.get(k + 1)) {
                        maxContrast = arr_forRangeComparisonContrast.get(k + 1);
                    }
//                    if (maxCorrelation < arr_forRangeComparisonCorrelation.get(k + 1)) {
//                        maxCorrelation = arr_forRangeComparisonCorrelation.get(k + 1);
//                    }
                    if (maxEntropy < arr_forRangeComparisonEntropy.get(k + 1)) {
                        maxEntropy = arr_forRangeComparisonEntropy.get(k + 1);
                    }
                    if (maxHomogenity < arr_forRangeComparisonHomogenity.get(k + 1)) {
                        maxHomogenity = arr_forRangeComparisonHomogenity.get(k + 1);
                    }

                }

                double rangeSecondMoment = maxSecondMoment - minSecondMoment;
                double rangeContrast = maxContrast - minContrast;
                //double rangeCorrelation = maxCorrelation - minCorrelation;
                double rangeEntropy = maxEntropy - minEntropy;
                double rangeHomogenity = maxHomogenity - minHomogenity;

                arr_oneCell.add(secondMoment);
                arr_oneCell.add(contrast);
                //arr_oneCell.add(correlation);
                arr_oneCell.add(entropy);
                arr_oneCell.add(homogenity);
                arr_oneCell.add(rangeSecondMoment);
                arr_oneCell.add(rangeContrast);
                //arr_oneCell.add(rangeCorrelation);
                arr_oneCell.add(rangeEntropy);
                arr_oneCell.add(rangeHomogenity);

                //System.out.print(secondMoment+":"+contrast+":"+correlation+":"+entropy+homogenity+":"+rangeSecondMoment+":"+rangeContrast+":"+rangeCorrelation+":"+rangeEntropy+":"+rangeHomogenity);
            }
            return arr_oneCell;
    }

    public double[][] GLCMMatrix_0_degree(ROIClass roiClass, int distance) {//0 degree

        double[][] GLCMMatrix_0_degree = new double[binCount][binCount];

        for (int i = 0; i < binCount; i++) {
            for (int j = 0; j < binCount; j++) {
                GLCMMatrix_0_degree[i][j] = 0.0;
            }
        }
//        int rows = matrix.length;
//        int cols = matrix[0].length;
        for (int i = 0; i < cellMatrixFromImage.length; i++) {//count occurance numbers

            for (int j = 0; j < cellMatrixFromImage[0].length; j++) {

                if (j + distance < cellMatrixFromImage[i].length && cellMatrixFromImage[i][j] != -1 && cellMatrixFromImage[i][j + distance] != -1) {

                    GLCMMatrix_0_degree[cellMatrixFromImage[i][j]][cellMatrixFromImage[i][j + distance]] += 1;

                }
            }
        }

        int sum = 0;
        for (int i = 0; i < GLCMMatrix_0_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_0_degree[0].length; j++) {
                sum += GLCMMatrix_0_degree[i][j];
            }
        }

        for (int i = 0; i < GLCMMatrix_0_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_0_degree[0].length; j++) {
                GLCMMatrix_0_degree[i][j] /= sum;
            }
        }

//        for (int i = 0; i < GLCMMatrix_0_degree.length; i++) {
//                for (int j = 0; j < GLCMMatrix_0_degree[0].length; j++) {
//                    System.out.print(GLCMMatrix_0_degree[i][j]+"\t");
//                }
//                System.out.println();
//            }
        return GLCMMatrix_0_degree;
    }

    public double[][] GLCMMatrix_45_degree(ROIClass roiClass, int distance) {//45 degree
//        int maxGreyTone = this.matrixForEachCell.TransferGrayTone(roiClass, roiClass.getMaxSignalList().get(1));
        double[][] GLCMMatrix_45_degree = new double[binCount][binCount];
        for (int i = 0; i < binCount; i++) {
            for (int j = 0; j < binCount; j++) {
                GLCMMatrix_45_degree[i][j] = 0.0;
            }
        }
        for (int i = 0; i < cellMatrixFromImage.length; i++) {//count occurance numbers

            for (int j = 0; j < cellMatrixFromImage[0].length; j++) {
                if (i - distance >= 0 && j + distance < cellMatrixFromImage[i].length && cellMatrixFromImage[i][j] != -1 && cellMatrixFromImage[i - distance][j + distance] != -1) {

                    GLCMMatrix_45_degree[cellMatrixFromImage[i][j]][cellMatrixFromImage[i - distance][j + distance]] += 1;
                }

            }
        }
        int sum = 0;
        for (int i = 0; i < GLCMMatrix_45_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_45_degree[0].length; j++) {
                sum += GLCMMatrix_45_degree[i][j];
            }
        }

        for (int i = 0; i < GLCMMatrix_45_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_45_degree[0].length; j++) {
                GLCMMatrix_45_degree[i][j] /= sum;
            }
        }
//for (int i = 0; i < GLCMMatrix_45_degree.length; i++) {
//                for (int j = 0; j < GLCMMatrix_45_degree[0].length; j++) {
//                    System.out.print(GLCMMatrix_45_degree[i][j]+"\t");
//                }
//                System.out.println();
//            }
        return GLCMMatrix_45_degree;
    }

    public double[][] GLCMMatrix_90_degree(ROIClass roiClass, int distance) {//90 degree
//        int maxGreyTone = this.matrixForEachCell.TransferGrayTone(roiClass, roiClass.getMaxSignalList().get(1));
        double[][] GLCMMatrix_90_degree = new double[binCount][binCount];
        for (int i = 0; i < binCount; i++) {
            for (int j = 0; j < binCount; j++) {
                GLCMMatrix_90_degree[i][j] = 0.0;
            }
        }
        for (int i = 0; i < cellMatrixFromImage.length; i++) {//count occurance numbers

            for (int j = 0; j < cellMatrixFromImage[0].length; j++) {
                if (i - distance >= 0 && cellMatrixFromImage[i][j] != -1 && cellMatrixFromImage[i - distance][j] != -1) {
                    GLCMMatrix_90_degree[cellMatrixFromImage[i][j]][cellMatrixFromImage[i - distance][j]] += 1;
                }
            }
        }
        int sum = 0;
        for (int i = 0; i < GLCMMatrix_90_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_90_degree[0].length; j++) {
                sum += GLCMMatrix_90_degree[i][j];
            }
        }

        for (int i = 0; i < GLCMMatrix_90_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_90_degree[0].length; j++) {
                GLCMMatrix_90_degree[i][j] /= sum;
            }
        }
//for (int i = 0; i < GLCMMatrix_90_degree.length; i++) {
//                for (int j = 0; j < GLCMMatrix_90_degree[0].length; j++) {
//                    System.out.print(GLCMMatrix_90_degree[i][j]+"\t");
//                }
//                System.out.println();
//            }
        return GLCMMatrix_90_degree;
    }

    public double[][] GLCMMatrix_135_degree(ROIClass roiClass, int distance) {//135 degree

        double[][] GLCMMatrix_135_degree = new double[binCount][binCount];
        for (int i = 0; i < binCount; i++) {
            for (int j = 0; j < binCount; j++) {
                GLCMMatrix_135_degree[i][j] = 0.0;
            }
        }

//        if (roiClass.getRoiPixels().size() == 326 && roiClass.getCenterX() == 14) {
//            for (int i = 0; i < cellMatrixFromImage.length; i++) {
//                for (int j = 0; j < cellMatrixFromImage[0].length; j++) {
//                    System.out.print(cellMatrixFromImage[i][j] + "\t");
//                }
//                System.out.println();
//            }
//        }
        for (int i = 0; i < cellMatrixFromImage.length; i++) {//count occurance numbers
            for (int j = 0; j < cellMatrixFromImage[0].length; j++) {

//                if (i - distance >= 0 && j - distance >= 0 && cellMatrixFromImage[i][j] != -1 && cellMatrixFromImage[i - distance][j - distance] != -1) {
                if ((i - distance) >= 0 && (j - distance) >= 0 && cellMatrixFromImage[i][j] != -1 && cellMatrixFromImage[i - distance][j - distance] != -1) {
//                     System.out.println("I am ...");   
                    GLCMMatrix_135_degree[cellMatrixFromImage[i][j]][cellMatrixFromImage[i - distance][j - distance]] += 1;
                }
            }
        }

        int sum = 0;
        for (int i = 0; i < GLCMMatrix_135_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_135_degree[0].length; j++) {
                sum += GLCMMatrix_135_degree[i][j];
            }
        }

        for (int i = 0; i < GLCMMatrix_135_degree.length; i++) {
            for (int j = 0; j < GLCMMatrix_135_degree[0].length; j++) {
                GLCMMatrix_135_degree[i][j] /= sum;
            }

        }
//        for (int i = 0; i < GLCMMatrix_135_degree.length; i++) {
//                for (int j = 0; j < GLCMMatrix_135_degree[0].length; j++) {
//                    System.out.print(GLCMMatrix_135_degree[i][j]+"\t");
//                }
//                System.out.println();
//            }
        return GLCMMatrix_135_degree;
    }

    public Double getSecondMoment(double[][] GLCMMatrix) {//二阶距(能量)
        double secondMoment = 0;
        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                secondMoment += (GLCMMatrix[i][j] * GLCMMatrix[i][j]);

            }
        }

        return secondMoment;
    }

    public Double getContrast(double[][] GLCMMatrix) {//对比度
        double contrast = 0;
        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                contrast += (GLCMMatrix[i][j] * ((i - j) * (i - j)));
            }

        }

        return contrast;
    }

    public Double getCorrelation(double[][] GLCMMatrix) {////相关性
        double correlation = 0;
        double variance = 0;
        double mean = 0;
        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                mean += (i * GLCMMatrix[i][j]);
            }

        }
        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                variance += (GLCMMatrix[i][j] * (i - mean) * (i - mean));
            }

        }

        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                correlation += (GLCMMatrix[i][j] * ((i - mean) * (j - mean) / variance));
            }

        }

        return correlation;
    }

    public Double getEntropy(double[][] GLCMMatrix) {//熵
        double entropy = 0;
        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                if (GLCMMatrix[i][j] != 0) {
                    entropy -= (GLCMMatrix[i][j] * log10(GLCMMatrix[i][j]));
                }
            }

        }

        return entropy;
    }

    public Double getHomogenity(double[][] GLCMMatrix) {//同质
        double homogenity = 0;

        for (int i = 0; i < GLCMMatrix.length; i++) {
            for (int j = 0; j < GLCMMatrix[0].length; j++) {
                homogenity += (GLCMMatrix[i][j] / (1 + (i - j) * (i - j)));

            }
        }

        return homogenity;
    }
}
