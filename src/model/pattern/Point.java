/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import java.util.HashMap;
import main.java.com.imagemodel.ImagePixel;

/**
 *
 * @author mlei
 */
public class Point {

    public static final int[] RADIUS = {1, 2}; //, 3

    private HashMap<Integer, Boolean> status;

    private void initStatus() {
        status = new HashMap<>();
        for (int r : RADIUS) {
            status.put(r, Boolean.FALSE);
        }
    }

    public boolean getStatus(int r) {
        return this.status.get(r);
    }

    public void setStatus(int r, boolean b) {
        this.status.put(r, b);
    }

    public static final int SIGNAL_CHANNEL_INDEX = 1;
    private Cell cell;

    private ImagePixel pxl;
    private double intensity;

    //key=rotation in rad, val=point
    public HashMap<Integer, Point> NN1;
    public HashMap<Integer, Point> NN2;
    public HashMap<Integer, Point> NN3;
    //key=r, val=map
    public HashMap<Integer, HashMap<Integer, Point>> r_NNs;

    /*
    LBP is a 10-based int, defined by sum(sign*2^i),i=0,...,3
        where i denotes rotation pi*i/2
    LBP[0] is horizontal-vertical LBP, phase=0
    LBP[1] is diagonal LBP. phase=pi/4
     */
    public int getLBP(int r, int phase) {

        if (this.r_NNs.containsKey(r)) {
            if (!this.getStatus(r)) {
                return -1;
            }
            int lbp = 0;
            Point neighbor;

            int rot;
            for (int i = 0; i < 8; i = i + 2) {
                rot = (phase + i) % 8;
                neighbor = r_NNs.get(r).get(rot);
                if (neighbor == null) {
                    return -1;
                }
//                lbp += Math.pow(2, (i / 2)) * this.compareToCen(neighbor);
                lbp += (2 ^ (i / 2)) * this.compareToCen(neighbor);
            }
            return lbp;
        } else {
            System.out.println("radius should be either 1,2 or 4...." + r);
            return -1;
        }
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public String getKeyForMap() {
        return this.getX() + "," + this.getY();
    }

    public static String getKeyForMap(int x, int y) {
        return x + "," + y;
    }

    public Point(int x, int y, double intensity, Cell cell) {
        this.initStatus();
        this.cell = cell;
        this.NN1 = new HashMap<>();
        this.NN2 = new HashMap<>();
        this.NN3 = new HashMap<>();
        this.r_NNs = new HashMap<>();
        this.r_NNs.put(1, NN1);
        this.r_NNs.put(2, NN2);
        this.r_NNs.put(3, NN3);
        this.pxl = new ImagePixel(x, y);
        this.intensity = intensity;
    }

    public Point(ImagePixel pxl, Cell cell) {
        this.initStatus();
        this.cell = cell;
        this.NN1 = new HashMap<>();
        this.NN2 = new HashMap<>();
        this.NN3 = new HashMap<>();
        this.r_NNs = new HashMap<>();
        this.r_NNs.put(1, NN1);
        this.r_NNs.put(2, NN2);
        this.r_NNs.put(3, NN3);
        this.pxl = pxl;
        this.intensity = pxl.getGreyChannels().get(SIGNAL_CHANNEL_INDEX);
    }

    public ImagePixel getPxl() {
        return pxl;
    }

    public void setPxl(ImagePixel pxl) {
        this.pxl = pxl;
    }

    public int getX() {
        return this.pxl.getPixelX();
    }

    public int getY() {
        return this.pxl.getPixelY();
    }

    public int compareToCen(Point p) {
        if (p == null) {
            return -99;
        }
        return this.intensity > p.getIntensity() ? 0 : 1;
    }

    public int getCoALBP(int r, int rotation) {
        if (!this.getStatus(r)) {
            return -1;
        }

        Point neighbor = null;
        try {
            neighbor = this.r_NNs.get(r).get(rotation);
            neighbor = neighbor.r_NNs.get(r).get(rotation);
        } catch (NullPointerException e) {
            System.out.println("get neighbor failed@" + this.toString() + "-" + r);

        }
        if (neighbor == null) {
            return -1;
        }
        if (!neighbor.getStatus(r)) {
//            System.out.println("CoLBP does not exist: neighbor status is false");
            return -1;
        }

        return this.getLBP(r, rotation) * 16 + neighbor.getLBP(r, rotation);

    }

    public boolean closeToEdge(int r) {
        return this.cell.pointCloseToEdgeX(this, r) && this.cell.pointCloseToEdgeY(this, r);
    }

    public boolean isEdge() {
        if (this.cell == null) {
            System.out.println("no cell associated");
            return false;
        }
        if (this.cell.getRoi() == null) {
            System.out.println("no ROI data given to cell");
            return false;
        }
        if (this.cell.getRoi().getEdgePixels() != null) {
            return this.cell.getRoi().getEdgePixels().containsKey(this.pxl.getPixelHashMapKey());
        } else {
            System.out.println("Bad ROIClass instance!");
            return false;
        }
    }

    @Override
    public String toString() {
        return "(" + this.getX() + "," + this.getY() + ")";
    }
}
