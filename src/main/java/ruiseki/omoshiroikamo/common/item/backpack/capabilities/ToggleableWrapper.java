package ruiseki.omoshiroikamo.common.item.backpack.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;

public class ToggleableWrapper extends UpgradeWrapper implements IToggleable {

    public ToggleableWrapper(ItemStack upgrade) {
        super(upgrade);
    }

    public boolean isEnabled() {
        return upgrade.hasTagCompound() && upgrade.getTagCompound()
            .getBoolean(ENABLED_TAG);
    }

    public void setEnabled(boolean enabled) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(upgrade);
        tag.setBoolean(IToggleable.ENABLED_TAG, enabled);
    }

    public void toggle() {
        setEnabled(!isEnabled());
    }
}
