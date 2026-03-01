package ruiseki.omoshiroikamo.api.modular.recipe.visitor;

import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EnergyOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EssentiaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.EssentiaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.FluidInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.FluidOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.GasInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.GasOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ItemOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ManaInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.ManaOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.VisInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.VisOutput;

/**
 * Interface for visiting recipe elements.
 * Implementations define operations to be performed on recipe components.
 */
public interface IRecipeVisitor {

    /**
     * Visit the entire recipe.
     * Default implementation visits all inputs and outputs.
     */
    default void visit(IModularRecipe recipe) {
        if (recipe.getInputs() != null) {
            recipe.getInputs()
                .forEach(input -> input.accept(this));
        }
        if (recipe.getOutputs() != null) {
            recipe.getOutputs()
                .forEach(output -> output.accept(this));
        }
    }

    // --- Inputs ---

    default void visit(ItemInput input) {}

    default void visit(FluidInput input) {}

    default void visit(EnergyInput input) {}

    default void visit(EssentiaInput input) {}

    default void visit(GasInput input) {}

    default void visit(ManaInput input) {}

    default void visit(VisInput input) {}

    // --- Outputs ---

    default void visit(ItemOutput output) {}

    default void visit(FluidOutput output) {}

    default void visit(EnergyOutput output) {}

    default void visit(EssentiaOutput output) {}

    default void visit(GasOutput output) {}

    default void visit(ManaOutput output) {}

    default void visit(VisOutput output) {}
}
