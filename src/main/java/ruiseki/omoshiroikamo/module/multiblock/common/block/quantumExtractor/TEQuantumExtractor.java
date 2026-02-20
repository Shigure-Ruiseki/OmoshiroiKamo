package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ExtractorType;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.core.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.multiblock.client.render.BeamSegment;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.TEQuantumOreExtractorT1;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore.TEQuantumOreExtractorT4;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.res.TEQuantumResExtractorT4;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.QuantumExtractorRecipes;

public abstract class TEQuantumExtractor extends AbstractMBModifierTE implements IOKEnergySink, ISidedInventory {

    @NBTPersist
    protected ItemStackHandlerBase output;
    protected final int[] allSlots;

    protected final List<BlockPos> modifiers = new ArrayList<>();
    protected BlockPos lens;
    protected ModifierHandler modifierHandler = new ModifierHandler();

    protected final List<WeightedStackBase> possibleResults = new ArrayList<>();
    protected final Random rand = new Random();

    protected EnumDye focusColor = EnumDye.WHITE;
    protected float focusBoostModifier = 1.0F;

    // Beam segment cache for optimized rendering
    @SideOnly(Side.CLIENT)
    private List<BeamSegment> cachedBeamSegments;
    @SideOnly(Side.CLIENT)
    private long lastSegmentCacheTick = 0L;
    private static final int SEGMENT_CACHE_INTERVAL = 10; // Update every 10 ticks

    // Path clear cache
    @SideOnly(Side.CLIENT)
    private boolean cachedPathClear = false;
    @SideOnly(Side.CLIENT)
    private long lastPathCheckTick = 0L;
    private static final int PATH_CHECK_INTERVAL = 10; // Update every 10 ticks

    public TEQuantumExtractor() {
        energyStorage.setEnergyStorage(1000000);
        this.output = new ItemStackHandlerBase(4);

        this.allSlots = new int[output.getSlots()];
        for (int i = 0; i < allSlots.length; i++) {
            allSlots[i] = i;
        }
    }

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    protected void onCrafting() {
        int energyExtracted = Math.min(getEnergyStored(), this.getCraftingEnergyCost());
        energyStorage.voidEnergy(energyExtracted);
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
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_VOID_ORE_MINER_T1.get());
        }
        if (tileEntity instanceof TEQuantumOreExtractorT4) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_VOID_ORE_MINER_T4.get());
        }
        if (tileEntity instanceof TEQuantumOreExtractorT1) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_VOID_RES_MINER_T1.get());
        }
        if (tileEntity instanceof TEQuantumResExtractorT4) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_VOID_RES_MINER_T4.get());
        }
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockPos pos = new BlockPos(x, y, z, worldObj);
        if ((block == MultiBlockBlocks.COLORED_LENS.getBlock() || block == MultiBlockBlocks.LENS.getBlock())
            && lens != pos) {
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
        modifierHandler.setModifiers(Collections.emptyList());
        lens = null;
    }

    private boolean isPathToVoidClear() {
        for (int y = yCoord - 1; y >= 0; y--) {
            Block block = worldObj.getBlock(xCoord, y, zCoord);

            if (block.isAir(worldObj, xCoord, y, zCoord) || block == Blocks.glass
                || block == Blocks.stained_glass
                || block == Blocks.glass_pane
                || block == Blocks.stained_glass_pane
                || block == MultiBlockBlocks.LASER_CORE.getBlock()
                || block == MultiBlockBlocks.LENS.getBlock()
                || block == MultiBlockBlocks.COLORED_LENS.getBlock()
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
        String[] whitelist = QuantumExtractorConfig.pathToVoidWhitelist;
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
        if (!getCachedPathClear()) {
            return 0f;
        }
        return 1.0F;
    }

    /**
     * Get cached path clear status.
     * Rechecked every PATH_CHECK_INTERVAL ticks.
     */
    @SideOnly(Side.CLIENT)
    private boolean getCachedPathClear() {
        if (worldObj == null) {
            return false;
        }

        long now = worldObj.getTotalWorldTime();
        if ((now - lastPathCheckTick) >= PATH_CHECK_INTERVAL) {
            lastPathCheckTick = now;
            cachedPathClear = isPathToVoidClear();
        }
        return cachedPathClear;
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
        return AxisAlignedBB
            .getBoundingBox(xCoord, Double.NEGATIVE_INFINITY, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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
     * Get cached beam segments for rendering.
     * Segments are recalculated every SEGMENT_CACHE_INTERVAL ticks to reduce
     * per-frame overhead.
     *
     * @return List of beam segments with color information
     */
    @SideOnly(Side.CLIENT)
    public List<BeamSegment> getBeamSegments() {
        if (worldObj == null) {
            return Collections.emptyList();
        }

        long now = worldObj.getTotalWorldTime();
        if (cachedBeamSegments != null && (now - lastSegmentCacheTick) < SEGMENT_CACHE_INTERVAL) {
            return cachedBeamSegments;
        }

        lastSegmentCacheTick = now;
        cachedBeamSegments = calculateBeamSegments();
        return cachedBeamSegments;
    }

    /**
     * Calculate beam segments by scanning blocks below the extractor.
     * This extracts the color scanning logic from the TESR.
     */
    @SideOnly(Side.CLIENT)
    private List<BeamSegment> calculateBeamSegments() {
        List<BeamSegment> segments = new ArrayList<>();

        // Current color (start with white)
        float[] currentColor = new float[] { 1.0f, 1.0f, 1.0f };
        int segmentStart = 0;
        boolean isFirstColoredBlock = true;

        // Scan downward from extractor
        for (int relY = 1; relY <= yCoord; relY++) {
            int worldY = yCoord - relY;
            Block block = worldObj.getBlock(xCoord, worldY, zCoord);
            int meta = worldObj.getBlockMetadata(xCoord, worldY, zCoord);

            float[] newColor = null;

            // Check for stained glass (block and pane)
            if (block == Blocks.stained_glass || block == Blocks.stained_glass_pane) {
                newColor = EntitySheep.fleeceColorTable[meta];
            }
            // Check for BlockColoredLens
            else if (block == MultiBlockBlocks.COLORED_LENS.getBlock()) {
                int fleeceIndex = 15 - meta;
                if (fleeceIndex >= 0 && fleeceIndex < 16) {
                    newColor = EntitySheep.fleeceColorTable[fleeceIndex];
                }
            }
            // Check for BlockLens with crystal meta
            else if (block == MultiBlockBlocks.LENS.getBlock() && meta == 1) {
                int color = EnumDye.CRYSTAL.getColor();
                newColor = new float[] { ((color >> 16) & 0xFF) / 255.0f, ((color >> 8) & 0xFF) / 255.0f,
                    (color & 0xFF) / 255.0f };
            }

            // If we found a colored block, create the previous segment and start a new one
            if (newColor != null) {
                int segmentEnd = relY - 1;
                if (segmentEnd > segmentStart) {
                    segments.add(new BeamSegment(segmentStart, segmentEnd - segmentStart, currentColor));
                }

                // First colored block: overwrite color entirely
                // Subsequent colored blocks: blend with current color
                if (isFirstColoredBlock) {
                    currentColor = new float[] { newColor[0], newColor[1], newColor[2] };
                    isFirstColoredBlock = false;
                } else {
                    currentColor = new float[] { (currentColor[0] + newColor[0]) / 2.0f,
                        (currentColor[1] + newColor[1]) / 2.0f, (currentColor[2] + newColor[2]) / 2.0f };
                }
                segmentStart = segmentEnd;
            }

            // Stop at bedrock - render final segment up to bedrock and exit
            if (block == Blocks.bedrock) {
                int bedrockSegmentEnd = relY - 1;
                int bedrockSegmentHeight = bedrockSegmentEnd - segmentStart;
                if (bedrockSegmentHeight > 0) {
                    segments.add(new BeamSegment(segmentStart, bedrockSegmentHeight, currentColor));
                }
                return segments;
            }
        }

        // Render the final segment (only reached if no bedrock was hit)
        int finalHeight = yCoord - segmentStart;
        if (finalHeight > 0) {
            segments.add(new BeamSegment(segmentStart, finalHeight, currentColor));
        }

        return segments;
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
    public void finishCrafting() {
        super.finishCrafting();
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

            // Apply Luck Modifier: chance for bonus output
            // 10% per modifier, additive (e.g., 20 modifiers = 200% = guaranteed +2 items)
            // Luck is additive, so default is 0.0 (no bonus without modifiers)
            float luckChance = modifierHandler.getAttributeMultiplier("luck", 0.0F);
            if (luckChance > 0) {
                int guaranteedBonus = (int) luckChance; // 200% -> +2 guaranteed
                float remainingChance = luckChance - guaranteedBonus; // e.g., 0.25 for 225%
                clone.stackSize = 1 + guaranteedBonus;
                if (remainingChance > 0 && this.rand.nextFloat() < remainingChance) {
                    clone.stackSize++;
                }
                // Cap at max stack size
                clone.stackSize = Math.min(clone.stackSize, clone.getMaxStackSize());
            }

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
    public boolean canStartCrafting() {

        boolean hasFreeSlot = false;
        for (int i = 0; i < output.getSlots(); i++) {
            if (output.getStackInSlot(i) == null) {
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
        possibleResults.clear();

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

        if (!hasEnergyForCrafting()) {
            return false;
        }

        return isPathToVoidClear();
    }

    public abstract int getEnergyCostPerDuration();

    @Override
    public int getCraftingEnergyCost() {
        int base = getEnergyCostPerDuration();
        int duration = Math.max(1, getCraftingDuration());
        float multiplier = modifierHandler.hasAttribute("energycost")
            ? modifierHandler.getAttributeMultiplier("energycost")
            : 1.0F;

        return Math.max(1, (int) (base * multiplier) / duration);
    }

    @Override
    public float getSpeedMultiplier() {
        float speed = modifierHandler.getAttributeMultiplier("speed");
        float speedP = modifierHandler.getAttributeMultiplier("speed_p");
        return speed * speedP;
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
        return getLocalizedName();
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
        return canInteractWith(player);
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
