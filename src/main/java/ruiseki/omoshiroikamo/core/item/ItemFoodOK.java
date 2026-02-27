package ruiseki.omoshiroikamo.core.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ItemFoodOK extends ItemFood implements IItem {

    protected String name;

    public ItemFoodOK(String name, int healAmount, float saturationModifier, boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.name = name;
        setUnlocalizedName(name);
    }

    @Override
    public void init() {
        GameRegistry.registerItem(this, name);
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(LibResources.PREFIX_MOD + this.getIconString());
    }
}
