package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.energy.IEnergySink;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.core.common.network.PacketClientFlight;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;

public abstract class TEQuantumBeacon extends AbstractMBModifierTE implements IEnergySink {

    private final List<BlockPos> modifiers = new ArrayList<>();
    protected ModifierHandler modifierHandler = new ModifierHandler();

    public TEQuantumBeacon(int eBuffSize) {
        this.energyStorage.setEnergyStorage(eBuffSize);
    }

    @Override
    public String getMachineName() {
        return "nanoBotBeacon";
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        disablePlayerFlight();
    }

    @Override
    protected void clearStructureParts() {
        modifiers.clear();
        modifierHandler = new ModifierHandler();
        disablePlayerFlight();
    }

    private void disablePlayerFlight() {
        if (player == null || worldObj.isRemote) {
            return;
        }
        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr != null && !plr.capabilities.isCreativeMode) {
            plr.capabilities.allowFlying = false;
            plr.sendPlayerAbilities();
            PacketHandler.sendToAllAround(new PacketClientFlight(plr.getUniqueID(), false), plr);
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

    private void addPlayerEffects() {
        if (player == null || !PlayerUtils.doesPlayerExist(worldObj, player.getId())) return;

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null) return;

        int potionDuration = getBaseDuration() * 2 + 300;

        int energyCost = (int) (modifierHandler.getAttributeMultiplier("energycost_fixed") * getBaseDuration());
        if (getEnergyStored() < energyCost && !plr.capabilities.isCreativeMode) return;

        setEnergyStored(getEnergyStored() - energyCost);
        updatePlayerFlight();
        applyPotionEffects(plr, potionDuration);
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

    private void addPotionEffect(EntityPlayer plr, String pe, int potionDuration, Potion effect) {
        if (this.modifierHandler.hasAttribute(pe)) {
            int level = (int) Math
                .min(this.modifierHandler.getAttributeMultiplier(pe) - 1.0F, (float) (this.maxPotionLevel(pe) - 1));

            if (level >= 0) {
                plr.addPotionEffect(new PotionEffect(effect.id, potionDuration, level, true));
            }
        }
    }

    private void updatePlayerFlight() {
        if (player == null || worldObj.isRemote) return;

        EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
        if (plr == null || plr.capabilities.isCreativeMode) return;

        boolean hasFlightModifier = modifierHandler
            .hasAttribute(ModifierAttribute.E_FLIGHT_CREATIVE.getAttributeName());
        boolean enoughEnergy = getEnergyStored()
            >= (int) (modifierHandler.getAttributeMultiplier("energycost_fixed") * getBaseDuration());
        boolean shouldHaveFlight = hasFlightModifier && enoughEnergy;

        if (plr.capabilities.allowFlying == shouldHaveFlight) return;

        plr.capabilities.allowFlying = shouldHaveFlight;

        if (!shouldHaveFlight && plr.capabilities.isFlying) {
            plr.capabilities.isFlying = false;
        }

        plr.sendPlayerAbilities();
        PacketHandler.sendToAllAround(new PacketClientFlight(plr.getUniqueID(), shouldHaveFlight), plr);

        if (shouldHaveFlight && !plr.capabilities.isCreativeMode) {
            setEnergyStored(
                getEnergyStored()
                    - (int) (modifierHandler.getAttributeMultiplier("energycost_fixed") * getBaseDuration()));
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
        for (BlockPos pos : this.modifiers) {
            Block block = pos.getBlock();
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
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

}
