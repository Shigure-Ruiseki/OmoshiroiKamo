package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.FilterUpgradeWrapper;

public class ItemFilterUpgrade extends ItemUpgrade<FilterUpgradeWrapper> {

    public ItemFilterUpgrade() {
        super(ModObject.itemFilterUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("filter_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.filter_upgrade"));
    }

    @Override
    public FilterUpgradeWrapper createWrapper(ItemStack stack) {
        return new FilterUpgradeWrapper(stack);
    }
}
