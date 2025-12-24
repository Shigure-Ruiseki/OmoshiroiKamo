package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedPickupUpgradeWrapper;

public class ItemAdvancedPickupUpgrade extends ItemUpgrade<AdvancedPickupUpgradeWrapper> {

    public ItemAdvancedPickupUpgrade() {
        super(ModObject.itemAdvancedPickupUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("advanced_pickup_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.advanced_pickup_upgrade"));
    }

    @Override
    public AdvancedPickupUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedPickupUpgradeWrapper(stack);
    }
}
