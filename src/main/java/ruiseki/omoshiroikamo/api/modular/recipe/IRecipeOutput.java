package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Interface for recipe output production.
 * Implementations define how to check space and produce outputs to ports.
 */
public interface IRecipeOutput {

    /**
     * Get the port type this output produces.
     */
    IPortType.Type getPortType();

    /**
     * Check if this output can be inserted and optionally produce it.
     *
     * @param ports    List of output ports to check/produce to
     * @param simulate If true, only check without producing. If false, actually
     *                 produce.
     * @return true if the output can be/was inserted
     */
    boolean process(List<IModularPort> ports, boolean simulate);
}
