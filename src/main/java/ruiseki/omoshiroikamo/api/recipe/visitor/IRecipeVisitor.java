package ruiseki.omoshiroikamo.api.recipe.visitor;

import ruiseki.omoshiroikamo.api.recipe.core.IRecipe;
import ruiseki.omoshiroikamo.api.recipe.io.BlockInput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockNbtInput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockNbtOutput;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EnergyInput;
import ruiseki.omoshiroikamo.api.recipe.io.EnergyOutput;
import ruiseki.omoshiroikamo.api.recipe.io.EssentiaInput;
import ruiseki.omoshiroikamo.api.recipe.io.EssentiaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.FluidInput;
import ruiseki.omoshiroikamo.api.recipe.io.FluidOutput;
import ruiseki.omoshiroikamo.api.recipe.io.GasInput;
import ruiseki.omoshiroikamo.api.recipe.io.GasOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemInput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ManaInput;
import ruiseki.omoshiroikamo.api.recipe.io.ManaOutput;
import ruiseki.omoshiroikamo.api.recipe.io.VisInput;
import ruiseki.omoshiroikamo.api.recipe.io.VisOutput;

/**
 * Interface for visiting recipe elements.
 * Implementations define operations to be performed on recipe components.
 */
public interface IRecipeVisitor {

    /**
     * Visit the entire recipe.
     * Default implementation visits all inputs and outputs.
     */
    default void visit(IRecipe recipe) {
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

    default void visit(BlockInput input) {}

    default void visit(BlockNbtInput input) {}

    /**
     * Fallback for unknown input types.
     */
    default void visit(IRecipeInput input) {}

    // --- Outputs ---

    default void visit(ItemOutput output) {}

    default void visit(FluidOutput output) {}

    default void visit(EnergyOutput output) {}

    default void visit(EssentiaOutput output) {}

    default void visit(GasOutput output) {}

    default void visit(ManaOutput output) {}

    default void visit(VisOutput output) {}

    default void visit(BlockOutput output) {}

    default void visit(BlockNbtOutput output) {}

    /**
     * Fallback for unknown output types.
     */
    default void visit(IRecipeOutput output) {}
}
