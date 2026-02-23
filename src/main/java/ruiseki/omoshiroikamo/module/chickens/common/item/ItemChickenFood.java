package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class ItemChickenFood extends ItemOK {

    // Add NEI compat
    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemChickenFood() {
        super(ModObject.itemChickenFood);
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (DataChicken chicken : DataChicken.getAllChickens()) {
            list.add(new ItemStack(this, 1, chicken.getId()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        ChickensRegistryItem chicken = ChickensRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (chicken == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG
            .localizeExact("item.chickenFood.name", LibMisc.LANG.localizeExact(chicken.getDisplayName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon(LibMisc.MOD_ID + ":chicken_food");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken != null) {
            return chicken.getItem()
                .getBgColor(); // Use BG color of the chicken for the food tint
        }
        return super.getColorFromItemStack(stack, pass);
    }
}
