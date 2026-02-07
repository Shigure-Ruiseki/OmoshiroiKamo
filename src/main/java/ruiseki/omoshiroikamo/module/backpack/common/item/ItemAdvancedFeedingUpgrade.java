package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedFeedingUpgradeWrapper;

public class ItemAdvancedFeedingUpgrade extends ItemUpgrade<AdvancedFeedingUpgradeWrapper> {

    public ItemAdvancedFeedingUpgrade() {
        super(ModObject.itemAdvancedFeedingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("backpack/advanced_feeding_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.advanced_feeding_upgrade"));
    }

    @Override
    public AdvancedFeedingUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedFeedingUpgradeWrapper(stack);
    }
}
