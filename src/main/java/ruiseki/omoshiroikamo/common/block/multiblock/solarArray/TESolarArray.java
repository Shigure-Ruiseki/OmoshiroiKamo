package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import ruiseki.omoshiroikamo.api.energy.IPowerContainer;
import ruiseki.omoshiroikamo.api.energy.PowerDistributor;
import ruiseki.omoshiroikamo.api.energy.PowerHandlerUtils;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.network.PacketHandler;
import ruiseki.omoshiroikamo.common.network.PacketPowerStorage;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class TESolarArray extends AbstractMultiBlockModifierTE implements IEnergyProvider, IPowerContainer {

    private PowerDistributor powerDis;
    private int lastCollectionValue = -1;
    private static final int CHECK_INTERVAL = 100;

    private int storedEnergyRF = 0;
    protected float lastSyncPowerStored = -1;
    private final EnergyStorage energyStorage;
    private ModifierHandler modifierHandler = new ModifierHandler();
    private List<BlockCoord> modifiers = new ArrayList<>();

    public TESolarArray(int energyGen) {
        this.energyStorage = new EnergyStorage(energyGen * getBaseDuration());
    }

    @Override
    public String getMachineName() {
        return ModObject.blockSolarArray.unlocalisedName;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block nbid) {
        super.onNeighborBlockChange(world, x, y, z, nbid);
        if (powerDis != null) {
            powerDis.neighboursChanged();
        }
    }

    @Override
    public void doUpdate() {
        super.doUpdate();
        boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
        transmitEnergy();
    }

    public abstract int getEnergyPerTick();

    @Override
    public int getBaseDuration() {
        return 10;
    }

    @Override
    public int getMinDuration() {
        return this.getBaseDuration();
    }

    @Override
    public int getMaxDuration() {
        return this.getBaseDuration();
    }

    @Override
    public float getSpeedMultiplier() {
        return 1.0F;
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

        this.modifierHandler.setModifiers(mods);
        this.modifierHandler.calculateAttributeMultipliers();
        return true;
    }

    @Override
    public void onProcessTick() {
        collectEnergy();
    }

    @Override
    public void onProcessComplete() {
        collectEnergy();
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
        if (tileEntity instanceof TESolarArrayT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_SOLAR_ARRAY_T1.get());
        }
        if (tileEntity instanceof TESolarArrayT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_SOLAR_ARRAY_T4.get());
        }
    }

    private void transmitEnergy() {
        if (powerDis == null) {
            powerDis = new PowerDistributor(getLocation());
        }

        int canTransmit = Math.min(getEnergyStored(), getMaxEnergyStored());
        if (canTransmit <= 0) {
            return;
        }

        int transmitted = powerDis.transmitEnergy(worldObj, canTransmit);
        setEnergyStored(getEnergyStored() - transmitted);
    }

    float calculateLightRatio() {
        return calculateLightRatio(worldObj, xCoord, yCoord, zCoord);
    }

    private void collectEnergy() {
        if (canSeeSun()) {
            if (lastCollectionValue == -1 || shouldDoWorkThisTick(CHECK_INTERVAL)) {
                lastCollectionValue = getEnergyRegen();
            }
            if (lastCollectionValue > 0) {
                this.setEnergyStored(Math.min(lastCollectionValue + this.getEnergyStored(), this.getMaxEnergyStored()));
            }
        }
    }

    boolean canSeeSun() {
        return worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
    }

    private int getEnergyRegen() {
        float fromSun = calculateLightRatio();
        float isRaining = worldObj.isRaining() ? (2f / 3f) : 1f;
        int formPiezo = worldObj.isRaining()
            ? (int) (this.modifierHandler.getAttributeMultiplier("piezo") * ((float) getEnergyPerTick() / 64))
            : 0;
        int gen = Math.round(getEnergyPerTick() * fromSun * isRaining) + formPiezo;
        return Math.min(getEnergyPerTick(), gen);
    }

    public static float calculateLightRatio(World world, int x, int y, int z) {
        int lightValue = world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) - world.skylightSubtracted;
        float sunAngle = world.getCelestialAngleRadians(1.0F);

        if (sunAngle < (float) Math.PI) {
            sunAngle += (0.0F - sunAngle) * 0.2F;
        } else {
            sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
        }

        lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

        lightValue = MathHelper.clamp_int(lightValue, 0, 15);

        return lightValue / 15f;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
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

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
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

        if (block == ModBlocks.MODIFIER_PIEZO.get()) {
            modifiers.add(coord);
            return true;
        }
        return false;
    }

    @Override
    protected void clearStructureParts() {
        modifiers.clear();
        modifierHandler = new ModifierHandler();
    }

    public abstract int getTier();

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY, storedEnergyRF);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        int energy;
        if (root.hasKey("storedEnergy")) {
            float storedEnergyMJ = root.getFloat("storedEnergy");
            energy = (int) (storedEnergyMJ * 10);
        } else {
            energy = root.getInteger(PowerHandlerUtils.STORED_ENERGY_NBT_KEY);
        }
        setEnergyStored(energy);
    }
}
