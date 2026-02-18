package ruiseki.omoshiroikamo.api.ids.block;

import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;

/**
 * Facade for a {@link IEnergyBattery} at a certain position.
 * Must be implemented on blocks.
 * 
 * @author rubensworks
 */
public interface IEnergyBatteryFacade {

    /**
     * Get the energy battery at a given position.
     * 
     * @param world The world.
     * @param pos   The position.
     * @return The variable container.
     */
    public IEnergyBattery getEnergyBattery(World world, BlockPos pos);

}
