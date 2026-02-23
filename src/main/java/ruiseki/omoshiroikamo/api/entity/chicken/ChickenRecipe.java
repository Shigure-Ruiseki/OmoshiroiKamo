package ruiseki.omoshiroikamo.api.entity.chicken;

import net.minecraft.item.ItemStack;

import lombok.Getter;

@Getter
public class ChickenRecipe {

    private final ItemStack input;
    private final ItemStack output;

    public ChickenRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public boolean matches(ItemStack stack) {
        if (stack == null || input == null) {
            return false;
        }
        return stack.getItem() == input.getItem()
            && (input.getItemDamage() == 32767 || input.getItemDamage() == stack.getItemDamage());
    }
}
