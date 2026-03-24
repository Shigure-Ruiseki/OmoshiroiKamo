package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.IJsonMaterial;

/**
 * Base interface for recipe output requirements.
 * This interface is independent of the Modular Port system.
 * For Modular Port-specific functionality, see IModularRecipeOutput.
 */
public interface IRecipeOutput extends IJsonMaterial {

    /**
     * Get the interval (in ticks) for per-tick processing.
     * 0 means not per-tick.
     */
    int getInterval();

    /**
     * Whether this output should be processed per tick.
     */
    default boolean isPerTick() {
        return getInterval() > 0;
    }

    /**
     * Create a deep copy of this output.
     */
    IRecipeOutput copy();

    /**
     * Create a deep copy of this output with a multi-batch quantity.
     *
     * @param multiplier The batch size multiplier
     */
    IRecipeOutput copy(int multiplier);

    /**
     * Write this output state to NBT.
     */
    void writeToNBT(NBTTagCompound nbt);

    /**
     * Read this output state from NBT.
     */
    void readFromNBT(NBTTagCompound nbt);

    /**
     * Get the amount produced by this output.
     */
    long getRequiredAmount();

    /**
     * Accept a visitor to perform operations on this output.
     */
    void accept(IRecipeVisitor visitor);

    /**
     * Get the TickResult to return if this output fails during processing.
     * 
     * @param perTick Whether this is a per-tick check
     */
    default RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.OUTPUT_FULL;
    }

    /**
     * Cast this output to IModularRecipeOutput if possible.
     */
    default IModularRecipeOutput asModular() {
        return this instanceof IModularRecipeOutput ? (IModularRecipeOutput) this : null;
    }
}
