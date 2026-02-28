package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

public class ModularRecipe implements IModularRecipe {

    private final String registryName;
    private final String recipeGroup;
    private final String name;
    private final int duration;
    private final int priority;
    private final List<IRecipeInput> inputs;
    private final List<IRecipeOutput> outputs;
    private final List<ICondition> conditions;

    private ModularRecipe(Builder builder) {
        this.registryName = builder.registryName;
        this.recipeGroup = builder.recipeGroup;
        this.duration = builder.duration;
        this.priority = builder.priority;
        this.name = builder.name;
        this.inputs = Collections.unmodifiableList(new ArrayList<>(builder.inputs));
        this.outputs = Collections.unmodifiableList(new ArrayList<>(builder.outputs));
        this.conditions = Collections.unmodifiableList(new ArrayList<>(builder.conditions));
    }

    public String getRegistryName() {
        return registryName;
    }

    public String getRecipeGroup() {
        return recipeGroup;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getPriority() {
        return priority;
    }

    public List<IRecipeInput> getInputs() {
        return inputs;
    }

    public List<IRecipeOutput> getOutputs() {
        return outputs;
    }

    public List<ICondition> getConditions() {
        return conditions;
    }

    public boolean isConditionMet(ConditionContext context) {
        for (ICondition condition : conditions) {
            if (!condition.isMet(context)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all input requirements are met by the given ports.
     *
     * @param inputPorts List of input ports
     * @param simulate   If true, only check. If false, consume inputs.
     * @return true if all inputs are satisfied
     */
    public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {

        for (IRecipeInput input : inputs) {
            List<IModularPort> filtered = filterByType(inputPorts, input.getPortType());
            if (!input.process(filtered, true)) {
                return false;
            }
        }

        if (!simulate) {
            for (IRecipeInput input : inputs) {
                List<IModularPort> filtered = filterByType(inputPorts, input.getPortType());
                input.process(filtered, false);
            }
        }
        return true;
    }

    /**
     * Check if all outputs can be inserted into the given ports.
     *
     * @param outputPorts List of output ports
     * @param simulate    If true, only check. If false, produce outputs.
     * @return true if all outputs can be inserted
     */
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {

        for (IRecipeOutput output : outputs) {
            List<IModularPort> filtered = filterByType(outputPorts, output.getPortType());
            if (!output.checkCapacity(filtered)) {
                return false;
            }
        }

        if (!simulate) {
            for (IRecipeOutput output : outputs) {
                List<IModularPort> filtered = filterByType(outputPorts, output.getPortType());
                output.apply(filtered);
            }
        }
        return true;
    }

    public boolean matchesInput(List<IModularPort> inputPorts) {
        return processInputs(inputPorts, true);
    }

    public boolean canOutput(List<IModularPort> outputPorts) {
        return processOutputs(outputPorts, true);
    }

    @Override
    public IPortType.Type checkOutputCapacity(List<IModularPort> outputPorts) {
        for (IRecipeOutput output : outputs) {
            if (!output.checkCapacity(outputPorts)) {
                return output.getPortType();
            }
        }
        return null;
    }

    @Override
    public void onTick(ConditionContext context) {
        // Default implementation does nothing, decorators might override this
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    private List<IModularPort> filterByType(List<IModularPort> ports, IPortType.Type type) {
        List<IModularPort> filtered = new ArrayList<>();
        for (IModularPort port : ports) {
            if (port.getPortType() == type) {
                filtered.add(port);
            }
        }
        return filtered;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String registryName;
        private String recipeGroup;
        private String name;
        private int duration = 100;
        private int priority = 0;
        private List<IRecipeInput> inputs = new ArrayList<>();
        private List<IRecipeOutput> outputs = new ArrayList<>();

        public Builder registryName(String registryName) {
            this.registryName = registryName;
            return this;
        }

        public Builder recipeGroup(String recipeGroup) {
            this.recipeGroup = recipeGroup;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder addInput(IRecipeInput input) {
            this.inputs.add(input);
            return this;
        }

        public Builder addOutput(IRecipeOutput output) {
            this.outputs.add(output);
            return this;
        }

        private List<ICondition> conditions = new ArrayList<>();

        public Builder addCondition(ICondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public ModularRecipe build() {
            if (recipeGroup == null || recipeGroup.isEmpty()) {
                throw new IllegalStateException("Recipe recipeGroup is required");
            }
            return new ModularRecipe(this);
        }
    }
}
