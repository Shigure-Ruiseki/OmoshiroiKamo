package ruiseki.omoshiroikamo.module.storage.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedFilterUpgradeWrapper;

public class ItemAdvancedFilterUpgrade extends ItemUpgrade<AdvancedFilterUpgradeWrapper> {

    public ItemAdvancedFilterUpgrade() {
        super(ModObject.STORAGE_FEEDING_UPGRADE.name);
        setMaxStackSize(1);
        setTextureName("storage/advanced_filter_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LangHelpers.localize(LibResources.TOOLTIP + "storage.advanced_filter_upgrade"));
    }

    @Override
    public AdvancedFilterUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedFilterUpgradeWrapper(stack);
    }
}
