package lt.gov.vpb.pdb.model.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum StringMatching implements Matching {
    CONTAINS("has"),
    STARTS_WITH("sw"),
    ENDS_WITH("ew"),
    EQUALS("eq"),
    //tokenized string operators, for text search
    CONTAINS_ANY("any"), CONTAINS_ALL("all");

    String value;

    private static final Map<String, StringMatching> ENUM_MAP;

    static {
        Map<String, StringMatching> map = new ConcurrentHashMap<>();
        for (StringMatching instance : StringMatching.values()) {
            map.put(instance.getValue(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    StringMatching(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StringMatching fromString(String name) {
        return ENUM_MAP.get(name);
    }

}

