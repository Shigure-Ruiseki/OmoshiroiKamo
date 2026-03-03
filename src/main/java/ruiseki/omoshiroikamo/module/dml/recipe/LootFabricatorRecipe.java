package ruiseki.omoshiroikamo.module.dml.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.recipe.core.IRecipe;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.ItemOutput;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;

public class LootFabricatorRecipe implements IRecipe {

    private final ModelRegistryItem model;
    private final List<IRecipeInput> inputs = new ArrayList<>();
    private final List<IRecipeOutput> outputs = new ArrayList<>();

    public LootFabricatorRecipe(ModelRegistryItem model) {
        this.model = model;
        // PristineMatterInput added here
        PristineMatterInput input = new PristineMatterInput(model.getId());
        // In reality, this would be initialized with modelId
        this.inputs.add(input);

        // Setup outputs
        if (model.getLootItems() != null) {
            for (ItemStack loot : model.getLootItems()) {
                this.outputs.add(new ItemOutput(loot));
            }
        }
    }

    @Override
    public String getRegistryName() {
        return "dml:fabrication_" + model.getDisplayName()
            .toLowerCase();
    }

    @Override
    public String getRecipeGroup() {
        return "loot_fabricator";
    }

    @Override
    public String getName() {
        return model.getDisplayName();
    }

    @Override
    public int getDuration() {
        return 100;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public List<IRecipeInput> getInputs() {
        return inputs;
    }

    @Override
    public List<IRecipeOutput> getOutputs() {
        return outputs;
    }

    @Override
    public List<ICondition> getConditions() {
        return new ArrayList<>();
    }

    @Override
    public boolean isConditionMet(ConditionContext context) {
        return true;
    }

    @Override
    public void onTick(ConditionContext context) {}

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit((IRecipe) this);
    }

    @Override
    public int compareTo(IRecipe o) {
        return 0;
    }
}
