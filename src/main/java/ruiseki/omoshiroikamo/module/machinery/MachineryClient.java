package ruiseki.omoshiroikamo.module.machinery;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.client.util.MachineryTextureGenerator;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;

/**
 * Client-side module for Machinery.
 * Handles texture generation, renderers, and other client-only features.
 */
@SideOnly(Side.CLIENT)
public class MachineryClient implements IModuleClient {

    private static final MachineryTextureGenerator textureGenerator = new MachineryTextureGenerator();

    @Override
    public String getId() {
        return "Machinery";
    }

    @Override
    public boolean isEnabled() {
        // TODO: Add config option
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Logger.info("MachineryClient: Registering texture generator");

        // Register the texture generator for TextureStitchEvent
        MinecraftForge.EVENT_BUS.register(textureGenerator);

        // Register texture requests from each block
        BlockItemInputPort.registerTexture();
        BlockItemOutputPort.registerTexture();
        BlockEnergyInputPort.registerTexture();
        BlockMachineController.registerTexture();

        Logger.info("MachineryClient: Texture registration complete");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        // TODO: Register TESRs and entity renderers
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        // Post-initialization if needed
    }
}
