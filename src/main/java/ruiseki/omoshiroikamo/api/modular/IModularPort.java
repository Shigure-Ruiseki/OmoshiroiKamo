package ruiseki.omoshiroikamo.api.modular;

import ruiseki.omoshiroikamo.api.io.ISidedIO;

/**
 * Base marker interface for modular machinery IO ports.
 * Implemented by TileEntities that can be part of a modular machine structure.
 * 
 * Use sub-interfaces to specify port type and direction:
 * - Type: {@link IItemPort}, {@link IFluidPort}, {@link IEnergyPort},
 * {@link IManaPort}
 * - Direction: {@link IInputPort}, {@link IOutputPort}
 * 
 * Example: A class implementing both IItemPort and IOutputPort is an Item
 * Output Port.
 */
public interface IModularPort extends ISidedIO {
}
