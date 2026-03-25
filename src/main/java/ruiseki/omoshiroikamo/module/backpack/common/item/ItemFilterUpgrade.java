package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.FilterUpgradeWrapper;

public class ItemFilterUpgrade extends ItemUpgrade<FilterUpgradeWrapper> {

    public ItemFilterUpgrade() {
        super(ModObject.BACKPACK_FILTER_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("backpack/filter_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "backpack.filter_upgrade"));
    }

    @Override
    public FilterUpgradeWrapper createWrapper(ItemStack stack) {
        return new FilterUpgradeWrapper(stack);
    }
}
