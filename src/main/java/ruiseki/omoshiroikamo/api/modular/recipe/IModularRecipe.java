package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Interface for modular recipes.
 * Extends Generic Comparable to handle sorting of various recipe
 * implementations and decorators.
 */
public interface IModularRecipe extends Comparable<IModularRecipe> {

    String getRegistryName();

    String getRecipeGroup();

    String getName();

    int getDuration();

    int getPriority();

    List<IRecipeInput> getInputs();

    List<IRecipeOutput> getOutputs();

    List<ICondition> getConditions();

    boolean isConditionMet(ConditionContext context);

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

    @Override
    default int compareTo(IModularRecipe other) {
        // 1. Higher priority comes first
        int priorityCompare = Integer.compare(other.getPriority(), this.getPriority());
        if (priorityCompare != 0) return priorityCompare;

        // 2. More input types comes first
        int thisInputTypes = (int) this.getInputs()
            .stream()
            .map(IRecipeInput::getPortType)
            .distinct()
            .count();
        int otherInputTypes = (int) other.getInputs()
            .stream()
            .map(IRecipeInput::getPortType)
            .distinct()
            .count();
        int inputTypeCompare = Integer.compare(otherInputTypes, thisInputTypes);
        if (inputTypeCompare != 0) return inputTypeCompare;

        // 3. Larger item stack count comes first
        return Integer.compare(other.getTotalItemInputCount(), this.getTotalItemInputCount());
    }

    default int getTotalItemInputCount() {
        return getInputs().stream()
            .filter(i -> i instanceof ItemInput)
            .mapToInt(i -> (int) ((ItemInput) i).getRequiredAmount())
            .sum();
    }

    /**
     * Called every tick while the recipe is being processed.
     * 
     * @param context The context of the machine processing this recipe.
     */
    void onTick(ruiseki.omoshiroikamo.api.condition.ConditionContext context);

    /**
     * Accept a visitor to perform operations on this recipe.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(IRecipeVisitor visitor);
}
