package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.crafting;

import java.util.List;

public interface ICraftingPattern {

    List<IngredientStack> getOutputs();

    List<IngredientStack> getInputs();
}
