package ruiseki.omoshiroikamo.core.compat.waila;

import org.apache.logging.log4j.Level;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.compat.LibMods;

public class WailaCompat {

    public static void init() {
        if (!LibMods.Waila.isLoaded()) {
            return;
        }
        EntityProvider.init();
        OmoshiroiKamo.okLog(Level.INFO, "Loaded WailaCompat");
    }
}
