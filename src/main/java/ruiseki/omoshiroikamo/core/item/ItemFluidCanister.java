package ruiseki.omoshiroikamo.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.color.RGBColor;
import com.gtnewhorizon.gtnhlib.itemrendering.IItemTexture;
import com.gtnewhorizon.gtnhlib.itemrendering.ItemTexture;
import com.gtnewhorizon.gtnhlib.itemrendering.ItemWithTextures;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;
import ruiseki.omoshiroikamo.core.client.render.item.MaskedBlockItemTexture;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.fluid.EnumFluidMaterial;

/**
 * Universal container for gases and liquids.
 * Tints its texture based on the contained fluid.
 * Can be used like a bucket to place/pick up fluids.
 */
public class ItemFluidCanister extends ItemOK implements IFluidContainerItem, ItemWithTextures {

    public ItemFluidCanister() {
        super("fluid_canister");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    private IIcon iconFluid;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibResources.PREFIX_MOD + "modular/canister");
        iconFluid = register.registerIcon(LibResources.PREFIX_MOD + "modular/canister_fluid");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemTexture[] getTextures(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        if (fluid != null && fluid.getFluid() != null) {
            IIcon fluidIcon = fluid.getFluid()
                .getStillIcon();

            int color = 0xFFFFFF;
            boolean found = false;
            for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
                if (mat.getName()
                    .equalsIgnoreCase(
                        fluid.getFluid()
                            .getName())) {
                    color = mat.getColor();
                    found = true;
                    break;
                }
            }
            if (!found) {
                color = fluid.getFluid()
                    .getColor(fluid);
            }

            RGBColor tint = RGBColor.fromRGB(color);
            // Use the actual fluid texture clipped to the mask shape (no overflow).
            // Fall back to colored mask when the fluid has no still icon registered.
            IItemTexture fluidLayer = fluidIcon != null ? new MaskedBlockItemTexture(iconFluid, fluidIcon, tint)
                : new ItemTexture(s -> iconFluid, s -> tint);
            return new IItemTexture[] { fluidLayer, new ItemTexture(s -> itemIcon, s -> RGBColor.WHITE) };
        }
        return new IItemTexture[] { new ItemTexture(s -> itemIcon, s -> RGBColor.WHITE) };
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // Bucket-like behavior
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return stack;

        int x = mop.blockX;
        int y = mop.blockY;
        int z = mop.blockZ;

        if (!world.canMineBlock(player, x, y, z)) return stack;

        FluidStack containerFluid = getFluid(stack);

        if (containerFluid == null || containerFluid.amount < 1000) {
            // Try to pick up fluid
            if (!player.canPlayerEdit(x, y, z, mop.sideHit, stack)) return stack;

            Block block = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);

            if (fluid == null) return stack;

            boolean isSource;
            if (block instanceof IFluidBlock fluidBlock) {
                isSource = fluidBlock.canDrain(world, x, y, z);
            } else {
                isSource = (meta == 0);
            }

            if (isSource) {
                FluidStack pickedUp = new FluidStack(fluid, 1000);
                if (fill(stack, pickedUp, false) == 1000) {
                    fill(stack, pickedUp, true);
                    world.setBlockToAir(x, y, z);
                }
            }
        } else {
            // Try to place fluid
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[mop.sideHit];
            int tx = x + dir.offsetX;
            int ty = y + dir.offsetY;
            int tz = z + dir.offsetZ;

            if (!player.canPlayerEdit(tx, ty, tz, mop.sideHit, stack)) return stack;

            if (this.tryPlaceContainedFluid(world, tx, ty, tz, containerFluid.getFluid())) {
                drain(stack, 1000, true);
            }
        }

        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!(world.getTileEntity(x, y, z) instanceof IFluidHandler tank)) return false;

        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
        FluidStack containerFluid = getFluid(stack);

        if (containerFluid == null || containerFluid.amount < 1000) {
            // Try to fill canister from tank
            FluidStack available = tank.drain(dir, 1000, false);
            if (available == null || available.amount <= 0) return false;

            int filled = fill(stack, available, false);
            if (filled <= 0) return false;

            fill(stack, tank.drain(dir, filled, true), true);
            world.playSoundAtEntity(player, "game.neutral.swim.splash", 0.5F, 1.0F);
            return true;
        } else {
            // Try to fill tank from canister
            int filled = tank.fill(dir, containerFluid, false);
            if (filled <= 0) return false;

            FluidStack drained = drain(stack, filled, false);
            if (drained == null || drained.amount <= 0) return false;

            tank.fill(dir, drained, true);
            drain(stack, drained.amount, true);
            world.playSoundAtEntity(player, "game.neutral.swim.splash", 0.5F, 1.0F);
            return true;
        }
    }

    private boolean tryPlaceContainedFluid(World world, int x, int y, int z, Fluid fluid) {
        if (fluid == null || !fluid.canBePlacedInWorld()) return false;

        Material material = world.getBlock(x, y, z)
            .getMaterial();
        boolean isNotSolid = !material.isSolid();

        if (!world.isAirBlock(x, y, z) && !isNotSolid) return false;

        if (!world.isRemote && isNotSolid && !material.isLiquid()) {
            world.func_147480_a(x, y, z, true);
        }

        Block block = fluid.getBlock();
        if ((block == null || block == Blocks.water) && fluid == FluidRegistry.WATER) {
            block = Blocks.flowing_water;
        }
        if ((block == null || block == Blocks.lava) && fluid == FluidRegistry.LAVA) {
            block = Blocks.flowing_lava;
        }

        if (block == null) return false;

        world.setBlock(x, y, z, block, 0, 3);
        return true;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        if (fluid == null || fluid.getFluid() == null) return super.getItemStackDisplayName(stack);

        String fluidName = fluid.getFluid()
            .getLocalizedName(fluid);

        // Try to find if we have a custom name for this fluid in our Enum
        String registryName = FluidRegistry.getFluidName(fluid);
        for (EnumFluidMaterial mat : EnumFluidMaterial.values()) {
            if (!mat.getName()
                .equalsIgnoreCase(registryName)) continue;

            // Force our mod's localized name if it match our material
            String local = StatCollector.translateToLocal("fluid." + mat.getName());
            if (local != null && !local.equals("fluid." + mat.getName())) {
                fluidName = local;
            }
            break;
        }

        return super.getItemStackDisplayName(stack) + " (" + fluidName + ")";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        FluidStack fluid = getFluid(stack);
        if (fluid != null && fluid.getFluid() != null) {
            // Display amount
            list.add(String.format("§7%d / %d mB§r", fluid.amount, getCapacity(stack)));

            // Shift for ID
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                list.add("§8Fluid ID: " + FluidRegistry.getFluidName(fluid) + "§r");
            } else {
                list.add(StatCollector.translateToLocal("tooltip.holdshift"));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Empty canister
        list.add(new ItemStack(item));

        if (!ItemConfigs.showAllCanisters) return;

        // Filled canisters for all registered fluids (including other mods)
        for (Fluid fluid : FluidRegistry.getRegisteredFluids()
            .values()) {
            ItemStack stack = new ItemStack(item);
            fill(stack, new FluidStack(fluid, 1000), true);
            list.add(stack);
        }
    }

    /* IFluidContainerItem Implementation */

    @Override
    public FluidStack getFluid(ItemStack container) {
        if (container.stackTagCompound == null || !container.stackTagCompound.hasKey("Fluid")) return null;
        return FluidStack.loadFluidStackFromNBT(container.stackTagCompound.getCompoundTag("Fluid"));
    }

    @Override
    public int getCapacity(ItemStack container) {
        return 1000;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (resource == null || resource.getFluid() == null || resource.amount <= 0) return 0;

        FluidStack current = getFluid(container);
        if (current != null && !current.isFluidEqual(resource)) return 0;

        int capacity = getCapacity(container);
        int currentAmount = current == null ? 0 : current.amount;
        int toFill = Math.min(resource.amount, capacity - currentAmount);

        if (toFill <= 0) return 0;

        if (doFill) {
            if (container.stackTagCompound == null) {
                container.setTagCompound(new NBTTagCompound());
            }
            if (current == null) {
                current = new FluidStack(resource.getFluid(), toFill);
            } else {
                current.amount += toFill;
            }
            NBTTagCompound fluidTag = new NBTTagCompound();
            current.writeToNBT(fluidTag);
            container.stackTagCompound.setTag("Fluid", fluidTag);
        }
        return toFill;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        FluidStack current = getFluid(container);
        if (current == null || maxDrain <= 0) return null;

        int toDrain = Math.min(current.amount, maxDrain);
        FluidStack drained = new FluidStack(current.getFluid(), toDrain);

        if (doDrain) {
            current.amount -= toDrain;
            if (current.amount <= 0) {
                container.stackTagCompound.removeTag("Fluid");
                if (container.stackTagCompound.hasNoTags()) {
                    container.setTagCompound(null);
                }
            } else {
                NBTTagCompound fluidTag = new NBTTagCompound();
                current.writeToNBT(fluidTag);
                container.stackTagCompound.setTag("Fluid", fluidTag);
            }
        }
        return drained;
    }
}
