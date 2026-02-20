package ruiseki.omoshiroikamo.core.item.weighted;

import net.minecraft.item.ItemStack;

public class WeightedItemStack extends WeightedStackBase {

    private final ItemStack stack;

    public WeightedItemStack(ItemStack stack, double weight) {
        super(weight, weight); // Default: focused weight = normal weight
        this.stack = stack;
    }

    public WeightedItemStack(ItemStack stack, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.stack = stack;
    }

    @Override
    public boolean isStackEqual(ItemStack other) {
        return this.stack != null && other != null && this.stack.isItemEqual(other);
    }

    @Override
    public ItemStack getMainStack() {
        return this.stack;
    }

    @Override
    public WeightedStackBase copy() {
        return new WeightedItemStack(this.stack.copy(), this.realWeight, this.realFocusedWeight);
    }
}
