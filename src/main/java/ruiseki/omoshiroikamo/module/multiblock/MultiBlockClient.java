package ruiseki.omoshiroikamo.module.multiblock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.module.multiblock.client.render.QuantumBeaconTESR;
import ruiseki.omoshiroikamo.module.multiblock.client.render.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon.TEQuantumBeacon;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

@SideOnly(Side.CLIENT)
public class MultiBlockClient extends ClientProxyComponent {

    public MultiBlockClient() {
        super(new MultiBlockCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerTickHandlers() {
        registerRenderer(TEQuantumBeacon.class, new QuantumBeaconTESR());
        registerRenderer(TEQuantumExtractor.class, new QuantumExtractorTESR());
        super.registerTickHandlers();
    }
}
