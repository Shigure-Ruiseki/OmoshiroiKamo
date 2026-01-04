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
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;

/**
 * Base class for multiblock modifier tile entities.
 * <p>
 * Handles:
 * <ul>
 * <li>Multiblock structure formation and validation</li>
 * <li>Player ownership via GameProfile</li>
 * <li>Crafting process integration</li>
 * </ul>
 */
public abstract class AbstractMBModifierTE extends AbstractMachineTE {

    /** Owner of the multiblock machine. */
    protected GameProfile player;

    /** Flag indicating whether the multiblock structure is fully formed. */
    @Getter
    protected boolean isFormed = false;

    /**
     * Returns the multiblock structure definition used for validation.
     *
     * @return structure definition
     */
    @SuppressWarnings("unchecked")
    protected abstract IStructureDefinition getStructureDefinition();

    /**
     * Returns offsets for structure validation based on tier.
     *
     * @return 2D array of offsets [tier][x,y,z]
     */
    public abstract int[][] getOffSet();

    /**
     * Returns the name of the structure piece to check.
     *
     * @return structure piece name
     */
    public abstract String getStructurePieceName();

    /**
     * Returns the facing of the multiblock structure.
     *
     * @return ExtendedFacing instance
     */
    protected ExtendedFacing getExtendedFacing() {
        return ExtendedFacing.of(ForgeDirection.getOrientation(2));
    }

    /**
     * Checks if the multiblock structure is valid.
     *
     * @param piece structure piece name
     * @param ox    x offset
     * @param oy    y offset
     * @param oz    z offset
     * @return true if structure is formed
     */
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

    /**
     * Clears stored multiblock structure parts.
     */
    protected abstract void clearStructureParts();

    /**
     * Attempts to add a TileEntity to the machine structure.
     *
     * @param tile tile entity to add
     * @return true if successfully added
     */
    protected boolean addToMachine(TileEntity tile) {
        return false;
    }

    /**
     * Attempts to add a block to the machine structure.
     *
     * @param block block to add
     * @param meta  block metadata
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param z     z-coordinate
     * @return true if successfully added
     */
    protected boolean addToMachine(Block block, int meta, int x, int y, int z) {
        return false;
    }

    /**
     * Returns the tier of the multiblock machine.
     *
     * @return tier number
     */
    public abstract int getTier();

    /**
     * Updates the tile entity each tick.
     * Checks multiblock structure before processing tasks.
     */
    @Override
    public void doUpdate() {
        // Always call super first to ensure IC2 registration and energy sync
        super.doUpdate();

        if (!shouldDoWorkThisTick(20) && isFormed) {
            return;
        }

        boolean valid = structureCheck(
            getStructurePieceName(),
            getOffSet()[getTier() - 1][0],
            getOffSet()[getTier() - 1][1],
            getOffSet()[getTier() - 1][2]);

        if (!valid) return;
    }

    /**
     * Determines if crafting can start.
     * Crafting requires the multiblock to be formed.
     */
    @Override
    public boolean canStartCrafting() {
        return isFormed && super.canStartCrafting();
    }

    @Override
    protected void onCrafting() {
        // NO OP
    }

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

    /**
     * Returns base crafting duration.
     *
     * @return base duration in ticks
     */
    public abstract int getBaseDuration();

    /**
     * Returns minimum allowed crafting duration.
     *
     * @return minimum duration
     */
    public abstract int getMinDuration();

    /**
     * Returns maximum allowed crafting duration.
     *
     * @return maximum duration
     */
    public abstract int getMaxDuration();

    /**
     * Returns speed multiplier applied to base duration.
     *
     * @return speed multiplier
     */
    public abstract float getSpeedMultiplier();

    /**
     * Called when the multiblock structure is successfully formed.
     */
    public abstract void onFormed();

    @Override
    protected int getCraftingDuration() {
        float speedMultiplier = getSpeedMultiplier();
        int baseDuration = getBaseDuration();

        int duration = (int) (baseDuration * speedMultiplier);

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

    /**
     * Sets the owner of the machine from a player.
     *
     * @param plr EntityPlayer instance
     */
    public void setPlayer(EntityPlayer plr) {
        if (plr != null) {
            this.player = plr.getGameProfile();
        }
    }

    /**
     * Sets the owner of the machine from a UUID.
     *
     * @param plr player UUID
     */
    public void setPlayer(UUID plr) {
        EntityPlayer pl = PlayerUtils.getPlayerFromWorld(this.worldObj, plr);
        if (pl != null) {
            this.player = pl.getGameProfile();
        }

    }

    /**
     * Returns the UUID of the player who owns the machine.
     *
     * @return player UUID
     */
    public UUID getPlayerID() {
        return this.player.getId();
    }

    /**
     * Returns the GameProfile of the player who owns the machine.
     *
     * @return player GameProfile
     */
    public GameProfile getPlayerProfile() {
        return this.player;
    }

    @Override
    public boolean isActive() {
        return isCrafting();
    }

    /**
     * Processes tasks only if the multiblock structure is formed.
     *
     * @param redstone true if redstone allows operation
     * @return true if tasks processed
     */
    @Override
    public boolean processTasks(boolean redstone) {
        if (!isFormed) {
            return false;
        }

        return super.processTasks(redstone);
    }

}
