package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemStack;

public interface IToggleable {

    String ENABLED_TAG = "Enabled";

    boolean isEnabled(ItemStack stack);

    void setEnabled(ItemStack stack, boolean enabled);

    default void toggle(ItemStack stack) {
        setEnabled(stack, !isEnabled(stack));
    }

    class Impl implements IToggleable {

        @Override
        public boolean isEnabled(ItemStack stack) {
            return false;
        }

        @Override
        public void setEnabled(ItemStack stack, boolean enabled) {

        }
    }
}
