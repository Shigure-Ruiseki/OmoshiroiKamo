package ruiseki.omoshiroikamo.module.machinery;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Client-side module for Machinery.
 * Handles renderers and other client-only features.
 * Texture rendering is handled via JSON models and GTNHLib JSON_ISBRH.
 */
@SideOnly(Side.CLIENT)
public class MachineryClient implements IModuleClient {

    @Override
    public String getId() {
        return "Machinery";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useMachinery;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Logger.info("MachineryClient: Pre-init complete");
        // JSON models are automatically loaded by GTNHLib
    }

    @Override
    public void init(FMLInitializationEvent event) {
        // TODO: Register TESRs and entity renderers if needed
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        // Post-initialization if needed
    }
}
