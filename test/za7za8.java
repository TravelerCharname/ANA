
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.java.com.dto.ANAPillarInfo;
import main.java.com.dto.ANASampleInfo;
import main.java.lis.constant.DiagnosisConstant;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mlei
 */
public class za7za8 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            System.out.println("b="+booleanTest());
        } catch (Exception ex) {
            Logger.getLogger(za7za8.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void numberFormatTest() {
        Double d;
        d = 2D;
        System.out.println("null>3"+(d>3D));
//        getNullTest();
//        treeMapOrderTest2();
//        decreasingMapOrderTest();
    }
    public static boolean booleanTest() throws Exception{
        boolean b;
        
        b=(2>Double.parseDouble(null));
        
        return b;
    }

    public static void treeMapOrderTest2() {
        TreeMap<DiagnosisConstant.ANA_Titer, String> tm = new TreeMap<>(new Comparator<DiagnosisConstant.ANA_Titer>() {
            @Override
            public int compare(DiagnosisConstant.ANA_Titer t, DiagnosisConstant.ANA_Titer t1) {
                if (t.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() < 0) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                if (t1.getId() > 6) {
                    throw new RuntimeException("Titer: " + t.name());
                }
                return t.getId() < t1.getId() ? -1 : t.getId() == t1.getId() ? 0 : 1;
            }
        }.reversed());
        HashMap<DiagnosisConstant.ANA_Titer, String> hm=new HashMap<>();
        String str;
        for(DiagnosisConstant.ANA_Titer t:DiagnosisConstant.ANA_Titer.values()){
            if(t.getId()>6||t.getId()<0)continue;
            str = t.getId()+":"+ t.getDescription();
            hm.put(t, str);System.out.println(str);
        }
        tm.putAll(hm);
        Iterator<DiagnosisConstant.ANA_Titer> it = tm.keySet().iterator();
        while(it.hasNext()){
            System.out.println(tm.get(it.next()));
        }
    }

    public static void getNullTest() {
        // TODO code application logic here
        HashMap<Object,Double> map=new HashMap<>();
        map.put(new Object(),null);
        map.put(null, 123d);
        System.out.println("get null: "+map.get(null));
        double d;
        for(Object obj:map.keySet()){
            d=map.get(obj);
            System.out.println(obj+":"+d);
        }
        
        
    }

    private static void decreasingMapOrderTest() {
        final Comparator<DiagnosisConstant.ANA_Titer> titerComparator = new Comparator<DiagnosisConstant.ANA_Titer>() {
            @Override
            public int compare(DiagnosisConstant.ANA_Titer t, DiagnosisConstant.ANA_Titer t1) {
        
                return t.getId() < t1.getId() ? -1 : t.getId() == t1.getId() ? 0 : 1;
            }
        };
        TreeMap<DiagnosisConstant.ANA_Titer, String> tm = new TreeMap<>(titerComparator.reversed());
        String str;
        System.out.println("input");
        for(DiagnosisConstant.ANA_Titer t:DiagnosisConstant.ANA_Titer.values()){
            if(t.getId()>6)continue;
            str = t.getId()+":"+ t.getDescription();
            tm.put(t, str);System.out.println(str);
        }
        System.out.println("output");
        Iterator<DiagnosisConstant.ANA_Titer> it = tm.keySet().iterator();
        while(it.hasNext()){
            System.out.println(tm.get(it.next()));
        }
    }
    
}
