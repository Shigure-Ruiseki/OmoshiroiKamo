package ruiseki.omoshiroikamo.api.recipe.core;

import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.recipe.io.IModularRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

/**
 * Base interface for all modular recipes.
 * Extends Generic Comparable to handle sorting of various recipe
 * implementations and decorators.
 */
public interface IRecipe extends Comparable<IRecipe> {

    String getRegistryName();

    String getRecipeGroup();

    String getName();

    int getDuration();

    int getPriority();

    /**
     * Get the maximum Tier required by this recipe across all component
     * requirements.
     * 
     * @return the max tier required, defaults to 0.
     */
    default int getMaxTierRequired() {
        return 0;
    }

    List<IRecipeInput> getInputs();

    List<IRecipeOutput> getOutputs();

    List<ICondition> getConditions();

    boolean isConditionMet(ConditionContext context);

    @Override
    default int compareTo(IRecipe other) {
        // 1. Higher Tier comes first
        int tierCompare = Integer.compare(other.getMaxTierRequired(), this.getMaxTierRequired());
        if (tierCompare != 0) return tierCompare;

        // 2. Higher priority comes first
        int priorityCompare = Integer.compare(other.getPriority(), this.getPriority());
        if (priorityCompare != 0) return priorityCompare;

        // 2. More input types comes first
        int thisInputTypes = (int) this.getInputs()
            .stream()
            .filter(i -> i instanceof IModularRecipeInput)
            .map(i -> ((IModularRecipeInput) i).getPortType())
            .distinct()
            .count();
        int otherInputTypes = (int) other.getInputs()
            .stream()
            .filter(i -> i instanceof IModularRecipeInput)
            .map(i -> ((IModularRecipeInput) i).getPortType())
            .distinct()
            .count();
        int inputTypeCompare = Integer.compare(otherInputTypes, thisInputTypes);
        if (inputTypeCompare != 0) return inputTypeCompare;

        int stackCompare = Integer.compare(other.getTotalItemInputCount(), this.getTotalItemInputCount());
        if (stackCompare != 0) return stackCompare;

        // 4. Registry name alphabetical order
        if (this.getRegistryName() != null && other.getRegistryName() != null) {
            return this.getRegistryName()
                .compareTo(other.getRegistryName());
        }
        return 0;
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
    void onTick(ConditionContext context);

    /**
     * Accept a visitor to perform operations on this recipe.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(IRecipeVisitor visitor);
}
