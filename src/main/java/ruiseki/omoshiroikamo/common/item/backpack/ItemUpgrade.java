package ruiseki.omoshiroikamo.common.item.backpack;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemUpgrade extends ItemOK {

    public static ItemUpgrade create() {
        return new ItemUpgrade();
    }

    public ItemUpgrade(String name) {
        super(name);
    }

    public ItemUpgrade() {
        this(ModObject.itemUpgrade.unlocalisedName);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibResources.PREFIX_MOD + "upgrade_base");
    }

    public boolean hasTab() {
        return false;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "upgrade_base"));
    }
}
