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

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import ruiseki.omoshiroikamo.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IEnergySink;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketNBBClientFlight;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class TEQuantumBeacon extends AbstractMBModifierTE implements IEnergySink {

    private final List<BlockPos> modifiers = new ArrayList<>();
    protected ModifierHandler modifierHandler = new ModifierHandler();

    private boolean dealsWithFlight = false;

    public TEQuantumBeacon(int eBuffSize) {
        this.energyStorage = new EnergyStorage(eBuffSize);
    }

    @Override
    public String getMachineName() {
        return "nanoBotBeacon";
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
    protected boolean processTasks(boolean redstoneCheckPassed) {

        if (redstoneCheckPassed) {
            EntityPlayer plr = PlayerUtils.getPlayerFromWorld(worldObj, player.getId());
            if (plr != null && !plr.capabilities.isCreativeMode && dealsWithFlight) {
                plr.capabilities.allowFlying = false;
                plr.capabilities.isFlying = false;
                dealsWithFlight = false;
                plr.sendPlayerAbilities();
                PacketHandler.sendToAllAround(new PacketNBBClientFlight(plr.getUniqueID(), false), plr);
            }
        }
        transmitEnergy();
        return super.processTasks(redstoneCheckPassed);
    }

    private void transmitEnergy() {

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            EnergyTransfer transfer = new EnergyTransfer();
            TileEntity adjacent = this.getWorldObj()
                .getTileEntity(this.xCoord + side.offsetX, this.yCoord + side.offsetY, this.zCoord + side.offsetZ);
            if (adjacent == null) {
                continue;
            }
            transfer.pull(this, side, adjacent);
            transfer.transfer();
        }
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockPos coord = new BlockPos(x, y, z);
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
        for (BlockPos pos : this.modifiers) {
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
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
        TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord, zCoord);
        if (tileEntity instanceof TEQuantumBeaconT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_NANO_BOT_BEACON_T1.get());
        }
        if (tileEntity instanceof TEQuantumBeaconT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_NANO_BOT_BEACON_T4.get());
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setBoolean("dflight", dealsWithFlight);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        this.dealsWithFlight = root.getBoolean("dflight");
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

}
