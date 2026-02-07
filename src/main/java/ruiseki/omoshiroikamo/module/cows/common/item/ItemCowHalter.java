package ruiseki.omoshiroikamo.module.cows.common.item;

import static ruiseki.omoshiroikamo.api.entity.IMobStats.GAIN_NBT;
import static ruiseki.omoshiroikamo.api.entity.IMobStats.GROWTH_NBT;
import static ruiseki.omoshiroikamo.api.entity.IMobStats.STRENGTH_NBT;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

public class ItemCowHalter extends ItemOK {

    public ItemCowHalter() {
        super(ModObject.itemCowHalter);
        setMaxStackSize(1);
        setTextureName("cow/cow_halter");
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        if (!(entity instanceof EntityCowsCow cow) || player.worldObj.isRemote
            || entity.isDead
            || player.dimension != entity.dimension
            || stack.getItemDamage() > 0
            || cow.getGrowingAge() < 0) {
            return false;
        }

        NBTTagCompound tag = new NBTTagCompound();
        cow.writeEntityToNBT(tag);
        stack.setTagCompound(tag);

        stack.setItemDamage(cow.getType() + 1);

        int slot = player.inventory.currentItem;
        player.inventory.mainInventory[slot] = stack.copy();
        player.inventory.markDirty();

        entity.setDead();

        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {

        if (!player.isSneaking()) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);

        if (stack.getItemDamage() == 0 && te instanceof TEStall stall && stall.hasCow()) {
            EntityCowsCow cow = stall.getCow(world);
            NBTTagCompound tag = new NBTTagCompound();
            cow.writeToNBT(tag);
            stack.setTagCompound(tag);
            stack.setItemDamage(cow.getType() + 1);
            int slot = player.inventory.currentItem;
            player.inventory.mainInventory[slot] = stack.copy();

            stall.removeCow();
            world.markBlockForUpdate(x, y, z);
            return true;
        }

        if (stack.getItemDamage() != 0 && te instanceof TEStall stall2) {
            toStall(stall2, stack, world);
            stack.setItemDamage(0);
            stack.setTagCompound(null);
            int slot = player.inventory.currentItem;
            player.inventory.mainInventory[slot] = stack.copy();
            player.inventory.markDirty();
            return true;
        }

        if (stack.getItemDamage() != 0) {
            NBTTagCompound tag = stack.getTagCompound();
            EntityCowsCow cow = new EntityCowsCow(world);
            cow.readEntityFromNBT(tag);
            cow.setPosition(x + 0.5, y + 1, z + 0.5);
            cow.setType(stack.getItemDamage() - 1);
            world.spawnEntityInWorld(cow);

            stack.setItemDamage(0);
            stack.setTagCompound(null);
            int slot = player.inventory.currentItem;
            player.inventory.mainInventory[slot] = stack.copy();
            player.inventory.markDirty();
            return true;
        }

        return false;
    }

    public void toStall(TEStall tile, ItemStack stack, World world) {
        EntityCowsCow cow = new EntityCowsCow(world);

        cow.setType(stack.getItemDamage() - 1);
        cow.readEntityFromNBT(stack.getTagCompound());

        tile.setCow(cow);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        NBTTagCompound tag = ItemNBTUtils.getNBT(stack);
        int strength = tag.getInteger(STRENGTH_NBT);
        int growth = tag.getInteger(GROWTH_NBT);
        int gain = tag.getInteger(GAIN_NBT);
        CowsRegistryItem cow = CowsRegistry.INSTANCE.getByType(stack.getItemDamage() - 1);

        TooltipUtils builder = TooltipUtils.builder();
        builder.addIf(stack.getItemDamage() == 0, "Empty. Right Click on cow to pick it up!");
        if (stack.getItemDamage() != 0) {
            builder.addColoredLang(EnumChatFormatting.WHITE, cow.getDisplayName());
            builder.addLang(LibResources.TOOLTIP + "mob.growth", growth)
                .addLang(LibResources.TOOLTIP + "mob.gain", gain)
                .addLang(LibResources.TOOLTIP + "mob.strength", strength);
        }
        list.addAll(builder.build());
    }
}
