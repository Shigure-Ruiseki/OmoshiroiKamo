package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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
     * @param ports    List of input ports to check/consume from
     * @param simulate If true, only check without consuming. If false, actually
     *                 consume.
     * @return true if the requirement is/was satisfied
     */
    boolean process(List<IModularPort> ports, boolean simulate);
}
