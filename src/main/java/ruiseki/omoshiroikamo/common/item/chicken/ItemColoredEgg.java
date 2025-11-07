package ruiseki.omoshiroikamo.common.item.chicken;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.enderio.core.common.util.DyeColor;

import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityColoredEgg;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class ItemColoredEgg extends ItemOK {

    public ItemColoredEgg() {
        super(ModObject.itemColoredEgg.unlocalisedName);
        setMaxStackSize(16);
        setMaxDamage(0);
        setTextureName("colored_egg");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        DyeColor color = DyeColor.fromIndex(stack.getItemDamage());
        String unlocalizedName = color.getName();
        if (unlocalizedName.equals("silver")) {
            unlocalizedName += "Dye";
        }
        return LibMisc.LANG.localize(getUnlocalizedName() + "." + unlocalizedName + ".name");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.isDye()) {
                list.add(new ItemStack(item, 1, chicken.getDyeMetadata()));
            }
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return DyeColor.fromIndex(stack.getItemDamage())
            .getColor();
    }

    public int getChickenType(ItemStack itemStack) {
        ChickensRegistryItem chicken = ChickensRegistry.findDyeChicken(itemStack.getItemDamage());
        if (chicken == null) {
            return -1;
        }
        return chicken.getId();
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        list.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "liquid_egg.l1"));
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
