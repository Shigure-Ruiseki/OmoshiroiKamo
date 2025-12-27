package ruiseki.omoshiroikamo.module.machinery.client;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockItemOutputPort;

/**
 * Client-side initialization for the Machinery module.
 * Registers texture generation and client events.
 */
@SideOnly(Side.CLIENT)
public class MachineryClient {

    private static final MachineryTextureGenerator textureGenerator = new MachineryTextureGenerator();

    /**
     * Call this during client preInit.
     * Registers texture generation requests and event handlers.
     */
    public static void preInit() {
        Logger.info("MachineryClient: Registering texture generator");

        // Register the texture generator for TextureStitchEvent
        MinecraftForge.EVENT_BUS.register(textureGenerator);

        // Register texture requests from each IO block
        BlockItemInputPort.registerTexture();
        BlockItemOutputPort.registerTexture();
        BlockEnergyInputPort.registerTexture();

        Logger.info("MachineryClient: Texture registration complete");
    }

    /**
     * Call this during client init.
     */
    public static void init() {
        // Additional client initialization if needed
    }
}
