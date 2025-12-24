package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityColoredEgg;

public class ItemColoredEgg extends ItemOK {

    public ItemColoredEgg() {
        super(ModObject.itemColoredEgg.unlocalisedName);
        setMaxStackSize(16);
        setMaxDamage(0);
        setTextureName("colored_egg");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        EnumDye color = EnumDye.fromIndex(stack.getItemDamage());
        String unlocalizedName = color.getName();
        if (unlocalizedName.equals("silver")) {
            unlocalizedName += "Dye";
        }
        return LibMisc.LANG.localize(getUnlocalizedName() + "." + unlocalizedName + ".name");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (ChickensRegistryItem chicken : ChickensRegistry.INSTANCE.getItems()) {
            if (chicken.isDye()) {
                list.add(new ItemStack(item, 1, chicken.getDyeMetadata()));
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return EnumDye.fromIndex(stack.getItemDamage())
            .getColor();
    }

    public int getChickenType(ItemStack itemStack) {
        ChickensRegistryItem chicken = ChickensRegistry.INSTANCE.findDyeChicken(itemStack.getItemDamage());
        if (chicken == null) {
            return -1;
        }
        return chicken.getId();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();
        builder.addLang(LibResources.TOOLTIP + "colored_egg.l1");
        list.addAll(builder.build());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }
        worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            int chickenType = getChickenType(itemStackIn);

            if (chickenType != -1) {
                EntityColoredEgg eggEntity = new EntityColoredEgg(worldIn, player);
                eggEntity.setChickenTypeInternal(chickenType);
                worldIn.spawnEntityInWorld(eggEntity);
            }
        }
        return itemStackIn;
    }
}
