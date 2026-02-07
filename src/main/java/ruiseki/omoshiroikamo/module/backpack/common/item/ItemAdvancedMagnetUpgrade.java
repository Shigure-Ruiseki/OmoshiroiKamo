package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedMagnetUpgradeWrapper;

public class ItemAdvancedMagnetUpgrade extends ItemUpgrade<AdvancedMagnetUpgradeWrapper> {

    public ItemAdvancedMagnetUpgrade() {
        super(ModObject.itemAdvancedMagnetUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("backpack/advanced_magnet_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.advanced_magnet_upgrade"));
    }

    @Override
    public AdvancedMagnetUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedMagnetUpgradeWrapper(stack);
    }
}
