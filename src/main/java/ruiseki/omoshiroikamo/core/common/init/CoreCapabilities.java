package ruiseki.omoshiroikamo.core.common.init;

import ruiseki.omoshiroikamo.core.common.capabilities.CapabilityItemHandler;

public class CoreCapabilities {

    public static void preInit() {
        CapabilityItemHandler.register();
    }
}
