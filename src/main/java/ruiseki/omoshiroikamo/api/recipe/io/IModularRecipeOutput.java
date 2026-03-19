package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Interface for recipe outputs that work with the Modular Port system.
 * Extends the base IRecipeOutput with Modular Port-specific operations.
 */
public interface IModularRecipeOutput extends IRecipeOutput {

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

    @Override
    default IModularRecipeOutput asModular() {
        return this;
    }
}
