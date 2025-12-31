package ruiseki.omoshiroikamo.core.integration.IC2;

import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.energy.IOKEnergyTile;

/**
 * Helper class for IC2 energy integration.
 * This class is only loaded when IC2 is present.
 * It provides methods to create and manage IC2EnergyAdapter instances.
 */
public class IC2EUHelper {

    /**
     * Creates a new IC2EnergyAdapter for the given tile.
     * 
     * @param energyTile the energy tile to adapt
     * @param tileEntity the underlying tile entity
     * @return the adapter instance
     */
    public static IC2EnergyAdapter createAdapter(IOKEnergyTile energyTile, TileEntity tileEntity) {
        return new IC2EnergyAdapter(energyTile, tileEntity);
    }

    /**
     * Registers an adapter with the IC2 energy network.
     * 
     * @param adapter the adapter to register
     */
    public static void register(IC2EnergyAdapter adapter) {
        if (adapter != null) {
            adapter.register();
        }
    }

    /**
     * Deregisters an adapter from the IC2 energy network.
     * 
     * @param adapter the adapter to deregister
     */
    public static void deregister(IC2EnergyAdapter adapter) {
        if (adapter != null) {
            adapter.deregister();
        }
    }

    /**
     * Registers a tile entity with the IC2 energy network.
     * Creates an adapter internally.
     * 
     * @param tile the tile entity to register
     */
    public static void register(TileEntity tile) {
        if (tile.getWorldObj().isRemote) return;

        if (tile instanceof IOKEnergyTile) {
            IC2EnergyAdapter adapter = createAdapter((IOKEnergyTile) tile, tile);
            adapter.register();
        }
    }

    /**
     * Deregisters a tile entity from the IC2 energy network.
     * 
     * @param tile the tile entity to deregister
     */
    public static void deregister(TileEntity tile) {
        if (tile.getWorldObj().isRemote) return;

        // Note: This simple implementation creates a new adapter just to deregister.
        // For proper tracking, the adapter should be stored in the tile entity.
        if (tile instanceof IOKEnergyTile) {
            IC2EnergyAdapter adapter = createAdapter((IOKEnergyTile) tile, tile);
            adapter.deregister();
        }
    }
}
