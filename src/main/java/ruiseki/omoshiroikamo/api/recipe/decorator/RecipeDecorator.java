package ruiseki.omoshiroikamo.api.recipe.decorator;

import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

/**
 * Basic decorator for IModularRecipe.
 * Delegates all calls to the internal recipe instance.
 */
public abstract class RecipeDecorator implements IModularRecipe {

    protected final IModularRecipe internal;

    protected RecipeDecorator(IModularRecipe internal) {
        this.internal = internal;
    }

    @Override
    public String getRegistryName() {
        return internal.getRegistryName();
    }

    @Override
    public String getRecipeGroup() {
        return internal.getRecipeGroup();
    }

    @Override
    public String getName() {
        return internal.getName();
    }

    @Override
    public int getDuration() {
        return internal.getDuration();
    }

    @Override
    public int getPriority() {
        return internal.getPriority();
    }

    @Override
    public List<IRecipeInput> getInputs() {
        return internal.getInputs();
    }

    @Override
    public List<IRecipeOutput> getOutputs() {
        return internal.getOutputs();
    }

    @Override
    public List<ICondition> getConditions() {
        return internal.getConditions();
    }

    @Override
    public boolean isConditionMet(ConditionContext context) {
        return internal.isConditionMet(context);
    }

    @Override
    public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {
        return internal.processInputs(inputPorts, simulate);
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        return internal.processOutputs(outputPorts, simulate);
    }

    @Override
    public boolean matchesInput(List<IModularPort> inputPorts) {
        return internal.matchesInput(inputPorts);
    }

    @Override
    public boolean canOutput(List<IModularPort> outputPorts) {
        return internal.canOutput(outputPorts);
    }

    @Override
    public IPortType.Type checkOutputCapacity(List<IModularPort> outputPorts) {
        return internal.checkOutputCapacity(outputPorts);
    }

    @Override
    public void onTick(ConditionContext context) {
        internal.onTick(context);
    }

    @Override
    public int getMaxTierRequired() {
        return internal.getMaxTierRequired();
    }

    @Override
    public Map<String, Integer> getRequiredComponentTiers() {
        return internal.getRequiredComponentTiers();
    }

    @Override
    public int getTotalItemInputCount() {
        return internal.getTotalItemInputCount();
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        internal.accept(visitor);
    }
}
