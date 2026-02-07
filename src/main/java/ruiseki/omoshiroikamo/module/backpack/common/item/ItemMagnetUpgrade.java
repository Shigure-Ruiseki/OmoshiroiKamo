package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.MagnetUpgradeWrapper;

public class ItemMagnetUpgrade extends ItemUpgrade<MagnetUpgradeWrapper> {

    public ItemMagnetUpgrade() {
        super(ModObject.itemMagnetUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("backpack/magnet_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.magnet_upgrade"));
    }

    @Override
    public MagnetUpgradeWrapper createWrapper(ItemStack stack) {
        return new MagnetUpgradeWrapper(stack);
    }
}
