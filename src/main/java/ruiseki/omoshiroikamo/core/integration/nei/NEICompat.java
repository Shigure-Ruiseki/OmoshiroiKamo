package ruiseki.omoshiroikamo.core.integration.nei;

import net.minecraftforge.common.MinecraftForge;

import ruiseki.omoshiroikamo.core.lib.LibMods;

public class NEICompat {

    public static void init() {
        if (!LibMods.NotEnoughItems.isLoaded()) {
            return;
        }
        // Manual instantiation and initialization to bypass discovery issues
        NEIConfig config = new NEIConfig();
        MinecraftForge.EVENT_BUS.register(config);
        config.loadConfig();
    }
}
