package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.FeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemFeedingUpgrade extends ItemUpgrade<FeedingUpgradeWrapper> {

    public ItemFeedingUpgrade() {
        super(ModObject.itemFeedingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("feeding_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "feeding_upgrade"));
    }

    @Override
    public FeedingUpgradeWrapper createWrapper(ItemStack stack) {
        return new FeedingUpgradeWrapper(stack);
    }
}
