package ruiseki.omoshiroikamo.common.entity.chicken;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class EntityChickensChicken extends EntityChicken {

    private static final String TYPE_NBT = "Type";
    private static final String CHICKEN_STATS_ANALYZED_NBT = "Analyzed";
    private static final String CHICKEN_GROWTH_NBT = "Growth";
    private static final String CHICKEN_GAIN_NBT = "Gain";
    private static final String CHICKEN_STRENGTH_NBT = "Strength";

    public EntityChickensChicken(World world) {
        super(world);
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
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
        return ChickensRegistry.getByType(getChickenTypeInternal());
    }

    public int getTier() {
        return getChickenDescription().getTier();
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return LibMisc.LANG.localize("entity." + getChickenDescription().getEntityName() + ".name");
    }

    @Override
    public void setCustomNameTag(String p_94058_1_) {
        super.setCustomNameTag(p_94058_1_);
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        EntityChickensChicken mateChicken = (EntityChickensChicken) ageable;

        ChickensRegistryItem chickenDescription = getChickenDescription();
        ChickensRegistryItem mateChickenDescription = mateChicken.getChickenDescription();

        ChickensRegistryItem childToBeBorn = ChickensRegistry
            .getRandomChild(chickenDescription, mateChickenDescription);
        if (childToBeBorn == null) {
            return null;
        }

        EntityChickensChicken newChicken = new EntityChickensChicken(this.worldObj);
        newChicken.setChickenType(childToBeBorn.getId());

        boolean mutatingStats = chickenDescription.getId() == mateChickenDescription.getId()
            && childToBeBorn.getId() == chickenDescription.getId();
        if (mutatingStats) {
            increaseStats(newChicken, this, mateChicken, rand);
        } else if (chickenDescription.getId() == childToBeBorn.getId()) {
            inheritStats(newChicken, this);
        } else if (mateChickenDescription.getId() == childToBeBorn.getId()) {
            inheritStats(newChicken, mateChicken);
        }

        return newChicken;
    }

    private static void inheritStats(EntityChickensChicken newChicken, EntityChickensChicken parent) {
        newChicken.setGrowth(parent.getGrowth());
        newChicken.setGain(parent.getGain());
        newChicken.setStrength(parent.getStrength());
    }

    private static void increaseStats(EntityChickensChicken newChicken, EntityChickensChicken parent1,
        EntityChickensChicken parent2, Random rand) {
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
        if (newStatValue >= 10) {
            return 10;
        }
        return newStatValue;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.isChild() && !this.field_152118_bv) {
            int newTimeUntilNextEgg = timeUntilNextEgg - 1;
            setTimeUntilNextEgg(newTimeUntilNextEgg);
            if (newTimeUntilNextEgg <= 1) {
                ChickensRegistryItem chickenDescription = getChickenDescription();
                ItemStack itemToLay = chickenDescription.createLayItem();

                // itemToLay = TileEntityHenhouse.pushItemStack(itemToLay, worldObj, new Vector3d(posX, posY, posZ));

                if (itemToLay != null) {
                    entityDropItem(chickenDescription.createLayItem(), 0);
                    int gain = getGain();
                    if (gain >= 5) {
                        entityDropItem(chickenDescription.createLayItem(), 0);
                    }
                    if (gain >= 10) {
                        entityDropItem(chickenDescription.createLayItem(), 0);
                    }
                    playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                }

                resetTimeUntilNextEgg();
            }
        }
        super.onLivingUpdate();
    }

    private void setTimeUntilNextEgg(int value) {
        timeUntilNextEgg = value;
        updateLayProgress();
    }

    public int getLayProgress() {
        return dataWatcher.getWatchableObjectInt(24);
    }

    private void updateLayProgress() {
        this.dataWatcher.updateObject(24, timeUntilNextEgg / 60 / 20 / 2);
    }

    private void resetTimeUntilNextEgg() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        int newBaseTimeUntilNextEgg = (chickenDescription.getMinLayTime()
            + rand.nextInt(chickenDescription.getMaxLayTime() - chickenDescription.getMinLayTime()));
        int newTimeUntilNextEgg = (int) Math.max(1.0f, (newBaseTimeUntilNextEgg * (10.f - getGrowth() + 1.f)) / 10.f);
        setTimeUntilNextEgg(newTimeUntilNextEgg * 2);
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean anyInNether = ChickensRegistry.isAnyIn(SpawnType.HELL);
        boolean anyInOverworld = ChickensRegistry.isAnyIn(SpawnType.NORMAL) || ChickensRegistry.isAnyIn(SpawnType.SNOW);

        BiomeGenBase biome = worldObj
            .getBiomeGenForCoords(MathHelper.floor_double(posX), MathHelper.floor_double(posZ));

        boolean isNetherBiome = biome == BiomeGenBase.hell;

        return (anyInNether && isNetherBiome) || (anyInOverworld && super.getCanSpawnHere());
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);

        if (data instanceof GroupData) {
            setChickenType(((GroupData) data).type);
        } else {
            SpawnType spawnType = getSpawnType();
            List<ChickensRegistryItem> list = ChickensRegistry.getPossibleChickensToSpawn(spawnType);

            if (!list.isEmpty()) {
                int type = list.get(rand.nextInt(list.size()))
                    .getId();
                setChickenType(type);
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
        return ChickensRegistry.getSpawnType(biome);
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

    public void setChickenType(int type) {
        setChickenTypeInternal(type);
        isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    private int getChickenTypeInternal() {
        return dataWatcher.getWatchableObjectInt(20);
    }

    private void setChickenTypeInternal(int t) {
        dataWatcher.updateObject(20, t);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, 0); // CHICKEN_TYPE
        this.dataWatcher.addObject(21, 1); // GROWTH
        this.dataWatcher.addObject(22, 1); // GAIN
        this.dataWatcher.addObject(23, 1); // STRENGTH
        this.dataWatcher.addObject(24, 0); // LAY_PROGRESS
        this.dataWatcher.addObject(25, (byte) 0); // ANALYZED (boolean)
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger(TYPE_NBT, getChickenTypeInternal());
        tagCompound.setBoolean(CHICKEN_STATS_ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.setInteger(CHICKEN_GROWTH_NBT, getGrowth());
        tagCompound.setInteger(CHICKEN_GAIN_NBT, getGain());
        tagCompound.setInteger(CHICKEN_STRENGTH_NBT, getStrength());

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setChickenType(tagCompound.getInteger(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(CHICKEN_STATS_ANALYZED_NBT));
        setGrowth(getStatusValue(tagCompound, CHICKEN_GROWTH_NBT));
        setGain(getStatusValue(tagCompound, CHICKEN_GAIN_NBT));
        setStrength(getStatusValue(tagCompound, CHICKEN_STRENGTH_NBT));
    }

    private int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
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
        ItemStack itemsToDrop = getChickenDescription().createDropItem();
        int count = 1 + rand.nextInt(1 + lootingModifier);
        itemsToDrop.stackSize *= count;
        entityDropItem(itemsToDrop, 0);

        if (this.isBurning()) {
            this.dropItem(Items.cooked_chicken, 1);
        } else {
            this.dropItem(Items.chicken, 1);
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
