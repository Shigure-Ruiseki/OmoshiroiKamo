package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.IJsonMaterial;

/**
 * Interface for recipe output requirements.
 * Implementations define how to check and produce outputs in ports.
 */
public interface IRecipeOutput extends IJsonMaterial {

    /**
     * Get the port type this output requires.
     */
    IPortType.Type getPortType();

    /**
     * Check if the ports have enough capacity to store this output.
     */
    boolean checkCapacity(List<IModularPort> ports, int multiplier);

    /**
     * Legacy support for single batch capacity check.
     */
    default boolean checkCapacity(List<IModularPort> ports) {
        return checkCapacity(ports, 1);
    }

    /**
     * Produce the output and store it in the provided ports.
     */
    void apply(List<IModularPort> ports, int multiplier);

    /**
     * Legacy support for single batch apply.
     */
    default void apply(List<IModularPort> ports) {
        apply(ports, 1);
    }

    /**
     * Check if this output is satisfied (legacy support for process if needed).
     * Now use checkCapacity and apply separately in ModularRecipe.
     */
    default boolean process(List<IModularPort> ports, boolean simulate) {
        return process(ports, 1, simulate);
    }

    /**
     * Multi-batch support for process.
     */
    default boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        if (simulate) return checkCapacity(ports, multiplier);
        apply(ports, multiplier);
        return true;
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
}
