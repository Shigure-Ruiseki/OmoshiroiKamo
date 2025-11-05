package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemFeedingUpgrade extends ItemUpgrade {

    public static ItemFeedingUpgrade create() {
        return new ItemFeedingUpgrade();
    }

    public ItemFeedingUpgrade() {
        super(ModObject.itemFeedingUpgrade.unlocalisedName);
        setNoRepair();
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibResources.PREFIX_MOD + "feeding_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "feeding_upgrade"));
    }
}
