package ruiseki.omoshiroikamo.core.common.block.abstractClass;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;

/**
 * Helper class for IC2 energy integration.
 * This class is separated to prevent ClassNotFoundError when IC2 is not
 * present.
 */
public class IC2EnergyHelper {

    public static void register(TileEntity tile) {
        if (tile.getWorldObj().isRemote) return;

        TileEntity registered = EnergyNet.instance
            .getTileEntity(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

        if (registered != tile) {
            if (registered instanceof IEnergyTile) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) registered));
            } else if (registered == null && tile instanceof IEnergyTile) {
                MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
            }
        }
    }

    public static void deregister(TileEntity tile) {
        if (tile.getWorldObj().isRemote) return;

        TileEntity registered = EnergyNet.instance
            .getTileEntity(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);

        if (registered instanceof IEnergyTile) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) registered));
        }
    }
}
