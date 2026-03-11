package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.IJsonMaterial;

/**
 * Interface for recipe input requirements.
 * Implementations define how to check and consume inputs from ports.
 */
public interface IRecipeInput extends IJsonMaterial {

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
     * @return true if the requirement is/was satisfied
     */
    boolean process(List<IModularPort> ports, int multiplier, boolean simulate);

    /**
     * Legacy support for single batch processing.
     */
    default boolean process(List<IModularPort> ports, boolean simulate) {
        return process(ports, 1, simulate);
    }

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
}
