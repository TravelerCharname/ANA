/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package constants;

import java.util.HashSet;

/**
 *
 * @author mlei
 */
public class RotEqClass {

    private static final HashSet<Integer> rotEqClass = new HashSet<>();
    private static String all = "";
    private static String twice = "";

    static {
        collect();
    }

    public static HashSet<Integer> getRotEqClass() {
        return rotEqClass;
    }

    public static String getAllLabels() {
        return all;
    }

    public static String getRotEqLabel() {
        return twice;
    }
    
    

    public static void show() {
        int fore, back;
        for (int i = 0; i < 256; i++) {
            int res = mirror_CoLBP(i);
            System.out.print("i = " + i + ", rot i = " + Integer.toBinaryString(res) + "   "+getKey(1, i));
            System.out.println();
        }
    }

    public static int rotPi(int i) {
        // (abcd) => (cdab) (base 2)
        return (i << 2 & 15) + (i >> 2);
    }

    public static int rotHalfPi(int i) {
        // (abcd) => (dabc) (base 2)
        return (i << 1 & 15) + (i >> 3);
    }

    public static int calc_CoLBP(int lbp_centr, int lbp_neibr, int rotation) {
        if (rotation < 0) {
            System.out.println("Fool! Rotation must not be negative!");
            return -1;
        }
        int n = rotation / 2;
        int result;
        if (n < 2) {
            for (int i = 0; i < n; i++) {
                lbp_centr = rotHalfPi(lbp_centr);
                lbp_neibr = rotHalfPi(lbp_neibr);
            }
            result = (lbp_centr << 4) + lbp_neibr;
            if (!rotEqClass.contains(result)) {
                result = mirror_CoLBP(result);
            }
            return result;
        } else if (n < 4) {
            return calc_CoLBP(lbp_neibr, lbp_centr, rotation - 4);
        } else {
            System.out.println("rotation " + rotation + " out of bound: 0-7");
            return -1;
        }
    }

    public static int mirror_CoLBP(int i) {
        // (abcd)(efgh) => (ghef)(cdab) (base 2)
        if (i < 0) {
            return -1;
        }
        int fore = i >> 4;
        fore = rotPi(fore);
        int back = i & 15;
        back = rotPi(back);
        return (back << 4) + fore;

    }

    private static void collect() {
        for (int i = 0; i < 256; i++) {
            if (rotEqClass.contains(mirror_CoLBP(i))) {
//                System.out.println(i+" is equi to "+rotPi2(i));
                twice += i + ",";
            } else {
                rotEqClass.add(i);
                all += i + ",";
            }
        }
    }

    public static String getKey(int r, int colbp) {
        if (colbp == -1) {
//            int[] arr=new int[3];
//            arr[3]=3;
            return null;
        }
        if (!rotEqClass.contains(colbp)) {
            colbp = mirror_CoLBP(colbp);
        }
        String str = Integer.toBinaryString(colbp);
        int n = str.length();
        for (int i = 0; i < (8 - n); i++) {
            str = "0" + str;
        }
        return r + ":" + str;
    }

    public static int getRadius(int key) {
        return key / 1000;
    }

    public static String getCoLBPSting(int key) {
        key = key % 1000;
        int fore = (key & 240) / 16;
        int back = key & 15;
        return "(" + Integer.toBinaryString(fore) + ")-(" + Integer.toBinaryString(back) + ")";
    }
}
