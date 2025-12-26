package ruiseki.omoshiroikamo.core.common.block.abstractClass;

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
import ruiseki.omoshiroikamo.api.crafting.CraftingState;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;

public abstract class AbstractMBModifierTE extends AbstractMachineTE {

    protected GameProfile player;
    @Getter
    protected boolean isFormed = false;

    protected abstract IStructureDefinition getStructureDefinition();

    public abstract int[][] getOffSet();

    public abstract String getStructurePieceName();

    protected ExtendedFacing getExtendedFacing() {
        return ExtendedFacing.of(ForgeDirection.getOrientation(getFacing()));
    }

    @SuppressWarnings("unchecked")
    protected boolean structureCheck(String piece, int ox, int oy, int oz) {
        clearStructureParts();

        boolean valid = getStructureDefinition()
            .check(this, piece, worldObj, getExtendedFacing(), xCoord, yCoord, zCoord, ox, oy, oz, false);

        if (valid && !isFormed) {
            isFormed = true;
            onFormed();
        } else if (!valid && isFormed) {
            isFormed = false;
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

    @Override
    public boolean canStartCrafting() {
        return isFormed && super.canStartCrafting();
    }

    @Override
    protected void onCrafting() {}

    @Override
    protected void finishCrafting() {
        resetCrafting();
    }

    @Override
    public int getCraftingEnergyCost() {
        return 0;
    }

    @Override
    protected CraftingState updateCraftingState() {
        if (!isCrafting()) {
            return CraftingState.IDLE;
        }

        if (!canContinueCrafting() || (!isCrafting() && !canStartCrafting())) {
            return CraftingState.ERROR;
        }

        return CraftingState.RUNNING;
    }

    public abstract int getBaseDuration();

    public abstract int getMinDuration();

    public abstract int getMaxDuration();

    public abstract float getSpeedMultiplier();

    public abstract void onFormed();

    @Override
    protected int getCraftingDuration() {
        float speed = getSpeedMultiplier();
        int base = getBaseDuration();

        int duration = (int) (base / speed);

        return Math.max(getMinDuration(), Math.min(duration, getMaxDuration()));
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        NBTTagCompound multiblock = new NBTTagCompound();
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
        this.isFormed = multiblock.getBoolean("isFormed");
        if (multiblock.hasKey("profile")) {
            this.player = PlayerUtils.profileFromNBT(multiblock.getCompoundTag("profile"));
        } else {
            this.player = null;
        }
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
        return isCrafting();
    }

    @Override
    public boolean processTasks(boolean redstone) {
        if (!isFormed) {
            return false;
        }

        return super.processTasks(redstone);
    }

}
