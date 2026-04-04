package ruiseki.omoshiroikamo.module.storage.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.storage.IStorageWrapper;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.SmeltingUpgradeWrapper;

public class ItemSmeltingUpgrade extends ItemUpgrade<SmeltingUpgradeWrapper> {

    public ItemSmeltingUpgrade() {
        super(ModObject.STORAGE_SMELTING_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("storage/smelting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "storage.smelting_upgrade"));
    }

    @Override
    public SmeltingUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper wrapper) {
        return new SmeltingUpgradeWrapper(stack, wrapper);
    }
}
