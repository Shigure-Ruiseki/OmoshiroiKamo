package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemAdvancedFilterUpgrade extends ItemUpgrade<AdvancedFilterUpgradeWrapper> {

    public ItemAdvancedFilterUpgrade() {
        super(ModObject.itemAdvancedFilterUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("advanced_filter_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "advanced_filter_upgrade"));
    }

    @Override
    public AdvancedFilterUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedFilterUpgradeWrapper(stack);
    }
}
