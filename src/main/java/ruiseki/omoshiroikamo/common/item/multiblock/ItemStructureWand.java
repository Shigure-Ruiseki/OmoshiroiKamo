package ruiseki.omoshiroikamo.common.item.multiblock;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.structure.WandSelectionManager;

/**
 * 構造スキャン用の杖アイテム
 * 右クリック: ポジション1を設定
 * Shift+右クリック: ポジション2を設定
 * 左クリック（空気）: スキャン範囲を仮保存
 */
public class ItemStructureWand extends ItemOK {

    private static final String NBT_POS1_X = "pos1X";
    private static final String NBT_POS1_Y = "pos1Y";
    private static final String NBT_POS1_Z = "pos1Z";
    private static final String NBT_POS2_X = "pos2X";
    private static final String NBT_POS2_Y = "pos2Y";
    private static final String NBT_POS2_Z = "pos2Z";
    private static final String NBT_HAS_POS1 = "hasPos1";
    private static final String NBT_HAS_POS2 = "hasPos2";
    private static final String NBT_DIMENSION = "dimension";

    public ItemStructureWand() {
        super("structure_wand");
        setMaxStackSize(1);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {

        if (world.isRemote) {
            return false;
        }

        NBTTagCompound nbt = getOrCreateNBT(stack);

        if (player.isSneaking()) {
            // Shift+右クリック: ポジション2を設定
            setPos2(nbt, x, y, z, world.provider.dimensionId);
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + "[OmoshiroiKamo] Position 2 set: (" + x + ", " + y + ", " + z + ")"));

            // 両方設定されていたらブロック数を表示
            if (hasPos1(nbt) && hasPos2(nbt)) {
                showSelectionInfo(player, nbt);
            }
        } else {
            // 右クリック: ポジション1を設定
            setPos1(nbt, x, y, z, world.provider.dimensionId);
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.AQUA + "[OmoshiroiKamo] Position 1 set: (" + x + ", " + y + ", " + z + ")"));
        }

        return true;
    }

    @Override
    public boolean onEntitySwing(EntityPlayer player, ItemStack stack) {
        if (player.worldObj.isRemote) {
            return false;
        }

        NBTTagCompound nbt = getOrCreateNBT(stack);

        if (!hasPos1(nbt) || !hasPos2(nbt)) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED
                        + "[OmoshiroiKamo] Please set both positions first! (Right-click for pos1, Shift+Right-click for pos2)"));
            return false;
        }

        // ディメンションチェック
        int dim = nbt.getInteger(NBT_DIMENSION);
        if (dim != player.worldObj.provider.dimensionId) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[OmoshiroiKamo] Selection is in a different dimension!"));
            return false;
        }

        // 仮保存
        ChunkCoordinates pos1 = getPos1(nbt);
        ChunkCoordinates pos2 = getPos2(nbt);

        WandSelectionManager.getInstance()
            .setPendingScan(player.getUniqueID(), pos1, pos2, dim);

        int blockCount = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID())
            .getBlockCount();

        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GREEN + "[OmoshiroiKamo] Scan area ready! (" + blockCount + " blocks)"));
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.YELLOW + "Use: " + EnumChatFormatting.WHITE + "/ok wand save <name>"));

        return false;
    }

    private void showSelectionInfo(EntityPlayer player, NBTTagCompound nbt) {
        ChunkCoordinates pos1 = getPos1(nbt);
        ChunkCoordinates pos2 = getPos2(nbt);

        int sizeX = Math.abs(pos2.posX - pos1.posX) + 1;
        int sizeY = Math.abs(pos2.posY - pos1.posY) + 1;
        int sizeZ = Math.abs(pos2.posZ - pos1.posZ) + 1;
        int totalBlocks = sizeX * sizeY * sizeZ;

        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GRAY + "Selection: "
                    + sizeX
                    + "x"
                    + sizeY
                    + "x"
                    + sizeZ
                    + " ("
                    + totalBlocks
                    + " blocks)"));
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GRAY + "Left-click to prepare scan, then use /ok wand save <name>"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            tooltip.add(EnumChatFormatting.GRAY + "Right-click: Set Position 1");
            tooltip.add(EnumChatFormatting.GRAY + "Shift+Right-click: Set Position 2");
            tooltip.add(EnumChatFormatting.GRAY + "Left-click: Prepare scan");
            return;
        }

        if (hasPos1(nbt)) {
            ChunkCoordinates pos1 = getPos1(nbt);
            tooltip.add(EnumChatFormatting.AQUA + "Pos1: (" + pos1.posX + ", " + pos1.posY + ", " + pos1.posZ + ")");
        } else {
            tooltip.add(EnumChatFormatting.GRAY + "Pos1: Not set");
        }

        if (hasPos2(nbt)) {
            ChunkCoordinates pos2 = getPos2(nbt);
            tooltip.add(EnumChatFormatting.GREEN + "Pos2: (" + pos2.posX + ", " + pos2.posY + ", " + pos2.posZ + ")");
        } else {
            tooltip.add(EnumChatFormatting.GRAY + "Pos2: Not set");
        }

        if (hasPos1(nbt) && hasPos2(nbt)) {
            ChunkCoordinates pos1 = getPos1(nbt);
            ChunkCoordinates pos2 = getPos2(nbt);
            int sizeX = Math.abs(pos2.posX - pos1.posX) + 1;
            int sizeY = Math.abs(pos2.posY - pos1.posY) + 1;
            int sizeZ = Math.abs(pos2.posZ - pos1.posZ) + 1;
            tooltip.add(EnumChatFormatting.YELLOW + "Size: " + sizeX + "x" + sizeY + "x" + sizeZ);
        }
    }

    // NBT helper methods
    private NBTTagCompound getOrCreateNBT(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private void setPos1(NBTTagCompound nbt, int x, int y, int z, int dim) {
        nbt.setInteger(NBT_POS1_X, x);
        nbt.setInteger(NBT_POS1_Y, y);
        nbt.setInteger(NBT_POS1_Z, z);
        nbt.setBoolean(NBT_HAS_POS1, true);
        nbt.setInteger(NBT_DIMENSION, dim);
    }

    private void setPos2(NBTTagCompound nbt, int x, int y, int z, int dim) {
        nbt.setInteger(NBT_POS2_X, x);
        nbt.setInteger(NBT_POS2_Y, y);
        nbt.setInteger(NBT_POS2_Z, z);
        nbt.setBoolean(NBT_HAS_POS2, true);
        nbt.setInteger(NBT_DIMENSION, dim);
    }

    private boolean hasPos1(NBTTagCompound nbt) {
        return nbt.getBoolean(NBT_HAS_POS1);
    }

    private boolean hasPos2(NBTTagCompound nbt) {
        return nbt.getBoolean(NBT_HAS_POS2);
    }

    private ChunkCoordinates getPos1(NBTTagCompound nbt) {
        return new ChunkCoordinates(nbt.getInteger(NBT_POS1_X), nbt.getInteger(NBT_POS1_Y), nbt.getInteger(NBT_POS1_Z));
    }

    private ChunkCoordinates getPos2(NBTTagCompound nbt) {
        return new ChunkCoordinates(nbt.getInteger(NBT_POS2_X), nbt.getInteger(NBT_POS2_Y), nbt.getInteger(NBT_POS2_Z));
    }

    /**
     * NBTからポジション1を取得（クライアントサイドレンダリング用）
     */
    public static ChunkCoordinates getPos1FromStack(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) return null;
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.getBoolean(NBT_HAS_POS1)) return null;
        return new ChunkCoordinates(nbt.getInteger(NBT_POS1_X), nbt.getInteger(NBT_POS1_Y), nbt.getInteger(NBT_POS1_Z));
    }

    /**
     * NBTからポジション2を取得（クライアントサイドレンダリング用）
     */
    public static ChunkCoordinates getPos2FromStack(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) return null;
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.getBoolean(NBT_HAS_POS2)) return null;
        return new ChunkCoordinates(nbt.getInteger(NBT_POS2_X), nbt.getInteger(NBT_POS2_Y), nbt.getInteger(NBT_POS2_Z));
    }

    /**
     * NBTからディメンションを取得（クライアントサイドレンダリング用）
     */
    public static int getDimensionFromStack(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) return 0;
        return stack.getTagCompound()
            .getInteger(NBT_DIMENSION);
    }
}
