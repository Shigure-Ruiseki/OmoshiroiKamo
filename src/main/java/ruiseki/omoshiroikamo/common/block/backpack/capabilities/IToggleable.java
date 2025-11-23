package ruiseki.omoshiroikamo.common.block.backpack.capabilities;

import net.minecraft.item.ItemStack;

public interface IToggleable {

    String ENABLED_TAG = "Enabled";

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default void toggle(ItemStack stack) {
        setEnabled(!isEnabled());
    }

    class Impl implements IToggleable {

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {

        }
    }
}
