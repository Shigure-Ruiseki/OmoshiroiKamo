package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
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
     *
     * @param ports      List of ports to check
     * @param multiplier The batch size multiplier
     * @param context    The condition context for dynamic evaluation
     * @return true if there is enough capacity
     */
    boolean checkCapacity(List<IModularPort> ports, int multiplier, ConditionContext context);

    /**
     * Produce the output and store it in the provided ports.
     *
     * @param ports      List of ports to apply to
     * @param multiplier The batch size multiplier
     * @param context    The condition context for dynamic evaluation
     */
    void apply(List<IModularPort> ports, int multiplier, ConditionContext context);

    /**
     * Multi-batch support for process.
     *
     * @param ports      List of ports to check/apply
     * @param multiplier The batch size multiplier
     * @param simulate   If true, only check capacity. If false, apply output.
     * @param context    The condition context for dynamic evaluation
     * @return true if successful
     */
    default boolean process(List<IModularPort> ports, int multiplier, boolean simulate, ConditionContext context) {
        if (simulate) return checkCapacity(ports, multiplier, context);
        apply(ports, multiplier, context);
        return true;
    }

    default boolean process(List<IModularPort> ports, boolean simulate, ConditionContext context) {
        return process(ports, 1, simulate, context);
    }

    // --- Default / Legacy Support ---

    default boolean checkCapacity(List<IModularPort> ports, int multiplier) {
        return checkCapacity(ports, multiplier, null);
    }

    default boolean checkCapacity(List<IModularPort> ports) {
        return checkCapacity(ports, 1, null);
    }

    default void apply(List<IModularPort> ports, int multiplier) {
        apply(ports, multiplier, null);
    }

    default void apply(List<IModularPort> ports) {
        apply(ports, 1, null);
    }

    default boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        return process(ports, multiplier, simulate, null);
    }

    default boolean process(List<IModularPort> ports, boolean simulate) {
        return process(ports, 1, simulate, null);
    }

    @Override
    default IModularRecipeOutput asModular() {
        return this;
    }

    /**
     * Get the target port index, if specified.
     * 
     * @return Target index, or -1 for any port.
     */
    default int getIndex() {
        return -1;
    }
}
