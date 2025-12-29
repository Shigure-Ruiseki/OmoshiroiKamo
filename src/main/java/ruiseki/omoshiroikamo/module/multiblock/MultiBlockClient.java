package ruiseki.omoshiroikamo.module.multiblock;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.multiblock.client.render.QuantumBeaconTESR;
import ruiseki.omoshiroikamo.module.multiblock.client.render.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.TEQuantumBeacon;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

@SideOnly(Side.CLIENT)
public class MultiBlockClient implements IModuleClient {

    @Override
    public String getId() {
        return "MultiBlock";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useMultiBlock;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumExtractor.class, new QuantumExtractorTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumBeacon.class, new QuantumBeaconTESR());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
