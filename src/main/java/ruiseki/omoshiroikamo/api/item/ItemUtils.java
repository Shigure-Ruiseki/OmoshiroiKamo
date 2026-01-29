package ruiseki.omoshiroikamo.api.item;

import java.util.Objects;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

public class ItemUtils extends ItemUtil {

    public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
        if (s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
            return false;
        }
        if (!s1.isItemEqual(s2)) {
            return false;
        }
        return areStacksEqual(s1, s2);
    }

    public static boolean areItemsEqualIgnoreDurability(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return false;
        }
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }
        return Objects.equals(stack1.getTagCompound(), stack2.getTagCompound());
    }

    public static ItemStack merge(ItemStack a, ItemStack b) {
        if (a == null) return b;
        a.stackSize += b.stackSize;
        return a;
    }
}
