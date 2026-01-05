package ruiseki.omoshiroikamo.api.modular;

import ruiseki.omoshiroikamo.api.block.IOKTile;
import ruiseki.omoshiroikamo.api.io.ISidedIO;

/**
 * Base marker interface for modular machinery IO ports.
 * Implemented by TileEntities that can be part of a modular machine structure.
 */
public interface IModularPort extends IPortType, ISidedIO, IOKTile, ISidedTexture {
}
