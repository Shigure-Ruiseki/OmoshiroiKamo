package ruiseki.omoshiroikamo.core.integration.waila;

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMods;

public class WailaCompat {

    public static void init() {
        if (!LibMods.Waila.isLoaded()) {
            return;
        }
        EntityProvider.init();
        BlockProvider.init();
        Logger.info("Loaded WailaCompat");
    }
}
