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

public class ItemCraftingUpgrade extends ItemUpgrade {

    public static ItemCraftingUpgrade create() {
        return new ItemCraftingUpgrade();
    }

    public ItemCraftingUpgrade() {
        super(ModObject.itemCraftingUpgrade.unlocalisedName);
        setNoRepair();
        setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister reg) {
        itemIcon = reg.registerIcon(LibResources.PREFIX_MOD + "crafting_upgrade");
    }

    @Override
    public boolean hasTab() {
        return true;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "crafting_upgrade"));
    }
}
