package ruiseki.omoshiroikamo.common.entity.cow;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.common.util.FluidUtil;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class EntityCowsCow extends EntityCow {

    private static final String TYPE_NBT = "Type";
    private static final String STATS_ANALYZED_NBT = "Analyzed";
    private static final String GROWTH_NBT = "Growth";
    private static final String GAIN_NBT = "Gain";
    private static final String STRENGTH_NBT = "Strength";
    public int timeUntilNextMilk;
    public SmartTank milkTank;

    public EntityCowsCow(World worldIn) {
        super(worldIn);
        timeUntilNextMilk = this.rand.nextInt(6000) + 6000;
        milkTank = new SmartTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
    }

    public boolean getStatsAnalyzed() {
        return this.dataWatcher.getWatchableObjectByte(25) != 0;
    }

    public void setStatsAnalyzed(boolean val) {
        this.dataWatcher.updateObject(25, (byte) (val ? 1 : 0));
    }

    public int getGrowth() {
        return this.dataWatcher.getWatchableObjectInt(21);
    }

    private void setGrowth(int growth) {
        this.dataWatcher.updateObject(21, growth);
    }

    public int getGain() {
        return this.dataWatcher.getWatchableObjectInt(22);
    }

    private void setGain(int gain) {
        this.dataWatcher.updateObject(22, gain);
    }

    public int getStrength() {
        return this.dataWatcher.getWatchableObjectInt(23);
    }

    private void setStrength(int strength) {
        this.dataWatcher.updateObject(23, strength);
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

    public void setType(int type) {
        dataWatcher.updateObject(20, type);
        isImmuneToFire = getCowDescription().isImmuneToFire();
    }

    public int getType() {
        return dataWatcher.getWatchableObjectInt(20);
    }

    public SmartTank getMilkTank() {
        return milkTank;
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return LibMisc.LANG.localize(getCowDescription().getDisplayName());
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

        if (this.getStatsAnalyzed() || mateChicken.getStatsAnalyzed()) {
            child.setStatsAnalyzed(true);
        }

        return child;
    }

    private static void increaseStats(EntityCowsCow newChicken, EntityCowsCow parent1, EntityCowsCow parent2,
        Random rand) {
        int parent1Strength = parent1.getStrength();
        int parent2Strength = parent2.getStrength();
        newChicken.setGrowth(
            calculateNewStat(parent1Strength, parent2Strength, parent1.getGrowth(), parent2.getGrowth(), rand));
        newChicken
            .setGain(calculateNewStat(parent1Strength, parent2Strength, parent2.getGain(), parent2.getGain(), rand));
        newChicken
            .setStrength(calculateNewStat(parent1Strength, parent2Strength, parent1Strength, parent2Strength, rand));
    }

    private static int calculateNewStat(int thisStrength, int mateStrength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * thisStrength + stat2 * mateStrength) / (thisStrength + mateStrength) + mutation;
        if (newStatValue <= 1) {
            return 1;
        }
        return Math.min(newStatValue, 10);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack stack = player.getHeldItem();
        if (stack == null) {
            return false;
        }

        if (!isChild() && milkTank.getFluidAmount() >= FluidContainerRegistry.BUCKET_VOLUME) {
            FluidStack milkToDrain = milkTank.drain(FluidContainerRegistry.BUCKET_VOLUME, false);
            if (milkToDrain != null && milkToDrain.amount > 0) {
                FluidUtil.FluidAndStackResult result = FluidUtil.tryFillContainer(stack, milkToDrain);
                if (result.result != null && result.result.fluidStack != null) {
                    worldObj.playSoundAtEntity(this, "mob.cow.milking", 1.0F, 1.0F);

                    milkTank.drain(FluidContainerRegistry.BUCKET_VOLUME, true);

                    ItemStack filledItem = result.result.itemStack;
                    ItemStack remainderItem = result.remainder.itemStack;

                    player.inventory.setInventorySlotContents(
                        player.inventory.currentItem,
                        remainderItem == null ? filledItem : remainderItem);

                    if (remainderItem != null && !player.inventory.addItemStackToInventory(filledItem)) {
                        entityDropItem(filledItem, 0.0F);
                    }
                    syncMilkFluid();
                    return true;
                }
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
            int newTimeUntilNextEgg = timeUntilNextMilk - 1;
            setTimeUntilNextMilk(newTimeUntilNextEgg);
            if (newTimeUntilNextEgg <= 1) {
                CowsRegistryItem cow = getCowDescription();
                FluidStack fluid = cow.createMilkFluid();
                if (fluid != null) {
                    int gain = getGain();
                    if (gain >= 5) {
                        fluid.amount += cow.createMilkFluid().amount;
                    }
                    if (gain >= 10) {
                        fluid.amount += cow.createMilkFluid().amount;
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

    public int getMilkProgress() {
        return dataWatcher.getWatchableObjectInt(24);
    }

    private void updateMilkProgress() {
        this.dataWatcher.updateObject(24, timeUntilNextMilk);
    }

    private void resetTimeUntilNextMilk() {
        CowsRegistryItem cowDescription = getCowDescription();
        int newBaseTimeUntilNextEgg = (cowDescription.getMaxTime()
            + rand.nextInt(cowDescription.getMaxTime() - cowDescription.getMinTime()));
        int newTimeUntilNextMilk = (int) Math.max(1.0f, (newBaseTimeUntilNextEgg * (10.f - getGrowth() + 1.f)) / 10.f);
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
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, 0); // TYPE
        this.dataWatcher.addObject(21, 1); // GROWTH
        this.dataWatcher.addObject(22, 1); // GAIN
        this.dataWatcher.addObject(23, 1); // STRENGTH
        this.dataWatcher.addObject(24, 0); // PROGRESS
        this.dataWatcher.addObject(25, (byte) 0); // ANALYZED (boolean)
        this.dataWatcher.addObject(26, ""); // FluidStack (NBT string)
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TYPE_NBT, getType());
        tagCompound.setBoolean(STATS_ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.setInteger(GROWTH_NBT, getGrowth());
        tagCompound.setInteger(GAIN_NBT, getGain());
        tagCompound.setInteger(STRENGTH_NBT, getStrength());
        milkTank.writeCommon("milkTank", tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setType(tagCompound.getInteger(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(STATS_ANALYZED_NBT));
        setGrowth(getStatusValue(tagCompound, GROWTH_NBT));
        setGain(getStatusValue(tagCompound, GAIN_NBT));
        setStrength(getStatusValue(tagCompound, STRENGTH_NBT));
        milkTank.readCommon("milkTank", tagCompound);
    }

    private int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
    }

    public void syncMilkFluid() {
        if (milkTank.getFluid() != null) {
            NBTTagCompound tag = milkTank.getFluid()
                .writeToNBT(new NBTTagCompound());
            this.dataWatcher.updateObject(26, tag.toString()); // lưu NBT thành string
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
    public void setGrowingAge(int age) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) {
            age = Math.min(-1, (childAge * (10 - getGrowth() + 1)) / 10);
        }

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) {
            age = Math.max(1, (loveAge * (10 - getGrowth() + 1)) / 10);
        }

        super.setGrowingAge(age);
    }
}
