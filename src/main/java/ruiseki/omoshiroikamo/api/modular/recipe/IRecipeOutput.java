package ruiseki.omoshiroikamo.api.modular.recipe;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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
    boolean checkCapacity(java.util.List<IModularPort> ports);

    /**
     * Produce the output and store it in the provided ports.
     */
    void apply(java.util.List<IModularPort> ports);

    /**
     * Check if this output is satisfied (legacy support for process if needed).
     * Now use checkCapacity and apply separately in ModularRecipe.
     */
    default boolean process(java.util.List<IModularPort> ports, boolean simulate) {
        if (simulate) return checkCapacity(ports);
        apply(ports);
        return true;
    }

    /**
     * Create a deep copy of this output.
     */
    IRecipeOutput copy();

    /**
     * Write this output state to NBT.
     */
    void writeToNBT(NBTTagCompound nbt);

    /**
     * Read this output state from NBT.
     */
    void readFromNBT(NBTTagCompound nbt);

    /**
     * Accept a visitor to perform operations on this output.
     */
    void accept(IRecipeVisitor visitor);
}
