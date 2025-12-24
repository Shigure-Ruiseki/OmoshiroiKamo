package ruiseki.omoshiroikamo.module.backpack.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.UpgradeWrapper;

public class ItemEverlastingUpgrade extends ItemUpgrade<UpgradeWrapper> {

    public ItemEverlastingUpgrade() {
        super(ModObject.itemEverlastingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("everlasting_upgrade");
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "backpack.everlasting_upgrade"));
    }
}
