package ruiseki.omoshiroikamo.common.item.dml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.model.LivingRegistry;
import ruiseki.omoshiroikamo.api.entity.model.LivingRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ItemLivingMatter extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemLivingMatter() {
        super(ModObject.itemLivingMatter.unlocalisedName);
        setMaxStackSize(64);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (LivingRegistryItem model : LivingRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(this, 1, model.getId()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        LivingRegistryItem model = LivingRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (model == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(model.getItemName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (LivingRegistryItem model : LivingRegistry.INSTANCE.getItems()) {
            int type = model.getId();
            String iconName = model.getTexture();
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
        TooltipUtils builder = TooltipUtils.builder();
        LivingRegistryItem item = LivingRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (item != null) {
            builder.addLang("tooltip.living_matter.consume_for_xp", KeyboardUtils.getUseKeyName());
            builder.addLang("tooltip.living_matter.consume_stack", KeyboardUtils.getSneakKeyName());
            builder.addLang("tooltip.living_matter.xp", item.getXpValue());
        }

        list.addAll(builder.build());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            LivingRegistryItem item = LivingRegistry.INSTANCE.getByType(stack.getItemDamage());
            if (item == null) return stack;

            boolean consumeAll = KeyboardUtils.isHoldingShift();
            int consumeCount = consumeAll ? stack.stackSize : 1;

            int xp = item.getXpValue() * consumeCount;
            player.addExperience(xp);

            world.playSoundAtEntity(player, "random.orb", 0.3F, world.rand.nextFloat() * 0.4F + 0.8F);

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize -= consumeCount;
            }
        }

        return stack;
    }

}
