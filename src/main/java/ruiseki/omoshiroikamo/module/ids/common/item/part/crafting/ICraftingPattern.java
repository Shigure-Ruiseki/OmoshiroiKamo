package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting;

import java.util.List;

public interface ICraftingPattern {

    List<IngredientStack> getOutputs();

    List<IngredientStack> getInputs();
}
