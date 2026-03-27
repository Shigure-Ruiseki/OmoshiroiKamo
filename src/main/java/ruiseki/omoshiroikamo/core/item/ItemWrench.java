package ruiseki.omoshiroikamo.core.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.item.IToolHammer;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Item Wrench - config ISidedIO.
 * TODO: Add Wrench texture
 * TODO: Add tooltip
 * TODO: Add announcement when change io
 */
public class ItemWrench extends ItemOK implements IToolHammer {

    public ItemWrench() {
        super(ModObject.WRENCH.name);
        setMaxStackSize(1);
        setTextureName("modular/wrench");
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);

        // 1. Shift + Right Click on Block: Linking / Registration
        if (player.isSneaking()) {
            // Case A: Controller linking
            if (te instanceof TEMachineController) {
                if (!world.isRemote) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt == null) {
                        nbt = new NBTTagCompound();
                    }
                    nbt.setInteger("LinkedX", x);
                    nbt.setInteger("LinkedY", y);
                    nbt.setInteger("LinkedZ", z);
                    nbt.setInteger("LinkedDim", world.provider.dimensionId);
                    stack.setTagCompound(nbt);
                    player.addChatMessage(new ChatComponentTranslation("chat.omoshiroikamo.wrench_linked", x, y, z));
                    return true;
                }
                return false;
            }

            // Case B: External Port Registration
            if (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("LinkedX")) {
                int cx = stack.getTagCompound()
                    .getInteger("LinkedX");
                int cy = stack.getTagCompound()
                    .getInteger("LinkedY");
                int cz = stack.getTagCompound()
                    .getInteger("LinkedZ");
                int cDim = stack.getTagCompound()
                    .getInteger("LinkedDim");

                if (world.provider.dimensionId == cDim && (cx != x || cy != y || cz != z)) {
                    TileEntity cte = world.getTileEntity(cx, cy, cz);
                    if (cte instanceof TEMachineController) {
                        if (!world.isRemote) {
                            TEMachineController controller = (TEMachineController) cte;
                            controller.registerExternalPort(x, y, z, getSelectedPortType(stack), player);
                            return true;
                        }
                        return false;
                    } else {
                        if (!world.isRemote) player.addChatMessage(
                            new ChatComponentTranslation(
                                "Debug: Linked TE is not Controller @ " + cx + "," + cy + "," + cz));
                    }
                } else {
                    if (!world.isRemote) player.addChatMessage(
                        new ChatComponentTranslation(
                            "Debug: Linked pos mismatch. Dim: " + (world.provider.dimensionId == cDim)
                                + " Self: "
                                + (cx == x && cy == y && cz == z)));
                }
            }
        } else {
            // 2. Normal Right Click on Block: IO Toggle (Forward)
            if (te instanceof ISidedIO io) {
                if (world.isRemote) {
                    ForgeDirection clicked = ForgeDirection.getOrientation(side);
                    ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);
                    OmoshiroiKamo.instance.getPacketHandler()
                        .sendToServer(new PacketToggleSide(io, target, false));
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player) {
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(x, y, z);

        if (player.isSneaking()) {
            // 3. Shift + Left Click on Block: Unlink
            if (!world.isRemote) {
                NBTTagCompound nbt = stack.getTagCompound();
                if (nbt != null) {
                    nbt.removeTag("LinkedX");
                    nbt.removeTag("LinkedY");
                    nbt.removeTag("LinkedZ");
                    nbt.removeTag("LinkedDim");
                    player.addChatMessage(new ChatComponentTranslation("chat.omoshiroikamo.wrench_unlinked"));
                }
            }
            return true; // Cancel breaking
        } else {
            // 4. Normal Left Click on Block: IO Toggle (Backward)
            if (te instanceof ISidedIO io) {
                if (!world.isRemote) {
                    MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        ForgeDirection clicked = ForgeDirection.getOrientation(mop.sideHit);
                        float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
                        float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
                        float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
                        ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);
                        io.toggleSide(target, true);
                    }
                }
                return true; // Cancel breaking
            }
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        // 5. Right Click in Air: Port Type Cycle (Forward / Backward)
        MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);
        if (mop == null || mop.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            if (!world.isRemote) {
                int delta = player.isSneaking() ? -1 : 1;
                cyclePortType(stack, player, delta);
            }
        }
        return stack;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String baseName = super.getItemStackDisplayName(stack);
        IPortType.Type type = getSelectedPortType(stack);
        String typeName = LangHelpers.localize("gui.port_type." + type.name());
        return LangHelpers.localize("gui.port_type.format", baseName, typeName);
    }

    public static ForgeDirection getClickedSide(ForgeDirection hitSide, float hitX, float hitY, float hitZ) {
        final float BORDER = 0.20f;

        // Determine horizontal/vertical ranges based on the face
        boolean hLeft = false, hRight = false, vTop = false, vBottom = false;

        switch (hitSide) {
            case UP:
            case DOWN:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitZ < BORDER;
                vBottom = hitZ > 1 - BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.NORTH;
                if (vBottom) return ForgeDirection.SOUTH;
                return hitSide;

            case NORTH:
            case SOUTH:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;

            case WEST:
            case EAST:
                hLeft = hitZ > 1 - BORDER;
                hRight = hitZ < BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.SOUTH;
                if (hRight) return ForgeDirection.NORTH;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;
            default:
                break;
        }

        return hitSide;
    }

    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {
        return true;
    }

    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

    }

    private void cyclePortType(ItemStack stack, EntityPlayer player, int delta) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        int currentIndex = nbt.getInteger("SelectedPortTypeIndex");
        currentIndex = (currentIndex + delta) % IPortType.SUPPORTED_TYPES.length;
        if (currentIndex < 0) currentIndex += IPortType.SUPPORTED_TYPES.length;

        nbt.setInteger("SelectedPortTypeIndex", currentIndex);

        IPortType.Type nextType = IPortType.SUPPORTED_TYPES[currentIndex];
        String typeName = LangHelpers.localize("gui.port_type." + nextType.name());
        player.addChatMessage(new ChatComponentTranslation("gui.port_type", typeName));
    }

    public static IPortType.Type getSelectedPortType(ItemStack stack) {
        if (stack.hasTagCompound()) {
            int index = stack.getTagCompound()
                .getInteger("SelectedPortTypeIndex");
            if (index >= 0 && index < IPortType.SUPPORTED_TYPES.length) {
                return IPortType.SUPPORTED_TYPES[index];
            }
        }
        return IPortType.SUPPORTED_TYPES[0];
    }
}
