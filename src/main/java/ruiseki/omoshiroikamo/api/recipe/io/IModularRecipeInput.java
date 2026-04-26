package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Interface for recipe inputs that work with the Modular Port system.
 * Extends the base IRecipeInput with Modular Port-specific operations.
 */
public interface IModularRecipeInput extends IRecipeInput {

    /**
     * Get the port type this input requires.
     */
    IPortType.Type getPortType();

    /**
     * Check if this input can be satisfied and optionally consume it.
     *
     * @param ports      List of input ports to check/consume from
     * @param multiplier The batch size multiplier
     * @param simulate   If true, only check without consuming. If false, actually
     *                   consume.
     * @param context    The condition context for dynamic evaluation
     * @return true if the requirement is/was satisfied
     */
    boolean process(List<IModularPort> ports, int multiplier, boolean simulate, ConditionContext context);

    default boolean process(List<IModularPort> ports, boolean simulate, ConditionContext context) {
        return process(ports, 1, simulate, context);
    }

    /**
     * Get the required amount based on the provided condition context.
     *
     * @param context The condition context
     * @return The required amount
     */
    default long getRequiredAmount(ConditionContext context) {
        return getRequiredAmount();
    }

    default boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        return process(ports, multiplier, simulate, null);
    }

    default boolean process(List<IModularPort> ports, boolean simulate) {
        return process(ports, 1, simulate, null);
    }

    @Override
    default IModularRecipeInput asModular() {
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
