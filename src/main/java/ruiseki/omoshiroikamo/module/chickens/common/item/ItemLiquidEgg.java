package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import ruiseki.omoshiroikamo.api.entity.chicken.LiquidEggRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.LiquidEggRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class ItemLiquidEgg extends ItemOK implements IFluidContainerItem {
    // TODO: When use in Fluid port, it will be consumed.
    // For now, empty egg is returned.

    public ItemLiquidEgg() {
        super(ModObject.itemLiquidEgg);
        setMaxStackSize(16);
        setMaxDamage(0);
        setTextureName("chicken/liquid_egg");
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (LiquidEggRegistryItem liquid : LiquidEggRegistry.getAll()) {
            ItemStack filled = new ItemStack(item, 1, liquid.getId());
            setFluidNBT(filled);
            list.add(filled);
        }
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        return LiquidEggRegistry.findById(stack.getItemDamage())
            .getEggColor();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Block liquid = LiquidEggRegistry.findById(stack.getItemDamage())
            .getLiquid();
        return LibMisc.LANG.localize(
            getUnlocalizedName() + "."
                + liquid.getUnlocalizedName()
                    .substring(5)
                + ".name");
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();
        builder.addLang(LibResources.TOOLTIP + "liquid_egg.l1");
        list.addAll(builder.build());
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);

        if (mop == null) {
            return stack;
        }

        if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return stack;
        }

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        if (!world.canMineBlock(player, x, y, z)) {
            return stack;
        }

        Block hitBlock = world.getBlock(x, y, z);
        boolean replace = hitBlock.isReplaceable(world, x, y, z);

        ForgeDirection side = ForgeDirection.getOrientation(mop.sideHit);

        int px = replace && side == ForgeDirection.UP ? x : x + side.offsetX;
        int py = replace && side == ForgeDirection.UP ? y : y + side.offsetY;
        int pz = replace && side == ForgeDirection.UP ? z : z + side.offsetZ;

        Block liquid = LiquidEggRegistry.findById(stack.getItemDamage())
            .getLiquid();

        if (!player.canPlayerEdit(px, py, pz, mop.sideHit, stack)) {
            return stack;
        }

        FluidStack fluid = getFluid(stack);
        if (fluid != null && fluid.amount >= 1000) {
            if (tryPlaceContainedLiquid(world, px, py, pz, liquid)) {
                player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

                if (!player.capabilities.isCreativeMode) {
                    stack.stackSize--;
                }

                return stack;
            }

        } else {
            stack.stackSize--;
            return stack;
        }
        return stack;
    }

    private boolean tryPlaceContainedLiquid(World world, int x, int y, int z, Block fluidBlock) {
        if (fluidBlock == Blocks.air) {
            return false;
        }

        Material targetMat = world.getBlock(x, y, z)
            .getMaterial();
        boolean replaceable = !targetMat.isSolid();

        if (!world.isAirBlock(x, y, z) && !replaceable) {
            return false;
        }

        if (world.provider.isHellWorld && fluidBlock == Blocks.flowing_water) {
            world.playSoundEffect(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                "random.fizz",
                0.5F,
                2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
            for (int i = 0; i < 8; i++) {
                world.spawnParticle("largesmoke", x + Math.random(), y + Math.random(), z + Math.random(), 0, 0, 0);
            }
            return true;
        } else {
            if (!world.isRemote && replaceable && !targetMat.isLiquid()) {
                world.func_147480_a(x, y, z, true);
            }
            world.setBlock(x, y, z, fluidBlock, 0, 3);
            return true;
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        setFluidNBT(stack);
    }

    private void setFluidNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.stackTagCompound.hasKey("Fluid")) {
            LiquidEggRegistryItem egg = LiquidEggRegistry.findById(stack.getItemDamage());
            FluidStack fluid = new FluidStack(egg.getFluid(), 1000);

            NBTTagCompound fluidNBT = new NBTTagCompound();
            fluid.writeToNBT(fluidNBT);

            stack.stackTagCompound.setTag("Fluid", fluidNBT);
        }
    }

    public static void setFluid(ItemStack stack, FluidStack fluid) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        NBTTagCompound fluidTag = new NBTTagCompound();
        fluid.writeToNBT(fluidTag);
        tag.setTag("Fluid", fluidTag);
        stack.setTagCompound(tag);
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
    }

    @Override
    public int getCapacity(ItemStack container) {
        return 1000;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (container.stackSize < 1) {
            return null;
        }

        FluidStack fluidStack = getFluid(container);
        if (fluidStack == null) {
            return null;
        }

        int drainAmount = Math.min(maxDrain, fluidStack.amount);
        FluidStack drained = new FluidStack(fluidStack, drainAmount);

        if (doDrain) {
            fluidStack.amount -= drainAmount;
            setFluid(container, fluidStack);

            if (fluidStack.amount <= 0) {
                container.stackSize--;
            }
        }

        return drained;
    }
}
