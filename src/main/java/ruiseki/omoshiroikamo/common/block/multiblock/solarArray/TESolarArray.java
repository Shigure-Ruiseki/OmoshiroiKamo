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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;
import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import cofh.api.energy.EnergyStorage;
import ruiseki.omoshiroikamo.api.energy.capability.EnergySource;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergySource;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.common.block.multiblock.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.common.init.ModAchievements;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.PlayerUtils;

public abstract class TESolarArray extends AbstractMBModifierTE implements IEnergySource, CapabilityProvider {

    private int lastCollectionValue = -1;
    private static final int CHECK_INTERVAL = 100;

    private final EnergyStorage energyStorage;
    private ModifierHandler modifierHandler = new ModifierHandler();
    private List<BlockPos> modifiers = new ArrayList<>();

    public TESolarArray(int energyGen) {
        this.energyStorage = new EnergyStorage(energyGen * getBaseDuration());
    }

    @Override
    public String getMachineName() {
        return ModObject.blockSolarArray.unlocalisedName;
    }

    @Override
    protected boolean processTasks(boolean redstoneCheckPassed) {
        transmitEnergy();
        return super.processTasks(redstoneCheckPassed);
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

        for (BlockPos pos : this.modifiers) {
            Block block = worldObj.getBlock(pos.x, pos.y, pos.z);
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
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
        TileEntity tileEntity = worldObj.getTileEntity(xCoord, yCoord, zCoord);
        if (tileEntity instanceof TESolarArrayT1) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_SOLAR_ARRAY_T1.get());
        }
        if (tileEntity instanceof TESolarArrayT4) {
            player.triggerAchievement(ModAchievements.ASSEMBLE_SOLAR_ARRAY_T4.get());
        }
    }

    float calculateLightRatio() {
        return calculateLightRatio(worldObj, xCoord, yCoord + 1, zCoord);
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

    private void transmitEnergy() {

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            EnergyTransfer transfer = new EnergyTransfer();
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
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        int extracted = Math.min(maxExtract, getEnergyStored());
        if (!simulate) {
            setEnergyStored(getEnergyStored() - extracted);
        }
        return extracted;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public void setEnergyStored(int storedEnergy) {
        int storedEnergyRF = Math.min(storedEnergy, getMaxEnergyStored());
        energyStorage.setEnergyStored(storedEnergyRF);
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
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

        if (block == ModBlocks.MODIFIER_PIEZO.get()) {
            modifiers.add(pos);
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
        energyStorage.writeToNBT(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        energyStorage.readFromNBT(root);
    }

    @Override
    public <T> @Nullable T getCapability(@NotNull Class<T> capability, @NotNull ForgeDirection side) {
        if (capability == EnergySource.class) {
            return capability.cast(new OKEnergySource(this, side));
        }

        return null;
    }
}
