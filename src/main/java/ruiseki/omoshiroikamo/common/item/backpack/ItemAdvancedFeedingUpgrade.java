package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFeedingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemAdvancedFeedingUpgrade extends ItemUpgrade<AdvancedFeedingUpgradeWrapper> {

    public ItemAdvancedFeedingUpgrade() {
        super(ModObject.itemAdvancedFeedingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("advanced_feeding_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "advanced_feeding_upgrade"));
    }

    @Override
    public AdvancedFeedingUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedFeedingUpgradeWrapper(stack);
    }
}
