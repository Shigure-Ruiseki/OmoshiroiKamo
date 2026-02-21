package ruiseki.omoshiroikamo.core.creative;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class CreativeTabRegistry {

    private static final Map<String, OKCreativeTab> TABS = new LinkedHashMap<>();

    public static OKCreativeTab create(String name) {
        OKCreativeTab tab = new OKCreativeTab(name);
        TABS.put(name, tab);
        return tab;
    }

    public static OKCreativeTab get(String name) {
        return TABS.get(name);
    }

    public static Collection<OKCreativeTab> all() {
        return TABS.values();
    }
}
