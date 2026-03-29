package ruiseki.omoshiroikamo.module.storage.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.PickupUpgradeWrapper;

public class ItemPickupUpgrade extends ItemUpgrade<PickupUpgradeWrapper> {

    public ItemPickupUpgrade() {
        super(ModObject.STORAGE_PICKUP_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("storage/pickup_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "storage.pickup_upgrade"));
    }

    @Override
    public PickupUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper wrapper) {
        return new PickupUpgradeWrapper(stack, wrapper);
    }
}
