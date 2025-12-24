package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.PickupUpgradeWrapper;

public class ItemPickupUpgrade extends ItemUpgrade<PickupUpgradeWrapper> {

    public ItemPickupUpgrade() {
        super(ModObject.itemPickupUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("pickup_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.pickup_upgrade"));
    }

    @Override
    public PickupUpgradeWrapper createWrapper(ItemStack stack) {
        return new PickupUpgradeWrapper(stack);
    }
}
