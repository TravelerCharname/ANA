/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.pattern;

import java.util.EnumSet;
import java.util.HashMap;

/**
 *
 * @author mlei
 */
public enum Pattern {
    
    // Warnning! whenever the coding changes, make sure the patternDecision funtion in Predictor.java still works
    CENTROMERE(1, "p1", "Centromere"),
    HOMOGENEOUS(2, "p2", "Homogeneous"),
    NUCLEOLAR(3, "p3", "Nucleolar"),
    PERIPHERAL(4, "p4", "Peripheral"),
    SPECKLED(5, "p5", "Speckled"),
    PERIPHERAL_AND_CENTROMERE(6, "p6", "Peripheral and Centromere"),
    SPECKLED_AND_NUCLEOLAR(7, "p7", "Speckled and Nucleolar"),
    NA(8, "p8", "Not Applicable"),
    HOMOGENEOUS_AND_SPECKELED(9, "p9", "Homogeneous and Speckled"),
    HOMOGENEOUS_AND_NUCLEOLAR(10, "p10", "Homogeneous and Nucleolar"),
    NO_RESULT(-1, "", "NO RESULT");

    private Pattern(double label, String folder, String description) {
        this.label = label;
        this.folder = folder;
        this.description = description;
    }

    private final double label;
    private final String folder;
    private final String description;

    private static final HashMap<String, Pattern> folder2Pattern = new HashMap<>();
    private static final HashMap<Double, Pattern> lable2Pattern = new HashMap<>();
    
    public static int numOfPatterns(){
        return folder2Pattern.size();
    }

    public double getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public static double getLabelByFolder(String folder) {
        return folder2Pattern.get(folder).label;
    }
    
    public static Pattern getPatternByFolder(String folder) {
        return folder2Pattern.get(folder);
    }
    
    public static String getFolderByLabel(double label) {
        return lable2Pattern.get(label).folder;
    }
    
    public static Pattern getPatternByLabel(double label) {
        return lable2Pattern.get(label);
    }

    static {
        // Lookup table
        for (Pattern s : EnumSet.allOf(Pattern.class)) {
            folder2Pattern.put(s.folder, s);
            lable2Pattern.put(s.label, s);
        }
    }

}
