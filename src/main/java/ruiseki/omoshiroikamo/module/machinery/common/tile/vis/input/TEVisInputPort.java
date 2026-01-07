package ruiseki.omoshiroikamo.module.machinery.common.tile.vis.input;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;

/**
 * Vis Input Port - receives Vis from Vis Relay network.
 */
public class TEVisInputPort extends AbstractVisPortTE {

    private static final int DEFAULT_VIS_CAPACITY = 100;

    public TEVisInputPort() {
        super(DEFAULT_VIS_CAPACITY);
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote && shouldDoWorkThisTick(10)) {
            drawVisFromNetwork();
        }
        return false;
    }

    /**
     * Draw vis from nearby Vis Relay network.
     */
    private void drawVisFromNetwork() {
        // Request vis from the network for each primal aspect
        Aspect[] primals = { Aspect.AIR, Aspect.WATER, Aspect.FIRE, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY };

        for (Aspect primal : primals) {
            int current = visStored.getAmount(primal);
            int space = maxVisPerAspect - current;

            if (space > 0) {
                // Request up to 10 vis per tick
                int toRequest = Math.min(space, 10);
                int received = VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, primal, toRequest);
                if (received > 0) {
                    addVis(primal, received);
                }
            }
        }
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1 && getSideIO(side) != EnumIO.NONE) {
            return IconRegistry.getIcon("overlay_visinput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
