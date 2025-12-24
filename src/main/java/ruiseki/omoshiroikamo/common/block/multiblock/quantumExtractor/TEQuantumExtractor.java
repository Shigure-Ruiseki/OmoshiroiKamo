package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.energy.IEnergySink;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT4;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT4;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.recipe.quantumExtractor.QuantumExtractorRecipes;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

public abstract class TEQuantumExtractor extends AbstractMBModifierTE implements IEnergySink, ISidedInventory {

    protected ItemStackHandler output;
    protected final int[] allSlots;

    protected final List<BlockPos> modifiers = new ArrayList<>();
    protected BlockPos lens;
    protected ModifierHandler modifierHandler = new ModifierHandler();

    protected final List<WeightedStackBase> possibleResults = new ArrayList<>();
    protected final Random rand = new Random();

    protected EnumDye focusColor = EnumDye.WHITE;
    protected float focusBoostModifier = 1.0F;

    private float beamProgress = 0.0F;
    private long lastBeamUpdateTick = 0L;

    public TEQuantumExtractor() {
        energyStorage.setEnergyStorage(1000000);
        this.output = new ItemStackHandler(4);

        this.allSlots = new int[output.getSlots()];
        for (int i = 0; i < allSlots.length; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    public String getMachineName() {
        return "quantumExtractor";
    }

    @Override
    public void onProcessTick() {
        int energyExtracted = Math.min(getEnergyStored(), this.getEnergyCostPerTick());
        setEnergyStored(getEnergyStored() - energyExtracted);
    }

    @Override
    public void onFormed() {
        if (this.player == null) {
            return;
        }
        EntityPlayer player = PlayerUtils.getPlayerFromWorld(this.worldObj, this.player.getId());
        if (player == null) {
            return;
        }
        TileEntity tileEntity = getPos().getTileEntity();
        if (tileEntity instanceof TEQuantumOreExtractorT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_VOID_ORE_MINER_T1.get());
        }
        if (tileEntity instanceof TEQuantumOreExtractorT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_VOID_ORE_MINER_T4.get());
        }
        if (tileEntity instanceof TEQuantumOreExtractorT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_VOID_RES_MINER_T1.get());
        }
        if (tileEntity instanceof TEQuantumResExtractorT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_VOID_RES_MINER_T4.get());
        }
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockPos pos = new BlockPos(x, y, z, worldObj);
        if ((block == ModBlocks.COLORED_LENS.get() || block == ModBlocks.LENS.get()) && lens != pos) {
            lens = new BlockPos(x, y, z, worldObj);
            return true;
        }

        if (isModifierBlock(block) && !modifiers.contains(pos)) {
            modifiers.add(pos);
            return true;
        }

        return false;
    }

    private boolean isModifierBlock(Block block) {
        return block instanceof IModifierBlock;
    }

    @Override
    protected void clearStructureParts() {
        modifiers.clear();
        modifierHandler = new ModifierHandler();
        possibleResults.clear();
        lens = null;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        energyStorage.writeToNBT(root);
        root.setTag("output_inv", this.output.serializeNBT());
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        energyStorage.readFromNBT(root);
        if (root.hasKey("output_inv")) {
            this.output.deserializeNBT(root.getCompoundTag("output_inv"));
        }
    }

    private boolean isPathToVoidClear() {
        for (int y = yCoord - 1; y >= 0; y--) {
            Block block = worldObj.getBlock(xCoord, y, zCoord);

            if (block.isAir(worldObj, xCoord, y, zCoord) || block == Blocks.glass
                || block == Blocks.stained_glass
                || block == Blocks.glass_pane
                || block == Blocks.stained_glass_pane
                || block == ModBlocks.LASER_CORE.get()
                || block == ModBlocks.LENS.get()
                || block == ModBlocks.COLORED_LENS.get()
                || isBlockInPathWhitelist(block, worldObj.getBlockMetadata(xCoord, y, zCoord))) {
                if (y == 0) {
                    return true;
                }
                continue;
            }
            if (block == Blocks.bedrock) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Check if the given block matches any entry in the path-to-void whitelist.
     * Supports formats: "modid:blockname" or "modid:blockname:meta"
     */
    private boolean isBlockInPathWhitelist(Block block, int meta) {
        String[] whitelist = EnvironmentalConfig.quantumExtractorConfig.pathToVoidWhitelist;
        if (whitelist == null || whitelist.length == 0) {
            return false;
        }

        String blockName = Block.blockRegistry.getNameForObject(block);
        if (blockName == null) {
            return false;
        }

        for (String entry : whitelist) {
            if (entry == null || entry.isEmpty()) {
                continue;
            }

            // Check if entry has metadata specified (modid:block:meta)
            int lastColon = entry.lastIndexOf(':');
            int firstColon = entry.indexOf(':');

            if (lastColon > firstColon && lastColon < entry.length() - 1) {
                // Format: modid:blockname:meta
                String entryMeta = entry.substring(lastColon + 1);
                String entryBlockId = entry.substring(0, lastColon);

                try {
                    int expectedMeta = Integer.parseInt(entryMeta);
                    if (blockName.equals(entryBlockId) && meta == expectedMeta) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // If meta is not a number, treat whole string as block ID
                    if (blockName.equals(entry)) {
                        return true;
                    }
                }
            } else {
                // Format: modid:blockname (any meta)
                if (blockName.equals(entry)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getBeamProgress() {
        if (!isPathToVoidClear()) {
            beamProgress = 0f;
            return 0f;
        }

        long now = this.worldObj.getTotalWorldTime();
        int ticksPassed = (int) (now - this.lastBeamUpdateTick);
        this.lastBeamUpdateTick = now;

        if (ticksPassed > 1) {
            beamProgress -= (float) ticksPassed / 40.0F;
            if (beamProgress < 0f) {
                beamProgress = 0f;
            }
        }

        beamProgress += 0.025F;
        if (beamProgress > 1.0F) {
            beamProgress = 1.0F;
        }
        return beamProgress;
    }

    /**
     * Override the render bounding box to include the entire beam (from miner to
     * Y=0).
     * This prevents frustum culling from hiding the beam when the player is not
     * looking at the miner.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, 0, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    /**
     * Increase the max render distance to match vanilla Beacon (256 blocks).
     * This prevents the beam from disappearing when the player is far away.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D; // 256 * 256
    }

    /**
     * Returns the type of this extractor.
     * Subclasses must override to specify their type.
     */
    public abstract ExtractorType getExtractorType();

    public IFocusableRegistry getRegistry() {
        int dimId = this.worldObj.provider.dimensionId;
        int tier = getTier() - 1; // getTier() returns 1-6, array is 0-5
        switch (getExtractorType()) {
            case ORE:
                return QuantumExtractorRecipes.getOreRegistry(tier, dimId);
            case RESOURCE:
                return QuantumExtractorRecipes.getResRegistry(tier, dimId);
            default:
                throw new IllegalStateException("Unknown extractor type: " + getExtractorType());
        }
    }

    @Override
    public void onProcessComplete() {
        if (!this.possibleResults.isEmpty()) {
            WeightedStackBase result = (WeightedStackBase) WeightedRandom
                .getRandomItem(this.rand, this.possibleResults);
            if (result == null) {
                return;
            }

            ItemStack resultStack = result.getMainStack();
            if (resultStack == null || resultStack.getItem() == null) {
                return;
            }

            ItemStack clone = resultStack.copy();
            clone.stackSize = 1;
            ItemHandlerHelper.insertItem(this.output, clone, false);
            this.extract();
        }
    }

    public void extract() {
        ItemTransfer transfer = new ItemTransfer();

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {

            TileEntity adjacent = this.getWorldObj()
                .getTileEntity(this.xCoord + side.offsetX, this.yCoord + side.offsetY, this.zCoord + side.offsetZ);
            transfer.push(this, side, adjacent);
            transfer.transfer();

        }
    }

    @Override
    public boolean canProcess() {

        boolean hasFreeSlot = false;
        for (int i = 0; i < output.getSlots(); i++) {
            ItemStack stack = output.getStackInSlot(i);
            if (stack == null) {
                hasFreeSlot = true;
                break;
            }
        }
        if (!hasFreeSlot) {
            this.extract();
            return false;
        }

        List<IModifierBlock> mods = new ArrayList<>();
        for (BlockPos pos : this.modifiers) {
            Block block = pos.getBlock();
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
            }
        }

        modifierHandler.setModifiers(mods);
        modifierHandler.calculateAttributeMultipliers();
        focusBoostModifier = modifierHandler.getAttributeMultiplier("accuracy");

        // 毎回 possibleResults を再計算して Modifier 効果を反映させる
        this.possibleResults.clear();
        if (lens != null) {
            Block block = lens.getBlock();
            if (block instanceof BlockColoredLens) {
                int meta = lens.getBlockMetadata();
                this.focusColor = ((BlockColoredLens) block).getFocusColor(meta);
                this.possibleResults.addAll(
                    this.getRegistry()
                        .getFocusedList(this.focusColor, this.focusBoostModifier));
            } else {
                if (lens.getBlockMetadata() == 1) {
                    this.focusColor = EnumDye.CRYSTAL;
                    this.possibleResults.addAll(
                        this.getRegistry()
                            .getFocusedList(this.focusColor, this.focusBoostModifier));
                } else {
                    this.focusColor = null;
                    this.possibleResults.addAll(
                        this.getRegistry()
                            .getUnFocusedList());
                }
            }
        } else {
            this.possibleResults.addAll(
                this.getRegistry()
                    .getUnFocusedList());
        }

        if (getEnergyStored() < getEnergyCostPerTick()) {
            return false;
        }

        return isPathToVoidClear();
    }

    public abstract int getEnergyCostPerDuration();

    public int getEnergyCostPerTick() {
        if (this.modifierHandler.hasAttribute("energycost")) {
            int e = (int) ((float) this.getEnergyCostPerDuration()
                * this.modifierHandler.getAttributeMultiplier("energycost"));
            return Math.max(1, e / Math.max(1, this.getCurrentProcessDuration()));
        }
        return Math.max(1, this.getEnergyCostPerDuration() / Math.max(1, this.getCurrentProcessDuration()));
    }

    @Override
    public float getSpeedMultiplier() {
        return this.modifierHandler.hasAttribute("speed") ? this.modifierHandler.getAttributeMultiplier("speed") : 1.0F;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return allSlots;
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        ItemStack existing = output.getStackInSlot(slot);
        if (existing == null) {
            return false;
        }
        return stack.getItem() == existing.getItem() && existing.stackSize >= stack.stackSize;
    }

    @Override
    public int getSizeInventory() {
        return output.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return output.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack fromStack = output.getStackInSlot(fromSlot);
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            output.setStackInSlot(fromSlot, null);
            return fromStack;
        }
        ItemStack split = fromStack.splitStack(amount);
        output.setStackInSlot(fromSlot, fromStack.stackSize > 0 ? fromStack : null);
        return split;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        if (contents == null) {
            output.setStackInSlot(slot, null);
        } else {
            output.setStackInSlot(slot, contents.copy());
        }

        ItemStack stack = output.getStackInSlot(slot);
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
            output.setStackInSlot(slot, stack);
        }
    }

    @Override
    public String getInventoryName() {
        return getMachineName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }
}
