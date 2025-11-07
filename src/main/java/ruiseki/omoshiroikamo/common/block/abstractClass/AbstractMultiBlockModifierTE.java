package ruiseki.omoshiroikamo.common.block.abstractClass;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ItemUtil;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.mojang.authlib.GameProfile;

import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketMBClientUpdate;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class AbstractMultiBlockModifierTE extends AbstractTE implements IProgressTile {

    protected GameProfile player;
    protected boolean isFormed = false;
    private boolean isProcessing = false;
    private int currentDuration = 0;
    private int currentProgress = 0;

    protected abstract IStructureDefinition getStructureDefinition();

    public abstract int[][] getOffSet();

    public abstract String getStructurePieceName();

    protected ExtendedFacing getExtendedFacing() {
        return ExtendedFacing.of(ForgeDirection.getOrientation(getFacing()));
    }

    @SuppressWarnings("unchecked")
    protected boolean structureCheck(String piece, int offsetX, int offsetY, int offsetZ) {
        boolean valid = getStructureDefinition().check(
            this,
            piece,
            worldObj,
            getExtendedFacing(),
            xCoord,
            yCoord,
            zCoord,
            offsetX,
            offsetY,
            offsetZ,
            false);

        if (valid && !isFormed) {
            isFormed = true;
            onFormed();
        } else if (!valid && isFormed) {
            isFormed = false;
            clearStructureParts();
        }

        return !valid;
    }

    protected abstract void clearStructureParts();

    protected boolean addToMachine(TileEntity tile) {
        return false;
    }

    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        return false;
    }

    public abstract int getTier();

    @Override
    public void doUpdate() {

        if (structureCheck(
            getStructurePieceName(),
            getOffSet()[getTier() - 1][0],
            getOffSet()[getTier() - 1][1],
            getOffSet()[getTier() - 1][2])) {
            return;
        }

        if (this.isFormed) {
            this.machineTick();
        } else {
            this.isProcessing = false;
        }
        super.doUpdate();
    }

    public void machineTick() {
        if (this.canProcess()) {
            if (this.currentProgress < this.currentDuration) {
                this.isProcessing = true;
                this.onProcessTick();
                ++this.currentProgress;
            } else {
                this.onProcessComplete();
                this.currentDuration = this.getCurrentProcessDuration();
                this.currentProgress = 0;
                this.isProcessing = false;
            }
        } else {
            this.isProcessing = false;
        }
    }

    public abstract int getBaseDuration();

    public abstract int getMinDuration();

    public abstract int getMaxDuration();

    public abstract float getSpeedMultiplier();

    public abstract boolean canProcess();

    public abstract void onProcessTick();

    public abstract void onProcessComplete();

    public abstract void onFormed();

    public int getCurrentProcessDuration() {
        int duration = (int) ((float) this.getBaseDuration() * this.getSpeedMultiplier());
        if (duration < this.getMinDuration()) {
            return this.getMinDuration();
        } else {
            return duration > this.getMaxDuration() ? this.getMaxDuration() : duration;
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        root.setBoolean("processing", this.isProcessing);
        root.setInteger("curr_dur", this.currentDuration);
        root.setInteger("curr_prog", this.currentProgress);
        root.setBoolean("isFormed", this.isFormed);
        if (this.player != null) {
            root.setTag("profile", PlayerUtils.proifleToNBT(this.player));
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        this.isProcessing = root.getBoolean("processing");
        this.currentDuration = root.getInteger("curr_dur");
        this.currentProgress = root.getInteger("curr_prog");
        this.isFormed = root.getBoolean("isFormed");
        if (root.hasKey("profile")) {
            this.player = PlayerUtils.profileFromNBT(root.getCompoundTag("profile"));
        } else {
            this.player = null;
        }
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public float getProgress() {
        return getCurrentProgress();
    }

    @Override
    public void setProgress(float progress) {
        setCurrentProgress((int) progress);
    }

    public boolean isFormed() {
        return this.isFormed;
    }

    public void setFormed(boolean formed) {
        isFormed = formed;
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    public void setProcessing(boolean processing) {
        isProcessing = processing;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int currentDuration) {
        this.currentDuration = currentDuration;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void setPlayer(EntityPlayer plr) {
        if (plr != null) {
            this.player = plr.getGameProfile();
        }
    }

    public void setPlayer(UUID plr) {
        EntityPlayer pl = PlayerUtils.getPlayerFromWorld(this.worldObj, plr);
        if (pl != null) {
            this.player = pl.getGameProfile();
        }

    }

    public UUID getPlayerID() {
        return this.player.getId();
    }

    public GameProfile getPlayerProfile() {
        return this.player;
    }

    public void updateClientWithPlayer() {
        if (this.player == null) {
            return;
        }

        EntityPlayer playerObj = PlayerUtils.getPlayerFromWorld(this.worldObj, this.player.getId());
        if (playerObj == null) {
            return;
        }
        PacketHandler.sendToAllAround(new PacketMBClientUpdate(this), playerObj, 8.0D);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        return false;
    }

    public void ejectAll(ItemStackHandler output) {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            BlockCoord loc = getLocation().getLocation(dir);
            TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);
            if (te == null) {
                continue;
            }

            for (int i = 0; i < output.getSlots(); i++) {
                ItemStack stack = output.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }

                int inserted = ItemUtil.doInsertItem(te, stack, dir.getOpposite());

                if (inserted > 0) {
                    stack.stackSize -= inserted;
                    if (stack.stackSize <= 0) {
                        stack = null;
                    }
                    if (i < output.getSlots()) {
                        output.setStackInSlot(i, stack);
                    }
                    markDirty();
                }
            }
        }
    }

}
