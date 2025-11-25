package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.MagnetUpgradeWrapper;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemMagnetUpgrade extends ItemUpgrade<MagnetUpgradeWrapper> {

    public ItemMagnetUpgrade() {
        super(ModObject.itemMagnetUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("magnet_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "magnet_upgrade"));
    }

    @Override
    public MagnetUpgradeWrapper createWrapper(ItemStack stack) {
        return new MagnetUpgradeWrapper(stack);
    }
}
