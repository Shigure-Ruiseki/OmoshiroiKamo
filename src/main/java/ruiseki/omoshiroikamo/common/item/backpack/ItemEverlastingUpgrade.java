package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemEverlastingUpgrade extends ItemUpgrade {

    public ItemEverlastingUpgrade() {
        super(ModObject.itemEverlastingUpgrade.unlocalisedName);
        setMaxStackSize(1);
        setTextureName("everlasting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "everlasting_upgrade"));
        list.add(
            ItemNBTUtils.getNBT(itemstack)
                .toString());
    }
}
