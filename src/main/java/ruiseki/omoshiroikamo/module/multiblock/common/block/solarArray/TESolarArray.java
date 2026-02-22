package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;
import ruiseki.omoshiroikamo.module.multiblock.common.block.modifier.ModifierHandler;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;

public abstract class TESolarArray extends AbstractMBModifierTE implements IOKEnergySource {

    private int lastCollectionValue = -1;
    private static final int CHECK_INTERVAL = 100;

    private ModifierHandler modifierHandler = new ModifierHandler();
    private List<BlockPos> modifiers = new ArrayList<>();
    private Map<BlockPos, Integer> cellTiers = new HashMap<>();

    public TESolarArray(int energyGen) {
        energyStorage.setEnergyStorage(energyGen * getBaseDuration());
    }

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
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
    public boolean canStartCrafting() {
        List<IModifierBlock> mods = new ArrayList<>();

        for (BlockPos pos : this.modifiers) {
            Block block = pos.getBlock(worldObj);
            if (block instanceof IModifierBlock) {
                mods.add((IModifierBlock) block);
            }
        }

        this.modifierHandler.setModifiers(mods);
        this.modifierHandler.calculateAttributeMultipliers();

        return super.canStartCrafting();
    }

    @Override
    protected void onCrafting() {
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
        TileEntity tileEntity = getPos().getTileEntity(worldObj);
        if (tileEntity instanceof TESolarArrayT1) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_SOLAR_ARRAY_T1.get());
        }
        if (tileEntity instanceof TESolarArrayT4) {
            player.triggerAchievement(MultiBlockAchievements.ASSEMBLE_SOLAR_ARRAY_T4.get());
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
        float light = calculateLightRatio();
        float rainFactor = worldObj.isRaining() ? (2f / 3f) : 1f;
        int cellTotal = calculateTotalEnergy();
        int piezo = worldObj.isRaining()
            ? (int) (this.modifierHandler.getAttributeMultiplier("piezo") * (cellTotal / 64f))
            : 0;
        int regen = Math.round(cellTotal * light * rainFactor) + piezo;
        return Math.min(regen, getEnergyPerTick());
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
            transfer.push(this, side, adjacent);
            transfer.transfer();
        }
    }

    public int getCellEnergy(int cellTier) {
        int base = SolarArrayConfig.cellSettings.baseGeneration;
        float multiplier = SolarArrayConfig.cellSettings.tierMultiplier;
        return (int) Math.round(base * Math.pow(multiplier, cellTier));
    }

    public int calculateTotalEnergy() {
        int total = 0;
        for (int tier : cellTiers.values()) {
            total += getCellEnergy(tier);
        }
        return total;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public boolean addToMachine(Block block, int meta, int x, int y, int z) {
        if (block == null) {
            return false;
        }

        BlockPos pos = new BlockPos(x, y, z);
        if (block == MultiBlockBlocks.SOLAR_CELL.getBlock() && !cellTiers.containsKey(pos)) {
            cellTiers.put(pos, meta);
            return true;
        }

        if (block instanceof IModifierBlock && !modifiers.contains(pos)) {
            modifiers.add(pos);
            return true;
        }
        return false;
    }

    @Override
    protected void clearStructureParts() {
        cellTiers.clear();
        modifiers.clear();
        modifierHandler = new ModifierHandler();
    }
}
