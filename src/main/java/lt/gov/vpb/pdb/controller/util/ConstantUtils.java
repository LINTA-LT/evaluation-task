package lt.gov.vpb.pdb.controller.util;

import java.util.*;

public class ConstantUtils {

    public static final Map<String, String> STATUS_LT_EN = new LinkedHashMap<>();

    static {
        STATUS_LT_EN.put("Paduota paraiška", "Application filed");
        STATUS_LT_EN.put("Atliekama ekspertizė", "Under examination");
        STATUS_LT_EN.put("Neigiamas sprendimas", "Examination Not OK");
        STATUS_LT_EN.put("Teigiamas sprendimas", "Examination OK");
        STATUS_LT_EN.put("Apeliacija", "Appeal pending");
        STATUS_LT_EN.put("Ženklas atitinka absoliučius reikalavimus (paraiška paskelbta)", "Examination OK (Application published)");
        STATUS_LT_EN.put("Atšaukta paraiška", "Withdrawn");
        STATUS_LT_EN.put("Neregistruotinas ženklas 1", "Negative decision 1");
        STATUS_LT_EN.put("Neregistruotinas ženklas 2", "Negative decision 2");
        STATUS_LT_EN.put("Registruotas", "Registered");
        STATUS_LT_EN.put("Pareikštas protestas", "Opposition pending");
        STATUS_LT_EN.put("Pasibaigęs ženklo galiojimo terminas, su galimybe pratęsti", "Expired with possibility to be restored");
        STATUS_LT_EN.put("Panaikinta registracija", "Cancelled");
        STATUS_LT_EN.put("Išregistruotas", "Ended");
        STATUS_LT_EN.put("Panaikinta paraiška", "Application refused");
        STATUS_LT_EN.put("Išregistruotas, nepratęsus galiojimo termino", "Ended");
        STATUS_LT_EN.put("Nacionalinė registracija pakeista į tarptautinę", "Replaced by international registration");
        STATUS_LT_EN.put("Pateiktas negaliojimo prašymas", "Invalidation pending");
        STATUS_LT_EN.put("Pateiktas panaikinimo prašymas", "Revocation pending");
        STATUS_LT_EN.put("Dalinis neigiamas sprendimas", "Partial negative decision");
    }

    private static Map<String, String> createOption(String value, String labelKey) {
        Map<String, String> map = new HashMap<>();
        map.put("value", value);
        map.put("labelKey", labelKey);
        return map;
    }
}
