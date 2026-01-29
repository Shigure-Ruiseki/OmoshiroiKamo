package ruiseki.omoshiroikamo.core.common.creative;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.ICreativeTabModule;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class OKCreativeTab extends CreativeTabs {

    private ItemStack icon;
    private final List<ICreativeTabModule> modules = new ArrayList<>();

    public OKCreativeTab(String label) {
        super(label);
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void registerModule(ICreativeTabModule module) {
        modules.add(module);
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> list) {
        list.clear();
        for (ICreativeTabModule module : modules) {
            module.fillTab(this, list);
        }
    }

    @Override
    public Item getTabIconItem() {
        return icon != null ? icon.getItem() : Item.getItemById(1);
    }

    @Override
    public ItemStack getIconItemStack() {
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return LibMisc.LANG.localize("creativetab." + getTabLabel());
    }
}
