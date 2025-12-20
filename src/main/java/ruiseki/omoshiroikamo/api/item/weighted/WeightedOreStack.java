package ruiseki.omoshiroikamo.api.item.weighted;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class WeightedOreStack extends WeightedStackBase {

    private final List<ItemStack> stacks;
    private final String oreName;

    public WeightedOreStack(String oreName, double weight) {
        this(oreName, weight, weight);
    }

    public WeightedOreStack(String oreName, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.oreName = oreName;
        this.stacks = OreDictionary.getOres(oreName);
    }

    protected WeightedOreStack(String oreName, List<ItemStack> stacks, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.oreName = oreName;
        this.stacks = stacks;
    }

    @Override
    public boolean isStackEqual(ItemStack stack) {
        return this.stacks != null && !this.stacks.isEmpty() && ((ItemStack) this.stacks.get(0)).isItemEqual(stack);
    }

    @Override
    public ItemStack getMainStack() {
        return this.stacks != null && !this.stacks.isEmpty() ? (ItemStack) this.stacks.get(0) : null;
    }

    @Override
    public WeightedStackBase copy() {
        List<ItemStack> newStacks = new ArrayList<ItemStack>();

        if (this.stacks != null) {
            for (ItemStack itemStack : this.stacks) {
                newStacks.add(itemStack.copy());
            }
        }

        return new WeightedOreStack(this.oreName, newStacks, this.realWeight, this.realFocusedWeight);
    }
}
