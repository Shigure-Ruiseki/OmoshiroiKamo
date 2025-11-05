package ruiseki.omoshiroikamo.common.init;

import ruiseki.omoshiroikamo.plugin.chicken.ModChickens;

public class ModEntity {

    public static void preInit() {
        ModChickens.preInit();
    }

    public static void init() {
        ModChickens.init();
    }
}
