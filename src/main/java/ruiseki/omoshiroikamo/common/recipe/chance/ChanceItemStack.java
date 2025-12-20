package ruiseki.omoshiroikamo.common.recipe.chance;

import net.minecraft.item.ItemStack;

public class ChanceItemStack {

    public final ItemStack stack;
    public final float chance; // 0.0f to 1.0f

    public ChanceItemStack(ItemStack stack, float chance) {
        this.stack = stack;
        this.chance = chance;
    }
}
