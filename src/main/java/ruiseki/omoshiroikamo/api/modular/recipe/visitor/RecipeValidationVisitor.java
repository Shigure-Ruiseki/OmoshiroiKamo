package ruiseki.omoshiroikamo.api.modular.recipe.visitor;

import java.util.ArrayList;
import java.util.List;

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
 * Visitor to validate the integrity of a recipe.
 * Checks for negative amounts, missing data, etc.
 */
public class RecipeValidationVisitor implements IRecipeVisitor {

    private final List<String> errors = new ArrayList<>();
    private String currentRecipeName = "Unknown";

    public List<String> getErrors() {
        return errors;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    private void addError(String message) {
        errors.add(String.format("[%s] %s", currentRecipeName, message));
    }

    @Override
    public void visit(IModularRecipe recipe) {
        this.currentRecipeName = recipe.getRegistryName() != null ? recipe.getRegistryName() : recipe.getName();

        if (recipe.getDuration() <= 0) {
            addError("Recipe duration must be greater than 0.");
        }

        // Delegate to inputs and outputs
        IRecipeVisitor.super.visit(recipe);
    }

    @Override
    public void visit(ItemInput input) {
        if (input.getRequiredAmount() <= 0) {
            addError("Item input amount must be greater than 0.");
        }
        if (!input.validate()) {
            addError("Item input is missing required data (item or ore).");
        }
    }

    @Override
    public void visit(FluidInput input) {
        if (input.getRequiredAmount() <= 0) {
            addError("Fluid input amount must be greater than 0.");
        }
        if (!input.validate()) {
            addError("Fluid input is missing required data.");
        }
    }

    @Override
    public void visit(EnergyInput input) {
        if (input.getAmount() <= 0) {
            addError("Energy input amount must be greater than 0.");
        }
    }

    @Override
    public void visit(EssentiaInput input) {
        if (input.getRequiredAmount() <= 0) {
            addError("Essentia input amount must be greater than 0.");
        }
    }

    @Override
    public void visit(GasInput input) {
        if (input.getRequiredAmount() <= 0) {
            addError("Gas input amount must be greater than 0.");
        }
    }

    @Override
    public void visit(ManaInput input) {
        if (input.getAmount() <= 0) {
            addError("Mana input amount must be greater than 0.");
        }
    }

    @Override
    public void visit(VisInput input) {
        if (input.getRequiredAmount() <= 0) {
            addError("Vis input amount must be greater than 0.");
        }
    }

    @Override
    public void visit(ItemOutput output) {
        if (output.getRequiredAmount() <= 0) {
            addError("Item output amount must be greater than 0.");
        }
        if (!output.validate()) {
            addError("Item output is missing required data.");
        }
    }

    @Override
    public void visit(FluidOutput output) {
        if (output.getRequiredAmount() <= 0) {
            addError("Fluid output amount must be greater than 0.");
        }
        if (!output.validate()) {
            addError("Fluid output is missing required data.");
        }
    }

    @Override
    public void visit(EnergyOutput output) {
        if (output.getAmount() <= 0) {
            addError("Energy output amount must be greater than 0.");
        }
    }

    @Override
    public void visit(EssentiaOutput output) {
        if (output.getAmount() <= 0) {
            addError("Essentia output amount must be greater than 0.");
        }
    }

    @Override
    public void visit(GasOutput output) {
        if (output.getAmount() <= 0) {
            addError("Gas output amount must be greater than 0.");
        }
    }

    @Override
    public void visit(ManaOutput output) {
        if (output.getAmount() <= 0) {
            addError("Mana output amount must be greater than 0.");
        }
    }

    @Override
    public void visit(VisOutput output) {
        if (output.getAmountCentiVis() <= 0) {
            addError("Vis output amount must be greater than 0.");
        }
    }
}
