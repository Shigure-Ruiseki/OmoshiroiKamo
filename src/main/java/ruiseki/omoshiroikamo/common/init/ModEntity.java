package ruiseki.omoshiroikamo.common.init;

import ruiseki.omoshiroikamo.plugin.cow.ModCows;
import ruiseki.omoshiroikamo.plugin.dml.ModModels;

public class ModEntity {

    public static void preInit() {
        ModCows.preInit();
    }

    public static void init() {
        ModCows.init();
        ModModels.init();
    }

    public static void postInit() {
        ModCows.postInit();
        ModModels.postInit();
    }
}
