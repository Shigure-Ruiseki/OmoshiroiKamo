package ruiseki.omoshiroikamo.api.modular;

import net.minecraft.util.ResourceLocation;

import ruiseki.omoshiroikamo.api.block.IOKTile;
import ruiseki.omoshiroikamo.api.block.ISidedIO;

/**
 * Base marker interface for modular machinery IO ports.
 * Implemented by TileEntities that can be part of a modular machine structure.
 */
public interface IModularPort extends IPortType, ISidedIO, IOKTile {

    ResourceLocation getPortOverlay();
}
