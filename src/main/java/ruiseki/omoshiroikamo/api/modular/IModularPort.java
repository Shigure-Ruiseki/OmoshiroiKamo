package ruiseki.omoshiroikamo.api.modular;

import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;

/**
 * Base marker interface for modular machinery IO ports.
 * Implemented by TileEntities that can be part of a modular machine structure.
 */
public interface IModularPort extends IPortType, ISidedIO, ISidedTexture {

    /**
     * Accept a visitor to perform operations on this port.
     */
    void accept(IRecipeVisitor visitor);
}
