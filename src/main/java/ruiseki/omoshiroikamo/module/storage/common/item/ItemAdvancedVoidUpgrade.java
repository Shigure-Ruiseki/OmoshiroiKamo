package ruiseki.omoshiroikamo.module.storage.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.inventory.IStorageWrapper;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedVoidUpgradeWrapper;

public class ItemAdvancedVoidUpgrade extends ItemUpgrade<AdvancedVoidUpgradeWrapper> {

    public ItemAdvancedVoidUpgrade() {
        super(ModObject.STORAGE_ADVANCED_VOID_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("storage/advanced_void_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "storage.advanced_void_upgrade"));
    }

    @Override
    public AdvancedVoidUpgradeWrapper createWrapper(ItemStack stack, IStorageWrapper wrapper) {
        return new AdvancedVoidUpgradeWrapper(stack, wrapper);
    }
}
