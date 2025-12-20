package ruiseki.omoshiroikamo.common.entity.cow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.jetbrains.annotations.NotNull;

import com.kuba6000.mobsinfo.api.IMobInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;

import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.MobTrait;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.plugin.waila.IWailaEntityInfoProvider;

@Optional.Interface(iface = "com.kuba6000.mobsinfo.api.IMobInfoProvider", modid = "mobsinfo")
public class EntityCowsCow extends EntityCow implements IMobStats, IWailaEntityInfoProvider, IMobInfoProvider {

    public static final String PROGRESS_NBT = "Progress";
    public static final String MILK_TANK_NBT = "MilkTank";

    private final Map<MobTrait, Integer> traits = new HashMap<>();
    public int timeUntilNextMilk;
    public SmartTank milkTank;

    public EntityCowsCow(World worldIn) {
        super(worldIn);
        timeUntilNextMilk = this.rand.nextInt(6000) + 6000;
        milkTank = new SmartTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
    }

    @Override
    public void setType(int type) {
        dataWatcher.updateObject(20, type);
        isImmuneToFire = getCowDescription().isImmuneToFire();
        resetTimeUntilNextMilk();
    }

    @Override
    public int getType() {
        return dataWatcher.getWatchableObjectInt(20);
    }

    @Override
    public boolean getStatsAnalyzed() {
        return this.dataWatcher.getWatchableObjectByte(24) != 0;
    }

    @Override
    public void setStatsAnalyzed(boolean val) {
        this.dataWatcher.updateObject(24, (byte) (val ? 1 : 0));
    }

    @Override
    public int getBaseGrowth() {
        return this.dataWatcher.getWatchableObjectInt(21);
    }

    @Override
    public void setBaseGrowth(int growth) {
        int clamped = MathHelper.clamp_int(growth, 1, getMaxGrowthStat());
        this.dataWatcher.updateObject(21, clamped);
    }

    @Override
    public int getBaseGain() {
        return this.dataWatcher.getWatchableObjectInt(22);
    }

    @Override
    public void setBaseGain(int gain) {
        int clamped = MathHelper.clamp_int(gain, 1, getMaxGainStat());
        this.dataWatcher.updateObject(22, clamped);
    }

    @Override
    public int getBaseStrength() {
        return this.dataWatcher.getWatchableObjectInt(23);
    }

    @Override
    public void setBaseStrength(int strength) {
        int clamped = MathHelper.clamp_int(strength, 1, getMaxStrengthStat());
        this.dataWatcher.updateObject(23, clamped);
    }

    @Override
    public Map<MobTrait, Integer> getTraits() {
        return traits;
    }

    @Override
    public void setGrowingAge(int age) {
        age = setStatsGrowingAge(age);
        super.setGrowingAge(age);
    }

    public int getMilkProgress() {
        return dataWatcher.getWatchableObjectInt(25);
    }

    public void updateMilkProgress() {
        this.dataWatcher.updateObject(25, timeUntilNextMilk);
    }

    public ResourceLocation getTexture() {
        CowsRegistryItem chickenDescription = getCowDescription();
        return chickenDescription.getTexture();
    }

    private CowsRegistryItem getCowDescription() {
        return CowsRegistry.INSTANCE.getByType(getType());
    }

    public int getTier() {
        return getCowDescription().getTier();
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }

        if (getCowDescription() != null) {
            return LibMisc.LANG.localize(getCowDescription().getDisplayName());
        }

        return super.getCommandSenderName();
    }

    @Override
    public EntityCow createChild(EntityAgeable ageable) {
        EntityCowsCow mateChicken = (EntityCowsCow) ageable;

        CowsRegistryItem parentA = getCowDescription();
        CowsRegistryItem parentB = mateChicken.getCowDescription();

        CowsRegistryItem childType = CowsRegistry.INSTANCE.getRandomChild(parentA, parentB);

        if (childType == null) {
            return null;
        }

        EntityCowsCow child = new EntityCowsCow(this.worldObj);
        child.setType(childType.getId());

        increaseStats(child, this, mateChicken, rand);

        if (CowConfig.useTrait) {
            mutationTrait(child, this, mateChicken, rand);
        }

        if (this.getStatsAnalyzed() || mateChicken.getStatsAnalyzed()) {
            child.setStatsAnalyzed(true);
        }

        return child;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            return false;
        }

        if (!isChild() && milkTank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME
            && stack.getItem() instanceof IFluidContainerItem) {
            FluidStack milkToDrain = milkTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);
            if (milkToDrain.amount == 1000) {
                ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(milkToDrain, stack);
                worldObj.playSoundAtEntity(this, "mob.cow.milking", 1.0F, 1.0F);
                player.inventory.setInventorySlotContents(player.inventory.currentItem, filledContainer);
                syncMilkFluid();
                return true;
            }
        }

        if (!(player instanceof FakePlayer)) {

            if (this.isChild() && isBreedingItem(stack)) {
                --stack.stackSize;
                this.addGrowth((int) ((-this.getGrowingAge() / 20.0F) * 0.1F));
                return true;
            }

            if (isBreedingItem(stack) && this.getGrowingAge() == 0 && !this.isInLove()) {
                --stack.stackSize;
                this.func_146082_f(player);
                return true;
            }
        }

        return super.interact(player);
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild()) {
            int newTimeUntilNextMilk = timeUntilNextMilk - 1;
            setTimeUntilNextMilk(newTimeUntilNextMilk);
            if (newTimeUntilNextMilk <= 1) {
                CowsRegistryItem cow = getCowDescription();
                FluidStack fluid = cow.createMilkFluid();
                if (fluid != null) {
                    int gain = getGain();
                    if (gain >= 5) {
                        int bonusMultiplier = Math.max(0, gain / 5);
                        if (bonusMultiplier > 0) {
                            int baseAmount = fluid.amount;
                            fluid.amount += baseAmount * bonusMultiplier;
                        }
                    }

                    milkTank.fill(fluid, true);
                    syncMilkFluid();
                }
                resetTimeUntilNextMilk();
            }
        }
        super.onLivingUpdate();
    }

    private void setTimeUntilNextMilk(int value) {
        timeUntilNextMilk = value;
        updateMilkProgress();
    }

    private void resetTimeUntilNextMilk() {
        CowsRegistryItem cowDescription = getCowDescription();
        int newBaseTimeUntilNextEgg = (cowDescription.getMaxTime()
            + rand.nextInt(cowDescription.getMaxTime() - cowDescription.getMinTime()));
        float growthModifier = getGrowthTimeModifier();
        int newTimeUntilNextMilk = (int) Math.max(1.0f, newBaseTimeUntilNextEgg * growthModifier);
        setTimeUntilNextMilk(newTimeUntilNextMilk * 2);
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean anyInNether = ChickensRegistry.INSTANCE.isAnyIn(SpawnType.HELL);
        boolean anyInOverworld = ChickensRegistry.INSTANCE.isAnyIn(SpawnType.NORMAL)
            || ChickensRegistry.INSTANCE.isAnyIn(SpawnType.SNOW);

        BiomeGenBase biome = worldObj
            .getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));

        boolean isNetherBiome = biome == BiomeGenBase.hell;

        return (anyInNether && isNetherBiome) || (anyInOverworld && super.getCanSpawnHere());
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);

        if (data instanceof GroupData) {
            setType(((GroupData) data).type);
        } else {
            SpawnType spawnType = getSpawnType();
            List<CowsRegistryItem> list = CowsRegistry.INSTANCE.getPossibleToSpawn(spawnType);

            if (!list.isEmpty()) {
                int type = list.get(rand.nextInt(list.size()))
                    .getId();
                setType(type);
                data = new GroupData(type);
            }
        }

        if (rand.nextInt(5) == 0) {
            setGrowingAge(-24000);
        }
        return data;
    }

    private SpawnType getSpawnType() {
        BiomeGenBase biome = worldObj
            .getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));
        return CowsRegistry.getSpawnType(biome);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, 0); // TYPE
        this.dataWatcher.addObject(21, 1); // GROWTH
        this.dataWatcher.addObject(22, 1); // GAIN
        this.dataWatcher.addObject(23, 1); // STRENGTH
        this.dataWatcher.addObject(24, (byte) 0); // ANALYZED (boolean)
        this.dataWatcher.addObject(25, 0); // PROGRESS
        this.dataWatcher.addObject(26, ""); // FluidStack (NBT string)
    }

    private float getGrowthTimeModifier() {
        int maxGrowth = Math.max(1, getMaxGrowthStat());
        int clampedGrowth = Math.max(1, Math.min(getGrowth(), maxGrowth));
        return (float) (maxGrowth - clampedGrowth + 1) / (float) maxGrowth;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        writeStatsNBT(tagCompound);
        milkTank.writeCommon(MILK_TANK_NBT, tagCompound);
        tagCompound.setInteger(PROGRESS_NBT, getMilkProgress());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        readStatsNBT(tagCompound);
        milkTank.readCommon(MILK_TANK_NBT, tagCompound);
        timeUntilNextMilk = tagCompound.getInteger(PROGRESS_NBT);
    }

    @Override
    public void addTrait(MobTrait trait) {
        IMobStats.super.addTrait(trait);
    }

    public void syncMilkFluid() {
        if (milkTank.getFluid() != null) {
            NBTTagCompound tag = milkTank.getFluid()
                .writeToNBT(new NBTTagCompound());
            this.dataWatcher.updateObject(26, tag.toString());
        } else {
            this.dataWatcher.updateObject(26, "");
        }
    }

    public FluidStack getMilkFluid() {
        String fluidNBT = this.dataWatcher.getWatchableObjectString(26);
        if (!fluidNBT.isEmpty()) {
            try {
                NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(fluidNBT);
                return FluidStack.loadFluidStackFromNBT(tag);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public int getTalkInterval() {
        return 20 * 60;
    }

    @Override
    protected void func_145780_a(int x, int y, int z, Block blockIn) {
        if (this.rand.nextFloat() > 0.1) {
            return;
        }
        super.func_145780_a(x, y, z, blockIn);
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        ItemStack itemsToDrop = getCowDescription().createDropItem();
        int count = 1 + rand.nextInt(1 + lootingModifier);
        itemsToDrop.stackSize *= count;
        entityDropItem(itemsToDrop, 0);

        if (this.isBurning()) {
            this.dropItem(Items.cooked_beef, 1);
        } else {
            this.dropItem(Items.beef, 1);
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, Entity entity) {
        if (!(entity instanceof EntityCowsCow cow)) {
            return;
        }
        tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.tier", getTier()));

        if (getStatsAnalyzed() || CowConfig.alwaysShowStats) {
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.growth", getGrowth()));
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.gain", getGain()));
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.strength", getStrength()));
        }

        if (!isChild()) {
            int milkProgress = getMilkProgress();
            if (milkProgress > 0) {
                int totalSeconds = milkProgress / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                String timeFormatted = String.format("%d:%02d", minutes, seconds);
                tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.milkProgress", timeFormatted));
            }

            FluidStack stored = cow.getMilkFluid();
            if (!(stored == null || stored.getFluid() == null)) {
                String fluidName = stored.getFluid()
                    .getLocalizedName(stored);
                int amount = stored.amount;
                tooltip.add(
                    String.format(
                        "%s%s : %s (%d %s)",
                        EnumChatFormatting.GRAY,
                        LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.fluid"),
                        fluidName,
                        amount,
                        LibMisc.LANG.localize("fluid.millibucket.abr")));
            }
        }
    }

    @Override
    @Optional.Method(modid = "mobsinfo")
    public void provideDropsInformation(@NotNull ArrayList<MobDrop> drops) {
        Item j = this.getDropItem();
        if (j != null) {
            drops.add(
                MobDrop.create(j)
                    .withLooting());
        }
    }

    private static class GroupData implements IEntityLivingData {

        private final int type;

        public GroupData(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    @Override
    public int getMaxGrowthStat() {
        return CowConfig.getMaxGrowthStat();
    }

    @Override
    public int getMaxGainStat() {
        return CowConfig.getMaxGainStat();
    }

    @Override
    public int getMaxStrengthStat() {
        return CowConfig.getMaxStrengthStat();
    }

}
