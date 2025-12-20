package ruiseki.omoshiroikamo.api.item.weighted;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public abstract class WeightedStackBase extends WeightedRandom.Item {

    public double realWeight;
    public double realFocusedWeight;

    public WeightedStackBase(double weight, double focusedWeight) {
        super((int) (weight * 100));
        this.realWeight = weight;
        this.realFocusedWeight = focusedWeight;
    }

    public int getWeight() {
        return this.itemWeight;
    }

    public abstract boolean isStackEqual(ItemStack stack);

    public abstract ItemStack getMainStack();

    public abstract WeightedStackBase copy();

}
