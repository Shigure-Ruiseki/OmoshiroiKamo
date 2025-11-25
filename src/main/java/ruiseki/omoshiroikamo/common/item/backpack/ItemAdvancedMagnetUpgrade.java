package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedMagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemAdvancedMagnetUpgrade extends ItemUpgrade<AdvancedMagnetUpgradeWrapper> {

    public ItemAdvancedMagnetUpgrade() {
        super(ModObject.itemAdvancedMagnetUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("advanced_magnet_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "advanced_magnet_upgrade"));
    }

    @Override
    public AdvancedMagnetUpgradeWrapper createWrapper(ItemStack stack) {
        return new AdvancedMagnetUpgradeWrapper(stack);
    }
}
