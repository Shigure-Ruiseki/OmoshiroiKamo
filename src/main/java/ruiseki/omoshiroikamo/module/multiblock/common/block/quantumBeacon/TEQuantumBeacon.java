package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.network.packet.PacketClientFlight;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.multiblock.client.render.BeamSegment;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.handler.QuantumBeaconEventHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public abstract class TEQuantumBeacon extends AbstractMBModifierTE implements IOKEnergySink {

    private final List<BlockPos> modifiers = new ArrayList<>();
    protected ModifierHandler modifierHandler = new ModifierHandler();

    /**
     * Tracks whether this Beacon granted flight to the player (to avoid conflicts
     * with other mods)
     */
    private boolean wasFlightGrantedByBeacon = false;

    // Beam segment cache for optimized rendering
    @SideOnly(Side.CLIENT)
    private List<BeamSegment> cachedBeamSegments;
    @SideOnly(Side.CLIENT)
    private long lastSegmentCacheTick = 0L;
    private static final int SEGMENT_CACHE_INTERVAL = 10; // Update every 10 ticks

    public TEQuantumBeacon(int eBuffSize) {
        this.energyStorage.setEnergyStorage(eBuffSize);
    }

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disablePlayerFlight();
    }

    @Override
    protected void clearStructureParts() {
        modifiers.clear();
        modifierHandler.setModifiers(Collections.emptyList());
        // Note: Do not call disablePlayerFlight here
        // Structure check runs every 20 ticks, calling it every time would cause toggle
        // issues
        // Flight is disabled in doUpdate() when the structure actually breaks
    }

    private void disablePlayerFlight() {
        // Do nothing if Beacon hasn't granted flight (don't steal flight from other
        // mods)
        if (!wasFlightGrantedByBeacon) {
            return;
        }
        wasFlightGrantedByBeacon = false;

        if (player == null || worldObj.isRemote) {
            return;
        }

        // Remove from global tracking (prevents re-granting on dimension change)
        QuantumBeaconEventHandler.unregisterFlightGranted(player.getId());

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr != null && !plr.capabilities.isCreativeMode) {
            plr.capabilities.allowFlying = false;
            if (plr.capabilities.isFlying) {
                plr.capabilities.isFlying = false;
            }
            plr.sendPlayerAbilities();
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(new PacketClientFlight(plr.getUniqueID(), false), plr);
        }
    }

    @Override
    public void doUpdate() {
        boolean wasFormed = isFormed;
        super.doUpdate();
        // Only disable flight when the structure breaks
        if (wasFormed && !isFormed) {
            disablePlayerFlight();
        }
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote && player != null && !redstoneCheckPassed) {
            disablePlayerFlight();
        }
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockPos coord = new BlockPos(x, y, z, worldObj);

        if (isModifierBlock(block) && !modifiers.contains(coord)) {
            modifiers.add(coord);
            return true;
        }
        return false;
    }

    private boolean isModifierBlock(Block block) {
        return block instanceof IModifierBlock;
    }

    /**
     * Check if the player is within the Beacon's effect range
     */
    private boolean isPlayerInRange(EntityPlayer plr) {
        int tier = getTier();
        QuantumBeaconConfig.BeaconTierRangeConfig rangeConfig = QuantumBeaconConfig.getRangeConfig(tier);

        int horizontalRange = rangeConfig.horizontalRange;
        int upwardRange = rangeConfig.upwardRange;
        int downwardRange = rangeConfig.downwardRange;

        double dx = Math.abs(plr.posX - (xCoord + 0.5));
        double dz = Math.abs(plr.posZ - (zCoord + 0.5));
        double dy = plr.posY - yCoord;

        // Check horizontal range (X and Z)
        if (dx > horizontalRange || dz > horizontalRange) {
            return false;
        }

        // Check vertical range (upward and downward)
        if (dy > upwardRange || dy < -downwardRange) {
            return false;
        }

        return true;
    }

    /**
     * Get the tier of this Beacon (1-6)
     */
    public abstract int getTier();

    private void addPlayerEffects() {
        if (player == null) return;

        // If player doesn't exist in this world (left chunk, etc.)
        // Reset the flight flag (will be re-granted when player returns)
        if (!PlayerUtils.doesPlayerExist(worldObj, player.getId())) {
            wasFlightGrantedByBeacon = false;
            return;
        }

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null) {
            wasFlightGrantedByBeacon = false;
            return;
        }

        // Check if player is within effect range
        if (!isPlayerInRange(plr)) {
            // Out of range: disable flight immediately if we granted it
            if (wasFlightGrantedByBeacon) {
                disablePlayerFlight();
            }
            return;
        }

        int potionDuration = getBaseDuration() * 2 + 300;

        boolean hasEnergy = plr.capabilities.isCreativeMode || hasEnergyForCrafting();

        // Check flight first, explicitly disable if energy is insufficient
        updateModifierHandler();
        updatePlayerFlight();
        if (!hasEnergy) return;

        applyPotionEffects(plr, potionDuration);
    }

    @Override
    public int getCraftingEnergyCost() {
        return (int) (modifierHandler.getAttributeMultiplier("energycost_fixed"));
    }

    @Override
    protected void onCrafting() {
        energyStorage.voidEnergy(getCraftingEnergyCost());
    }

    private void applyPotionEffects(EntityPlayer plr, int duration) {
        Object[][] effects = { { ModifierAttribute.P_SPEED.getAttributeName(), Potion.moveSpeed },
            { ModifierAttribute.P_NIGHT_VISION.getAttributeName(), Potion.nightVision },
            { ModifierAttribute.P_HASTE.getAttributeName(), Potion.digSpeed },
            { ModifierAttribute.P_STRENGTH.getAttributeName(), Potion.damageBoost },
            { ModifierAttribute.P_WATER_BREATHING.getAttributeName(), Potion.waterBreathing },
            { ModifierAttribute.P_REGEN.getAttributeName(), Potion.regeneration },
            { ModifierAttribute.P_SATURATION.getAttributeName(), Potion.field_76443_y },
            { ModifierAttribute.P_RESISTANCE.getAttributeName(), Potion.resistance },
            { ModifierAttribute.P_JUMP_BOOST.getAttributeName(), Potion.jump },
            { ModifierAttribute.P_FIRE_RESISTANCE.getAttributeName(), Potion.fireResistance } };

        for (Object[] entry : effects) {
            addPotionEffect(plr, (String) entry[0], duration, (Potion) entry[1]);
        }
    }

    private void addPotionEffect(EntityPlayer plr, String pe, int duration, Potion effect) {
        if (plr == null || plr.worldObj.isRemote) return;

        if (!modifierHandler.hasAttribute(pe)) {
            return;
        }
        int rawLevel = (int) (modifierHandler.getAttributeMultiplier(pe));
        int maxLevel = maxPotionLevel(pe);

        // Clamp: 0 <= level <= maxLevel
        int level = Math.max(0, Math.min(rawLevel, maxLevel) - 1);

        plr.addPotionEffect(new PotionEffect(effect.id, duration, level, true));

    }

    private void updatePlayerFlight() {
        if (player == null || worldObj.isRemote) return;

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null || plr.capabilities.isCreativeMode) return;

        boolean hasFlightModifier = modifierHandler
            .hasAttribute(ModifierAttribute.E_FLIGHT_CREATIVE.getAttributeName());
        boolean shouldHaveFlight = hasFlightModifier && hasEnergyForCrafting();

        // Grant flight if Beacon should provide it
        if (shouldHaveFlight) {
            if (!plr.capabilities.allowFlying) {
                plr.capabilities.allowFlying = true;
                plr.sendPlayerAbilities();
                OmoshiroiKamo.instance.getPacketHandler()
                    .sendToAllAround(new PacketClientFlight(plr.getUniqueID(), true), plr);
            }
            wasFlightGrantedByBeacon = true;
            // Register in global tracking (for re-granting after dimension change)
            QuantumBeaconEventHandler
                .registerFlightGranted(player.getId(), worldObj.provider.dimensionId, xCoord, yCoord, zCoord);
        }
        // Revoke flight only if Beacon was granting it and should no longer
        else if (wasFlightGrantedByBeacon) {
            wasFlightGrantedByBeacon = false;
            // Remove from global tracking
            QuantumBeaconEventHandler.unregisterFlightGranted(player.getId());
            plr.capabilities.allowFlying = false;
            if (plr.capabilities.isFlying) {
                plr.capabilities.isFlying = false;
            }
            plr.sendPlayerAbilities();
            OmoshiroiKamo.instance.getPacketHandler()
                .sendToAllAround(new PacketClientFlight(plr.getUniqueID(), false), plr);
        }
        // If shouldHaveFlight=false and wasFlightGrantedByBeacon=false, do nothing
        // (another mod may have granted flight)
    }

    protected abstract int maxPotionLevel(String attribute);

    @Override
    public int getBaseDuration() {
        return 40;
    }

    @Override
    public int getMinDuration() {
        return getBaseDuration();
    }

    @Override
    public int getMaxDuration() {
        return getBaseDuration();
    }

    @Override
    public float getSpeedMultiplier() {
        return 0.0F;
    }

    private void updateModifierHandler() {
        List<IModifierBlock> mods = new ArrayList<>();
        for (BlockPos pos : this.modifiers) {
            Block block = pos.getBlock();
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
            }
        }

        modifierHandler.setModifiers(mods);
        modifierHandler.calculateAttributeMultipliers();
    }

    @Override
    public boolean canStartCrafting() {
        updateModifierHandler();
        return isRedstoneActive() && isFormed && hasEnergyForCrafting();
    }

    @Override
    protected void finishCrafting() {
        super.finishCrafting();
        this.addPlayerEffects();
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
        if (tileEntity instanceof TEQuantumBeaconT1) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_NANO_BOT_BEACON_T1.get());
        }
        if (tileEntity instanceof TEQuantumBeaconT4) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_NANO_BOT_BEACON_T4.get());
        }
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    // ========== Beam Rendering Methods ==========

    /**
     * Check if the Beacon can see the sky (required for beam to show if requireSky
     * is true)
     */
    public boolean canSeeSky() {
        if (!QuantumBeaconConfig.general.requireSky) {
            return true;
        }
        return worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
    }

    @SideOnly(Side.CLIENT)
    public float getBeamProgress() {
        if (!QuantumBeaconConfig.general.enableBeam || !isFormed() || !canSeeSky()) {
            return 0f;
        }
        return 1.0F;
    }

    /**
     * Override the render bounding box to include the entire beam (from beacon to
     * build height).
     * This prevents frustum culling from hiding the beam when the player is not
     * looking at the beacon.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, Double.POSITIVE_INFINITY, zCoord + 1);
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
     * Calculate beam segments by scanning blocks above the beacon.
     * This extracts the color scanning logic from the TESR.
     */
    @SideOnly(Side.CLIENT)
    private List<BeamSegment> calculateBeamSegments() {
        List<BeamSegment> segments = new ArrayList<>();

        // Current color (start with white)
        float[] currentColor = new float[] { 1.0f, 1.0f, 1.0f };
        int segmentStart = 0;
        boolean isFirstColoredBlock = true;

        // Scan upward from beacon
        for (int relY = 1; relY < 256 - yCoord; relY++) {
            int worldY = yCoord + relY;
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
                segmentStart = relY - 1;
            }

            // Stop at opaque blocks
            if (block.isOpaqueCube()) {
                int finalSegmentEnd = relY;
                int finalSegmentHeight = finalSegmentEnd - segmentStart;
                if (finalSegmentHeight > 0) {
                    segments.add(new BeamSegment(segmentStart, finalSegmentHeight, currentColor));
                }
                return segments;
            }
        }

        // Render the final segment to build height
        int finalHeight = (256 - yCoord) - segmentStart;
        if (finalHeight > 0) {
            segments.add(new BeamSegment(segmentStart, finalHeight, currentColor));
        }

        return segments;
    }

}
