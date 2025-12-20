package ruiseki.omoshiroikamo.common.item.deepMobLearning;

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
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ItemPristineMatter extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemPristineMatter() {
        super(ModObject.itemPristineMatter.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return true;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(this, 1, model.getId()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (model == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(model.getPristineName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            int type = model.getId();
            String iconName = model.getPristineTexture();
            IIcon icon = reg.registerIcon(iconName);
            icons.put(type, icon);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return icons.get(meta);
    }
}
