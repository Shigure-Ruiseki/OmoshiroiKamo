package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Represents a recipe for a modular machine.
 * Recipes are immutable and created via the Builder pattern.
 */
public class ModularRecipe implements Comparable<ModularRecipe> {

    private final String recipeGroup;
    private final int duration;
    private final int priority;
    private final List<IRecipeInput> inputs;
    private final List<IRecipeOutput> outputs;

    private ModularRecipe(Builder builder) {
        this.recipeGroup = builder.recipeGroup;
        this.duration = builder.duration;
        this.priority = builder.priority;
        this.inputs = Collections.unmodifiableList(new ArrayList<>(builder.inputs));
        this.outputs = Collections.unmodifiableList(new ArrayList<>(builder.outputs));
    }

    // ========== Getters ==========

    public String getRecipeGroup() {
        return recipeGroup;
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

    // ========== Recipe Logic ==========

    /**
     * Check if all input requirements are met by the given ports.
     *
     * @param inputPorts List of input ports
     * @param simulate   If true, only check. If false, consume inputs.
     * @return true if all inputs are satisfied
     */
    public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {
        // First check all inputs can be satisfied
        for (IRecipeInput input : inputs) {
            List<IModularPort> filtered = filterByType(inputPorts, input.getPortType());
            if (!input.process(filtered, true)) {
                return false;
            }
        }
        // If not simulating, actually consume
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
        // First check all outputs can be inserted
        for (IRecipeOutput output : outputs) {
            List<IModularPort> filtered = filterByType(outputPorts, output.getPortType());
            if (!output.process(filtered, true)) {
                return false;
            }
        }
        // If not simulating, actually produce
        if (!simulate) {
            for (IRecipeOutput output : outputs) {
                List<IModularPort> filtered = filterByType(outputPorts, output.getPortType());
                output.process(filtered, false);
            }
        }
        return true;
    }

    /**
     * Check if all input requirements can be met (convenience method).
     */
    public boolean matchesInput(List<IModularPort> inputPorts) {
        return processInputs(inputPorts, true);
    }

    /**
     * Check if all outputs can be placed (convenience method).
     */
    public boolean canOutput(List<IModularPort> outputPorts) {
        return processOutputs(outputPorts, true);
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

    @Override
    public int compareTo(ModularRecipe other) {
        // 1. Higher priority comes first (descending order)
        int priorityCompare = Integer.compare(other.priority, this.priority);
        if (priorityCompare != 0) return priorityCompare;

        // 2. More input types comes first (descending order)
        int thisInputTypes = countDistinctInputTypes();
        int otherInputTypes = other.countDistinctInputTypes();
        int inputTypeCompare = Integer.compare(otherInputTypes, thisInputTypes);
        if (inputTypeCompare != 0) return inputTypeCompare;

        // 3. Larger item stack count comes first (descending order)
        int thisItemCount = getTotalItemInputCount();
        int otherItemCount = other.getTotalItemInputCount();
        return Integer.compare(otherItemCount, thisItemCount);
    }

    /**
     * Count distinct input port types (ITEM, FLUID, GAS, etc.)
     */
    private int countDistinctInputTypes() {
        java.util.Set<IPortType.Type> types = new java.util.HashSet<>();
        for (IRecipeInput input : inputs) {
            types.add(input.getPortType());
        }
        return types.size();
    }

    /**
     * Get total item input stack count requirement.
     */
    private int getTotalItemInputCount() {
        int total = 0;
        for (IRecipeInput input : inputs) {
            if (input instanceof ItemInput) {
                total += ((ItemInput) input).getRequired().stackSize;
            }
        }
        return total;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String recipeGroup;
        private int duration = 100;
        private int priority = 0;
        private List<IRecipeInput> inputs = new ArrayList<>();
        private List<IRecipeOutput> outputs = new ArrayList<>();

        public Builder recipeGroup(String recipeGroup) {
            this.recipeGroup = recipeGroup;
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

        public ModularRecipe build() {
            if (recipeGroup == null || recipeGroup.isEmpty()) {
                throw new IllegalStateException("Recipe recipeGroup is required");
            }
            if (inputs.isEmpty()) {
                throw new IllegalStateException("Recipe must have at least one input");
            }
            if (outputs.isEmpty()) {
                throw new IllegalStateException("Recipe must have at least one output");
            }
            return new ModularRecipe(this);
        }
    }
}
