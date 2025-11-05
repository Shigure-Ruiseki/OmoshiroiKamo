package ruiseki.omoshiroikamo.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public abstract class WeightedStackBase extends WeightedRandom.Item {

    public WeightedStackBase(int itemWeightIn) {
        super(itemWeightIn);
    }

    public int getWeight() {
        return this.itemWeight;
    }

    public abstract boolean isStackEqual(ItemStack stack);

    public abstract ItemStack getMainStack();

    public abstract WeightedStackBase copy();

}
