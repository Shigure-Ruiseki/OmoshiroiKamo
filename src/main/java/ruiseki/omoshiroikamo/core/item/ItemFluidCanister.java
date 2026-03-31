package ruiseki.omoshiroikamo.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.fluid.EnumFluidMaterial;
import ruiseki.omoshiroikamo.core.fluid.ModFluidGases;

/**
 * Universal container for gases and liquids.
 * Tints its texture based on the contained fluid.
 * Can be used like a bucket to place/pick up fluids.
 */
public class ItemFluidCanister extends ItemOK implements IFluidContainerItem {

    public ItemFluidCanister() {
        super("fluid_canister");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Bucket-like behavior
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (mop == null) {
            return stack;
        } else {
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int x = mop.blockX;
                int y = mop.blockY;
                int z = mop.blockZ;

                if (!world.canMineBlock(player, x, y, z)) {
                    return stack;
                }

                FluidStack containerFluid = getFluid(stack);

                if (containerFluid == null || containerFluid.amount < 1000) {
                    // Try to pick up fluid
                    if (!player.canPlayerEdit(x, y, z, mop.sideHit, stack)) {
                        return stack;
                    }

                    Block block = world.getBlock(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);
                    Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

                    if (fluid != null) {
                        // Check if it's a source block
                        boolean isSource = false;
                        if (block instanceof IFluidBlock) {
                            if (((IFluidBlock) block).canDrain(world, x, y, z)) {
                                isSource = true;
                            }
                        } else if (meta == 0) {
                            isSource = true;
                        }

                        if (isSource) {
                            FluidStack pickedUp = new FluidStack(fluid, 1000);
                            if (fill(stack, pickedUp, false) == 1000) {
                                fill(stack, pickedUp, true);
                                world.setBlockToAir(x, y, z);
                                return stack;
                            }
                        }
                    }
                } else {
                    // Try to place fluid
                    ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[mop.sideHit];
                    int tx = x + dir.offsetX;
                    int ty = y + dir.offsetY;
                    int tz = z + dir.offsetZ;

                    if (!player.canPlayerEdit(tx, ty, tz, mop.sideHit, stack)) {
                        return stack;
                    }

                    if (this.tryPlaceContainedFluid(world, tx, ty, tz, containerFluid.getFluid())) {
                        drain(stack, 1000, true);
                        return stack;
                    }
                }
            }

            return stack;
        }
    }

    private boolean tryPlaceContainedFluid(World world, int x, int y, int z, Fluid fluid) {
        if (fluid == null || !fluid.canBePlacedInWorld()) {
            return false;
        } else {
            Material material = world.getBlock(x, y, z)
                .getMaterial();
            boolean isNotSolid = !material.isSolid();

            if (!world.isAirBlock(x, y, z) && !isNotSolid) {
                return false;
            } else {
                if (!world.isRemote && isNotSolid && !material.isLiquid()) {
                    world.func_147480_a(x, y, z, true);
                }

                Block block = fluid.getBlock();
                if (block == null) {
                    // Fallback to vanilla if applicable
                    if (fluid == FluidRegistry.WATER) block = Blocks.flowing_water;
                    else if (fluid == FluidRegistry.LAVA) block = Blocks.flowing_lava;
                }

                if (block != null) {
                    int meta = 0; // Source block
                    world.setBlock(x, y, z, block, meta, 3);
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Empty canister
        list.add(new ItemStack(item));

        // Filled canisters for each fluid material
        for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
            ItemStack stack = new ItemStack(item);
            Fluid fluid = ModFluidGases.FLUIDS.get(mat);
            if (fluid != null) {
                fill(stack, new FluidStack(fluid, 1000), true);
                list.add(stack);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int renderPass) {
        // Render pass 1 is the fluid/content part of the canister
        if (renderPass == 1) {
            FluidStack fluid = getFluid(stack);
            if (fluid != null && fluid.getFluid() != null) {
                // Find matching material to get color
                for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
                    if (mat.getName()
                        .equals(
                            fluid.getFluid()
                                .getName())) {
                        return mat.getColor();
                    }
                }
                // Fallback to fluid color if not in our enum
                return fluid.getFluid()
                    .getColor(fluid);
            }
        }
        return super.getColorFromItemStack(stack, renderPass);
    }

    /* IFluidContainerItem Implementation */

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
        if (resource == null || resource.getFluid() == null) return 0;

        FluidStack current = getFluid(container);
        if (current != null && !current.isFluidEqual(resource)) return 0;

        if (!doFill) return 1000;

        if (container.stackTagCompound == null) {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound fluidTag = new NBTTagCompound();
        resource.writeToNBT(fluidTag);
        container.stackTagCompound.setTag("Fluid", fluidTag);
        return 1000;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        FluidStack stack = getFluid(container);
        if (stack == null) return null;

        int drained = Math.min(stack.amount, maxDrain);
        if (doDrain) {
            stack.amount -= drained;
            if (stack.amount <= 0) {
                container.stackTagCompound.removeTag("Fluid");
            } else {
                fill(container, stack, true);
            }
        }
        return new FluidStack(stack.getFluid(), drained);
    }
}
