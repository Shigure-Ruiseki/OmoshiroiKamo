package ruiseki.omoshiroikamo.api.modular;

import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
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

    /**
     * Get the tier level of this port (0-15).
     *
     * @return The tier level
     */
    int getTier();

    /**
     * Set the tier level of this port (0-15).
     *
     * @param tier The tier level to set
     */
    void setTier(int tier);

    /**
     * Check if this port is currently valid and usable.
     * Template Method Pattern: each implementation defines its own validation logic.
     *
     * Default implementation checks if this port is a TileEntity and whether it's invalid.
     * Subclasses (especially proxies) should override this to provide custom validation.
     *
     * @return true if this port is valid and can be used, false if it should be filtered out
     */
    default boolean isPortValid() {
        // Default implementation: check TileEntity validity
        if (this instanceof TileEntity) {
            return !((TileEntity) this).isInvalid();
        }
        return true;
    }

    /**
     * Get the index assigned to this port during structure formation.
     * 
     * @return The assigned index, or -1 if none.
     */
    default int getAssignedIndex() {
        return -1;
    }

    /**
     * Set the index assigned to this port during structure formation.
     * 
     * @param index The index to assign.
     */
    default void setAssignedIndex(int index) {}
}
