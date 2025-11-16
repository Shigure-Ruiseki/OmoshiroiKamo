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
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;
import com.gtnewhorizon.gtnhlib.capability.item.ItemIO;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSink;
import com.gtnewhorizon.gtnhlib.capability.item.ItemSource;
import com.gtnewhorizon.gtnhlib.item.ItemTransfer;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.energy.IPowerContainer;
import ruiseki.omoshiroikamo.api.energy.PowerHandlerUtils;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.OKItemIO;
import ruiseki.omoshiroikamo.api.item.WeightedStackBase;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT4;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT4;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketPowerStorage;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class TEQuantumExtractor extends AbstractMBModifierTE
    implements IEnergyReceiver, IPowerContainer, ISidedInventory, CapabilityProvider {

    protected int storedEnergyRF = 0;
    protected float lastSyncPowerStored = -1;

    protected EnergyStorage energyStorage;

    protected ItemStackHandler output;
    protected final int[] allSlots;

    protected final List<BlockPos> modifiers = new ArrayList<>();
    protected BlockPos lens;
    protected ModifierHandler modifierHandler = new ModifierHandler();

    protected final List<WeightedStackBase> possibleResults = new ArrayList<>();
    protected final Random rand = new Random();

    protected EnumDye focusColor = EnumDye.WHITE;
    protected float focusBoostModifier = 1.0F;

    @SideOnly(Side.CLIENT)
    private float beamProgress = 0.0F;
    @SideOnly(Side.CLIENT)
    private long lastBeamUpdateTick = 0L;

    public TEQuantumExtractor() {
        this.energyStorage = new EnergyStorage(1000000);
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
    protected boolean processTasks(boolean redstoneCheckPassed) {
        boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
        return super.processTasks(redstoneCheckPassed);
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
        TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord, zCoord);
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

        BlockPos pos = new BlockPos(x, y, z);
        if (modifiers.contains(pos)) {
            return false;
        }

        if (block == ModBlocks.COLORED_LENS.get() || block == ModBlocks.LENS.get()) {
            lens = new BlockPos(x, y, z);
            return true;
        }
        if (isModifierBlock(block)) {
            modifiers.add(pos);
            return true;
        }

        return false;
    }

    private boolean isModifierBlock(Block block) {
        return block == ModBlocks.MODIFIER_SPEED.get() || block == ModBlocks.MODIFIER_ACCURACY.get();
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
        root.setInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY, storedEnergyRF);
        root.setTag("output_inv", this.output.serializeNBT());
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        if (root.hasKey("storedEnergy")) {
            float storedEnergyMJ = root.getFloat("storedEnergy");
            setEnergyStored((int) (storedEnergyMJ * 10f));
        } else if (root.hasKey(PowerHandlerUtils.STORED_ENERGY_NBT_KEY)) {
            setEnergyStored(root.getInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY));
        }
        if (root.hasKey("output_inv")) {
            this.output.deserializeNBT(root.getCompoundTag("output_inv"));
        }
    }

    private boolean isPathToVoidClear() {
        for (int y = yCoord - 1; y >= 0; y--) {
            Block block = worldObj.getBlock(xCoord, y, zCoord);

            if (block.isAir(worldObj, xCoord, y, zCoord) || block == Blocks.bedrock
                || block == Blocks.glass
                || block == ModBlocks.LASER_CORE.get()
                || block == ModBlocks.LENS.get()
                || block == ModBlocks.COLORED_LENS.get()) {
                continue;
            }

            return false;
        }
        return true;
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

    public abstract IFocusableRegistry getRegistry();

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
            if (adjacent == null) {
                continue;
            }
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
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
            }
        }

        modifierHandler.setModifiers(mods);
        modifierHandler.calculateAttributeMultipliers();
        focusBoostModifier = modifierHandler.getAttributeMultiplier("accuracy");

        if (this.possibleResults.isEmpty()) {
            if (lens != null) {
                Block block = worldObj.getBlock(lens.x, lens.y, lens.z);
                if (block instanceof BlockColoredLens) {
                    int meta = worldObj.getBlockMetadata(lens.x, lens.y, lens.z);
                    this.focusColor = ((BlockColoredLens) block).getFocusColor(meta);
                    this.possibleResults.clear();
                    this.possibleResults.addAll(
                        this.getRegistry()
                            .getFocusedList(this.focusColor, this.focusBoostModifier));
                } else {
                    this.focusColor = null;
                    this.possibleResults.clear();
                    this.possibleResults.addAll(
                        this.getRegistry()
                            .getUnFocusedList());
                }
            } else {
                this.possibleResults.clear();
                this.possibleResults.addAll(
                    this.getRegistry()
                        .getUnFocusedList());
            }
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
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        if (capability == ItemSource.class || capability == ItemSink.class || capability == ItemIO.class) {
            return capability.cast(new OKItemIO(this, side));
        }

        return null;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        int result = Math.min(getMaxEnergyReceived(), maxReceive);
        result = Math.min(getMaxEnergyStored() - getEnergyStored(), result);
        result = Math.max(0, result);
        if (result > 0 && !simulate) {
            setEnergyStored(getEnergyStored() + result);
        }
        return result;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored() {
        return storedEnergyRF;
    }

    @Override
    public void setEnergyStored(int storedEnergy) {
        this.storedEnergyRF = Math.min(storedEnergy, getMaxEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getMaxEnergyReceived() {
        return energyStorage.getMaxReceive();
    }
}
