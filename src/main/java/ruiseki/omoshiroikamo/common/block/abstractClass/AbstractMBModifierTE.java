package ruiseki.omoshiroikamo.common.block.abstractClass;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.mojang.authlib.GameProfile;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class AbstractMBModifierTE extends AbstractEnergyTE implements IProgressTile {

    protected GameProfile player;
    @Getter
    protected boolean isFormed = false;
    private boolean isProcessing = false;
    @Getter
    private int currentDuration = 0;
    @Getter
    @Setter
    private int currentProgress = 0;

    protected abstract IStructureDefinition getStructureDefinition();

    public abstract int[][] getOffSet();

    public abstract String getStructurePieceName();

    protected ExtendedFacing getExtendedFacing() {
        return ExtendedFacing.of(ForgeDirection.getOrientation(getFacing()));
    }

    @SuppressWarnings("unchecked")
    protected boolean structureCheck(String piece, int ox, int oy, int oz) {
        boolean valid = getStructureDefinition()
            .check(this, piece, worldObj, getExtendedFacing(), xCoord, yCoord, zCoord, ox, oy, oz, false);

        if (valid && !isFormed) {
            isFormed = true;
            onFormed();
        } else if (!valid && isFormed) {
            isFormed = false;
            clearStructureParts();
        }

        return isFormed;
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
        if (!shouldDoWorkThisTick(20) && isFormed) {
            super.doUpdate();
            return;
        }

        boolean valid = structureCheck(
            getStructurePieceName(),
            getOffSet()[getTier() - 1][0],
            getOffSet()[getTier() - 1][1],
            getOffSet()[getTier() - 1][2]);

        if (!valid) return;

        super.doUpdate();
    }

    public void machineTick() {
        if (!canProcess()) {
            resetProcess();
            return;
        }

        if (!isProcessing) {
            currentDuration = getCurrentProcessDuration();
            currentProgress = 0;
            isProcessing = true;
        }

        if (++currentProgress >= currentDuration) {
            onProcessComplete();
            resetProcess();
        } else {
            onProcessTick();
        }
    }

    private void resetProcess() {
        currentProgress = 0;
        currentDuration = 0;
        isProcessing = false;
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
            return Math.min(duration, this.getMaxDuration());
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        NBTTagCompound multiblock = new NBTTagCompound();
        multiblock.setBoolean("processing", this.isProcessing);
        multiblock.setInteger("curr_dur", this.currentDuration);
        multiblock.setInteger("curr_prog", this.currentProgress);
        multiblock.setBoolean("isFormed", this.isFormed);
        if (this.player != null) {
            multiblock.setTag("profile", PlayerUtils.proifleToNBT(this.player));
        }
        root.setTag("multiblock", multiblock);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        NBTTagCompound multiblock = root.getCompoundTag("multiblock");
        this.isProcessing = multiblock.getBoolean("processing");
        this.currentDuration = multiblock.getInteger("curr_dur");
        this.currentProgress = multiblock.getInteger("curr_prog");
        this.isFormed = multiblock.getBoolean("isFormed");
        if (multiblock.hasKey("profile")) {
            this.player = PlayerUtils.profileFromNBT(multiblock.getCompoundTag("profile"));
        } else {
            this.player = null;
        }
    }

    @Override
    public float getProgress() {
        return getCurrentProgress();
    }

    @Override
    public void setProgress(float progress) {
        setCurrentProgress((int) progress);
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

    @Override
    public boolean isActive() {
        return getCurrentDuration() > 0;
    }

    @Override
    public boolean processTasks(boolean redstone) {
        if (!redstone || !isFormed) {
            isProcessing = false;
            return false;
        }

        machineTick();
        return isProcessing;
    }

}
