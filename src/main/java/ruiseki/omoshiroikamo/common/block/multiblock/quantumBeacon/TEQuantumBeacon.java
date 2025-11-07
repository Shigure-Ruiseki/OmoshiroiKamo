package ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.IPowerContainer;
import ruiseki.omoshiroikamo.api.energy.PowerHandlerUtils;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketNBBClientFlight;
import ruiseki.omoshiroikamo.common.network.PacketPowerStorage;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class TEQuantumBeacon extends AbstractMultiBlockModifierTE implements IEnergyReceiver, IPowerContainer {

    private int storedEnergyRF = 0;
    private float lastSyncPowerStored = -1;

    private final EnergyStorage energyStorage;
    private final List<BlockCoord> modifiers = new ArrayList<>();
    protected ModifierHandler modifierHandler = new ModifierHandler();

    private boolean dealsWithFlight = false;

    public TEQuantumBeacon(int eBuffSize) {
        this.energyStorage = new EnergyStorage(eBuffSize);
    }

    @Override
    public void onChunkUnload() {
        this.removePlayerEffects();
        super.onChunkUnload();
    }

    @Override
    protected void clearStructureParts() {
        modifiers.clear();
        modifierHandler = new ModifierHandler();
        this.removePlayerEffects();
    }

    @Override
    public void doUpdate() {
        super.doUpdate();
        boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockCoord coord = new BlockCoord(x, y, z);
        if (modifiers.contains(coord)) {
            return false;
        }

        if (isModifierBlock(block)) {
            modifiers.add(coord);
            return true;
        }
        return false;
    }

    private boolean isModifierBlock(Block block) {
        return block == ModBlocks.MODIFIER_SPEED.get() || block == ModBlocks.MODIFIER_FLIGHT.get()
            || block == ModBlocks.MODIFIER_NIGHT_VISION.get()
            || block == ModBlocks.MODIFIER_HASTE.get()
            || block == ModBlocks.MODIFIER_STRENGTH.get()
            || block == ModBlocks.MODIFIER_WATER_BREATHING.get()
            || block == ModBlocks.MODIFIER_REGENERATION.get()
            || block == ModBlocks.MODIFIER_SATURATION.get()
            || block == ModBlocks.MODIFIER_RESISTANCE.get()
            || block == ModBlocks.MODIFIER_JUMP_BOOST.get()
            || block == ModBlocks.MODIFIER_FIRE_RESISTANCE.get();
    }

    private void addPlayerEffects() {
        if (player == null || !PlayerUtils.doesPlayerExist(worldObj, player.getId())) {
            return;
        }

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null || modifierHandler == null) {
            return;
        }

        int potionDuration = getBaseDuration() * 2 + 300;
        int energyCost = (int) (modifierHandler.getAttributeMultiplier("energycost_fixed") * getBaseDuration());

        if (getEnergyStored() < energyCost) {
            removePlayerEffects();
            return;
        }

        setEnergyStored(getEnergyStored() - energyCost);
        updateFlightCapability(plr);

        applyPotionEffects(plr, potionDuration);
    }

    private void applyPotionEffects(EntityPlayer plr, int duration) {
        Object[][] effects = {{ModifierAttribute.P_SPEED.getAttributeName(), Potion.moveSpeed},
            {ModifierAttribute.P_NIGHT_VISION.getAttributeName(), Potion.nightVision},
            {ModifierAttribute.P_HASTE.getAttributeName(), Potion.digSpeed},
            {ModifierAttribute.P_STRENGTH.getAttributeName(), Potion.damageBoost},
            {ModifierAttribute.P_WATER_BREATHING.getAttributeName(), Potion.waterBreathing},
            {ModifierAttribute.P_REGEN.getAttributeName(), Potion.regeneration},
            {ModifierAttribute.P_SATURATION.getAttributeName(), Potion.field_76443_y},
            {ModifierAttribute.P_RESISTANCE.getAttributeName(), Potion.resistance},
            {ModifierAttribute.P_JUMP_BOOST.getAttributeName(), Potion.jump},
            {ModifierAttribute.P_FIRE_RESISTANCE.getAttributeName(), Potion.fireResistance}};

        for (Object[] entry : effects) {
            addPotionEffect(plr, (String) entry[0], duration, (Potion) entry[1]);
        }
    }

    private void addPotionEffect(EntityPlayer plr, String pe, int potionDuration, Potion effect) {
        if (this.modifierHandler.hasAttribute(pe)) {
            int level = (int) Math
                .min(this.modifierHandler.getAttributeMultiplier(pe) - 1.0F, (float) (this.maxPotionLevel(pe) - 1));

            if (level >= 0) {
                plr.addPotionEffect(new PotionEffect(effect.id, potionDuration, level, true));
            }
        }
    }

    private void updateFlightCapability(EntityPlayer plr) {
        if (plr.capabilities.isCreativeMode) {
            return;
        }

        boolean canFly = modifierHandler.hasAttribute(ModifierAttribute.E_FLIGHT_CREATIVE.getAttributeName());

        if (canFly && !dealsWithFlight) {
            plr.capabilities.allowFlying = true;
            plr.sendPlayerAbilities();
            dealsWithFlight = true;
            PacketHandler.sendToAllAround(new PacketNBBClientFlight(plr.getUniqueID(), true), plr);

        } else if (!canFly && dealsWithFlight) {
            plr.capabilities.isFlying = false;
            plr.capabilities.allowFlying = false;
            plr.sendPlayerAbilities();
            dealsWithFlight = false;
            PacketHandler.sendToAllAround(new PacketNBBClientFlight(plr.getUniqueID(), false), plr);
        }
    }

    private void removePlayerEffects() {
        if (player == null || modifierHandler == null) {
            return;
        }

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null || plr.capabilities.isCreativeMode) {
            return;
        }

        boolean hasFlightAttribute = modifierHandler
            .hasAttribute(ModifierAttribute.E_FLIGHT_CREATIVE.getAttributeName());
        if (dealsWithFlight && !hasFlightAttribute) {
            plr.capabilities.allowFlying = false;
            plr.capabilities.isFlying = false;
            dealsWithFlight = false;
            plr.sendPlayerAbilities();
            PacketHandler.sendToAllAround(new PacketNBBClientFlight(plr.getUniqueID(), false), plr);
        }
    }

    protected abstract int maxPotionLevel(String var1);

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

    @Override
    public boolean canProcess() {
        List<IModifierBlock> mods = new ArrayList<>();
        for (BlockCoord coord : this.modifiers) {
            Block blk = coord.getBlock(worldObj);
            if (blk instanceof IModifierBlock) {
                mods.add((IModifierBlock) blk);
            }
        }

        modifierHandler.setModifiers(mods);
        modifierHandler.calculateAttributeMultipliers();

        return this.player != null;
    }

    @Override
    public void onProcessTick() {

    }

    @Override
    public void onProcessComplete() {
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
        TileEntity tileEntity = getLocation().getTileEntity(worldObj);
        if (tileEntity instanceof TEQuantumBeaconT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_NANO_BOT_BEACON_T1.get());
        }
        if (tileEntity instanceof TEQuantumBeaconT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_NANO_BOT_BEACON_T4.get());
        }
    }

    @Override
    public String getMachineName() {
        return "nanoBotBeacon";
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
        storedEnergyRF = Math.min(storedEnergy, getMaxEnergyStored());
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getMaxEnergyReceived() {
        return energyStorage.getMaxReceive();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        int energy;
        if (root.hasKey("storedEnergy")) {
            float storedEnergyMJ = root.getFloat("storedEnergy");
            energy = (int) (storedEnergyMJ * 10);
        } else {
            energy = root.getInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY);
        }
        setEnergyStored(energy);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        this.storedEnergyRF = root.getInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY);
        this.dealsWithFlight = root.getBoolean("dflight");
    }

}
