package ruiseki.omoshiroikamo.module.ids.common.capabilities;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.block.IDynamicLight;
import ruiseki.omoshiroikamo.core.datastructure.EnumFacingMap;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.TECable;

public class SidedDynamicLight implements IDynamicLight {

    public final TECable tile;
    public final ForgeDirection side;

    public SidedDynamicLight(TECable tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    protected EnumFacingMap<Integer> getLightLevels() {
        return tile.getLightLevels();
    }

    @Override
    public void setLightLevel(int level) {
        if (!tile.getWorld().isRemote) {
            boolean sendUpdate = false;
            EnumFacingMap<Integer> lightLevels = getLightLevels();
            if (lightLevels.containsKey(side)) {
                if (lightLevels.get(side) != level) {
                    sendUpdate = true;
                    lightLevels.put(side, level);
                }
            } else {
                sendUpdate = true;
                lightLevels.put(side, level);
            }
            if (sendUpdate) {
                tile.updateLightInfo();
            }
        }
    }

    @Override
    public int getLightLevel() {
        EnumFacingMap<Integer> lightLevels = getLightLevels();
        if (lightLevels.containsKey(side)) {
            return lightLevels.get(side);
        }
        return 0;
    }
}
