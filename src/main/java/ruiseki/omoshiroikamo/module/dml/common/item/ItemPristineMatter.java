package ruiseki.omoshiroikamo.module.dml.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (model != null) {
            list.add(
                ChatFormatting.AQUA + LibMisc.LANG.localize("tooltip.pristine_matter.loot_items")
                    + ChatFormatting.RESET);
            for (ItemStack lootStack : model.getLootItems()) {
                list.add(lootStack.getDisplayName());
            }
        }
    }
}
