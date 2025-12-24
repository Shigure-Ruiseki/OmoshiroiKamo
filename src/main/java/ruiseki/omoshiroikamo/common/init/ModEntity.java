package ruiseki.omoshiroikamo.common.init;

import ruiseki.omoshiroikamo.plugin.chicken.ModChickens;
import ruiseki.omoshiroikamo.plugin.cow.ModCows;
import ruiseki.omoshiroikamo.plugin.dml.ModModels;

public class ModEntity {

    public static void preInit() {
        ModChickens.preInit();
        ModCows.preInit();
    }

    public static void init() {
        ModChickens.init();
        ModCows.init();
        ModModels.init();
    }

    public static void postInit() {
        ModChickens.postInit();
        ModCows.postInit();
        ModModels.postInit();
    }
}
