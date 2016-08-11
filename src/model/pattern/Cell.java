/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import constants.RotEqClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import main.java.com.imagemodel.*;

/**
 *
 * @author mlei
 */
public class Cell {

    public HashMap<String, Point> pts;
    public HashMap<String, Long> cell_CoLBP_count;
    private ArrayList<Double> LBP_Features;
    private ArrayList<Double> GLCM_Features; // haven't been scaled to [0,1]
    private ArrayList<Double> Combined_Features; // GLCM-LBP

    public ArrayList<Double> getGLCM_Features() {
        return GLCM_Features;
    }

    public ArrayList<Double> getLBP_Features() {
        if (LBP_Features == null) {
            this.initLBP_Features();
        }
        return LBP_Features;
    }

    public ArrayList<Double> getCombined_Features() {
        if (Combined_Features == null) {
            this.initCombined_Features();
        }
        return Combined_Features;
    }

    private ROIClass roi;

    public Cell() {
        this.pts = new HashMap<>();
        this.cell_CoLBP_count = new HashMap<>();
        this.GLCM_Features = new ArrayList<>();
    }

    public ArrayList<Double> initCombined_Features() {
        if (Combined_Features == null) {
            Combined_Features = new ArrayList<>();
            Combined_Features.addAll(GLCM_Features);
            Combined_Features.addAll(this.initLBP_Features());
        }
        return Combined_Features;
    }

    public ArrayList<Double> initLBP_Features() {
        if (LBP_Features == null) {
            LBP_Features = new ArrayList<>();
            for (int r : Point.RADIUS) {
                for (String i : RotEqClass.getAllLabels().split(",")) {
                    Long n = this.cell_CoLBP_count.get(RotEqClass.getKey(r, Integer.parseInt(i)));
                    if (n == null) {
                        this.LBP_Features.add(Double.valueOf(0));
                    } else {
//                        this.LBP_Features.add((n + 0.0));// / this.roi.getArea()
                        this.LBP_Features.add((n + 0.0) / this.roi.getArea());
                    }
                }
            }
        }
        return LBP_Features;
    }

    public void countCoLBP() {
        int colbp;
        String key;
        Long old;

        for (Point p : this.pts.values()) {
            for (int r : Point.RADIUS) {
                if (p.getStatus(r)) {
                    for (int rot = 0; rot < 8; rot++) {
                        colbp = p.getCoALBP(r, rot);
                        key = RotEqClass.getKey(r, colbp);
                        if (key == null) {
                            continue;
                        } else {
                            old = this.cell_CoLBP_count.get(key);
                            if (old == null) {
                                this.cell_CoLBP_count.put(key, Long.valueOf(1));
                            } else {
                                this.cell_CoLBP_count.put(key, old + 1);
                            }
                        }
                    }

                } else {
                    continue;
                }
            }
        }
    }

    public boolean validate_cell_CoLBP_count_key() {
        int rotEq;
        for (String key : cell_CoLBP_count.keySet()) {
            rotEq = Integer.parseInt(key.substring(2, key.length()), 2);
//            System.out.println(key+":"+rotEq);
            if (!RotEqClass.getRotEqClass().contains(rotEq)) {
                System.out.println(key + ":" + rotEq);
                return false;
            }
        }
        return true;
    }

    public void initNeighborPoints() {
        if (this.roi == null) {
            System.out.println("ROI is null: set ROI first");
            return;
        }
        if (this.pts == null) {
            System.out.println("Map is null: init with ROI");
            return;
        }
        for (Point p : this.pts.values()) {
            if (p == null) {
                System.out.println("Point at " + p + " is NULL");
            } else {
                this.putNN(p);
            }
        }
    }

    public void putNN(Point p) {

        HashMap<Integer, Point> map;
        int x, y;
        Point neighbor;

        for (int r : p.r_NNs.keySet()) {
            map = p.r_NNs.get(r);

            for (int i = 0; i < 8; i++) {
                y = i % 4 == 0 ? 0 : Integer.valueOf(4).compareTo(i);
                x = (i + 2) % 4 == 0 ? 0 : Integer.valueOf(4).compareTo((i + 2) % 8);
                neighbor = pts.get((p.getX() + x * r) + "," + (p.getY() + y * r));
                if (neighbor == null) {
                    break;
                }
                map.put(i, neighbor);
            }
            if (map.size() == 8) {
                p.setStatus(r, true);
            }

        }

    }

    public void initWithROIClass(ROIClass roi) {
        if (roi == null) {
            System.out.println("Null ROI input");
        } else {
            this.roi = roi;
            for (ImagePixel pxl : roi.getRoiPixels().values()) {
                this.pts.put(pxl.getPixelX() + "," + pxl.getPixelY(), new Point(pxl, this));
            }
//            System.out.println("No of Pixels in ROI: " + this.pts.size());

            this.GLCM_Features = new CreateGLCMMatrix(roi).caiCalc(roi);
//            System.out.println("No of GLCM Features: " + this.GLCM_Features.size());

        }
        // init NearestNeighbors
        initNeighborPoints();
        this.countCoLBP();
        this.initCombined_Features();
//        this.initLBP_Features();

//        System.out.println("non-zero LBP Feature = " + cell_CoLBP_count.keySet().size());
    }

    public HashMap<Integer, ImagePixel> getRoiPixels() {
        return this.roi.getRoiPixels();
    }

    public boolean isInnerPoint(Point p) {
        return this.getRoiPixels().containsValue(p.getPxl());
    }

    public boolean pointCloseToEdgeX(Point p, int r) {
        int key1 = new ImagePixel(p.getX() + r, p.getY()).getPixelHashMapKey();
        int key2 = new ImagePixel(p.getX() - r, p.getY()).getPixelHashMapKey();
        return this.getRoiPixels().containsKey(key1) && this.getRoiPixels().containsKey(key2);
    }

    public boolean pointCloseToEdgeY(Point p, int r) {
        int key1 = new ImagePixel(p.getX(), p.getY() + r).getPixelHashMapKey();
        int key2 = new ImagePixel(p.getX(), p.getY() - r).getPixelHashMapKey();
        return this.getRoiPixels().containsKey(key1) && this.getRoiPixels().containsKey(key2);
    }

    public ROIClass getRoi() {
        return roi;
    }

    public void setRoi(ROIClass roi) {
        this.roi = roi;
    }

    public void view() {
        for (Point p : this.pts.values()) {
            System.out.print(p.getIntensity() + " ");
        }

    }

    public void test() {
        HashSet<String> history = new HashSet<>();
        String key;
        int sum = 0;
        int inconsistant = 0;
        for (Point p : this.pts.values()) {
            for (int r : Point.RADIUS) {
                for (int i = 0; i < 8; i++) {
                    if (p.getCoALBP(r, i) == -1) {
                        continue;
                    }
                    key = RotEqClass.getKey(r, p.getCoALBP(r, i));
                    if (key != null && !cell_CoLBP_count.containsKey(key)) {
                        System.out.println(p + ": r=" + r + " colbp=" + p.getCoALBP(r, i));
                        inconsistant++;
                        if (!history.contains(key)) {
                            history.add(key);
                            sum++;
                            System.out.println("inconsistant key: " + key);
                        }
                    }

                }

            }
        }
        System.out.println("cum labels=" + sum + " , total inconsistant=" + inconsistant);

        for (String key1 : this.cell_CoLBP_count.keySet()) {
            System.out.println("key=" + key1 + ":count=" + this.cell_CoLBP_count.get(key1));
        }
    }

    public void test1() {
        String key;
        Long val;
        for (int r : Point.RADIUS) {
            for (String i : RotEqClass.getAllLabels().split(",")) {
                key = RotEqClass.getKey(r, Integer.parseInt(i));
                val = this.cell_CoLBP_count.get(key);
                if (val == null) {
                    val = (long) 0;
                }
                if (!RotEqClass.getRotEqClass().contains(i)) {
                    System.out.print("should not happen");
                }
                System.out.println("key=" + key + " count=" + val);
//                    if (this.cell_CoLBP_count.containsKey(key)) {
//                        this.features.add(Long.valueOf(0));
//                    } else {
//                        this.features.add(n);
//                    }
            }
        }
    }

    public void test2() {
        HashSet<Integer> history = new HashSet<>();
        System.out.println("all label" + RotEqClass.getAllLabels());
        int iii;
        for (String key : cell_CoLBP_count.keySet()) {
            iii = Integer.parseInt(key.split(":")[1], 2);
            if (!history.contains(iii)) {
                if (!RotEqClass.getAllLabels().contains("," + iii + ",")) {
                    System.out.print("!!!");
                }
                System.out.println(iii);
                history.add(iii);
            }

        }
    }

    public int numberOfPixels() {
        return this.getRoiPixels().size();
    }
}
