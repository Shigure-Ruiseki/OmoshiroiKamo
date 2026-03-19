package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.IJsonMaterial;

/**
 * Base interface for recipe input requirements.
 * This interface is independent of the Modular Port system.
 * For Modular Port-specific functionality, see IModularRecipeInput.
 */
public interface IRecipeInput extends IJsonMaterial {

    /**
     * Whether this input should be processed per tick.
     */
    boolean isPerTick();

    /**
     * Get the interval (in ticks) for per-tick processing.
     * 0 means not per-tick.
     */
    int getInterval();

    /**
     * Create a copy of this input.
     */
    IRecipeInput copy();

    /**
     * Create a copy of this input with a multi-batch quantity.
     */
    IRecipeInput copy(int multiplier);

    /**
     * Write this input state to NBT.
     */
    void writeToNBT(NBTTagCompound nbt);

    /**
     * Read this input state from NBT.
     */
    void readFromNBT(NBTTagCompound nbt);

    /**
     * Get the amount required for this input.
     */
    long getRequiredAmount();

    /**
     * Whether this input should be consumed.
     * If false, it only checks for presence.
     */
    boolean isConsume();

    /**
     * Accept a visitor to perform operations on this input.
     */
    void accept(IRecipeVisitor visitor);

    /**
     * Get the TickResult to return if this input fails during processing.
     * 
     * @param perTick Whether this is a per-tick check
     */
    default RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.NO_INPUT;
    }

    /**
     * Cast this input to IModularRecipeInput if possible.
     */
    default IModularRecipeInput asModular() {
        return this instanceof IModularRecipeInput ? (IModularRecipeInput) this : null;
    }
}
