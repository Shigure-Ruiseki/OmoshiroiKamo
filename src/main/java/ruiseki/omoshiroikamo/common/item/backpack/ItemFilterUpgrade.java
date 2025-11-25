package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.FilterUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

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
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "filter_upgrade"));
    }

    @Override
    public FilterUpgradeWrapper createWrapper(ItemStack stack) {
        return new FilterUpgradeWrapper(stack);
    }
}
