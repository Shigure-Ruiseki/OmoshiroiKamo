package ruiseki.omoshiroikamo.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.OKCreativeTab;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemOK extends Item implements IAdvancedTooltipProvider {

    protected String name;

    public ItemOK() {
        setCreativeTab(OKCreativeTab.INSTANCE);
        setHasSubtypes(true);
    }

    public ItemOK(String name) {
        this();
        this.name = name;
        setUnlocalizedName(name);
    }

    public ItemOK(ModObject modObject) {
        this(modObject.unlocalisedName);
    }

    public void init() {
        GameRegistry.registerItem(this, name);
    }

    public ItemOK setName(String name) {
        this.name = name;
        setUnlocalizedName(name);
        return this;
    }

    public ItemOK setName(ModObject modObject) {
        this.setName(modObject.unlocalisedName);
        return this;
    }

    @Override
    public ItemOK setTextureName(String textureName) {
        this.iconString = textureName;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getTextureName() {
        return iconString;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        if (getTextureName() != null) {
            itemIcon = register.registerIcon(LibResources.PREFIX_MOD + getTextureName());
        } else {
            itemIcon = register.registerIcon(LibResources.PREFIX_MOD + getName());
        }
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {}

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {}

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {}
}
