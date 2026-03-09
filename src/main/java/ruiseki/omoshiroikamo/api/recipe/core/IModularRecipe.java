package ruiseki.omoshiroikamo.api.recipe.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Interface for modular recipes.
 * Extends Generic Comparable to handle sorting of various recipe
 * implementations and decorators.
 */
public interface IModularRecipe extends IRecipe {

    /**
     * @param inputPorts List of input ports
     * @param simulate   If true, only check. If false, consume inputs.
     * @return true if all inputs are satisfied
     */
    boolean processInputs(List<IModularPort> inputPorts, boolean simulate);

    /**
     * @param outputPorts List of output ports
     * @param simulate    If true, only check. If false, produce outputs.
     * @return true if all outputs can be inserted
     */
    boolean processOutputs(List<IModularPort> outputPorts, boolean simulate);

    boolean matchesInput(List<IModularPort> inputPorts);

    boolean canOutput(List<IModularPort> outputPorts);

    IPortType.Type checkOutputCapacity(List<IModularPort> outputPorts);

    /**
     * Get the required Tier for specific components.
     * key: component name, value: required Tier.
     */
    default Map<String, Integer> getRequiredComponentTiers() {
        return Collections.emptyMap();
    }
}
