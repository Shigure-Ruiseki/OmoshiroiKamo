package ruiseki.omoshiroikamo.common.entity.chicken;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.FakePlayer;

import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.MobTrait;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.plugin.waila.IWailaEntityInfoProvider;

public class EntityChickensChicken extends EntityChicken implements IMobStats, IWailaEntityInfoProvider {

    private final List<MobTrait> traits = new ArrayList<>();

    public EntityChickensChicken(World world) {
        super(world);
    }

    @Override
    public boolean getStatsAnalyzed() {
        return this.dataWatcher.getWatchableObjectByte(25) != 0;
    }

    @Override
    public void setStatsAnalyzed(boolean val) {
        this.dataWatcher.updateObject(25, (byte) (val ? 1 : 0));
    }

    @Override
    public int getBaseGrowth() {
        return this.dataWatcher.getWatchableObjectInt(21);
    }

    @Override
    public void setBaseGrowth(int growth) {
        this.dataWatcher.updateObject(21, growth);
    }

    @Override
    public int getBaseGain() {
        return this.dataWatcher.getWatchableObjectInt(22);
    }

    @Override
    public void setBaseGain(int gain) {
        this.dataWatcher.updateObject(22, gain);
    }

    @Override
    public int getBaseStrength() {
        return this.dataWatcher.getWatchableObjectInt(23);
    }

    @Override
    public void setBaseStrength(int strength) {
        this.dataWatcher.updateObject(23, strength);
    }

    @Override
    public List<MobTrait> getTraits() {
        return traits;
    }

    @Override
    public void setType(int type) {
        dataWatcher.updateObject(20, type);
        isImmuneToFire = getChickenDescription().isImmuneToFire();
        resetTimeUntilNextEgg();
    }

    @Override
    public int getType() {
        return dataWatcher.getWatchableObjectInt(20);
    }

    @Override
    public void setGrowingAge(int age) {
        age = setStatsGrowingAge(age);
        super.setGrowingAge(age);
    }

    public int getLayProgress() {
        return dataWatcher.getWatchableObjectInt(24);
    }

    private void updateLayProgress() {
        this.dataWatcher.updateObject(24, timeUntilNextEgg);
    }

    public ResourceLocation getTexture() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        return chickenDescription.getTexture();
    }

    private ChickensRegistryItem getChickenDescription() {
        return ChickensRegistry.INSTANCE.getByType(getType());
    }

    public int getTier() {
        return getChickenDescription().getTier();
    }

    @Override
    public String getCommandSenderName() {
        if (hasCustomNameTag()) {
            return getCustomNameTag();
        }
        return LibMisc.LANG.localize(getChickenDescription().getDisplayName());
    }

    @Override
    public EntityChicken createChild(EntityAgeable ageable) {
        EntityChickensChicken mateChicken = (EntityChickensChicken) ageable;

        ChickensRegistryItem parentA = getChickenDescription();
        ChickensRegistryItem parentB = mateChicken.getChickenDescription();

        ChickensRegistryItem childType = ChickensRegistry.INSTANCE.getRandomChild(parentA, parentB);

        if (childType == null) {
            return null;
        }

        EntityChickensChicken child = new EntityChickensChicken(this.worldObj);
        child.setType(childType.getId());

        increaseStats(child, this, mateChicken, rand);

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
        if (!this.worldObj.isRemote && !this.isChild() && !this.field_152118_bv) {
            int newTimeUntilNextEgg = timeUntilNextEgg - 1;
            setTimeUntilNextEgg(newTimeUntilNextEgg);
            if (newTimeUntilNextEgg <= 1) {
                ChickensRegistryItem chicken = getChickenDescription();
                ItemStack itemToLay = chicken.createLayItem();

                if (itemToLay != null) {
                    int gain = getGain();
                    if (gain >= 5) {
                        itemToLay.stackSize += chicken.createLayItem().stackSize;
                    }
                    if (gain >= 10) {
                        itemToLay.stackSize += chicken.createLayItem().stackSize;
                    }

                    entityDropItem(itemToLay, 0.0F);
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

    private void resetTimeUntilNextEgg() {
        ChickensRegistryItem chickenDescription = getChickenDescription();
        int newBaseTimeUntilNextEgg = (chickenDescription.getMinTime()
            + rand.nextInt(chickenDescription.getMaxTime() - chickenDescription.getMinTime()));
        int newTimeUntilNextEgg = (int) Math.max(1.0f, (newBaseTimeUntilNextEgg * (10.f - getGrowth() + 1.f)) / 10.f);
        setTimeUntilNextEgg(newTimeUntilNextEgg * 2);
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
            List<ChickensRegistryItem> list = ChickensRegistry.INSTANCE.getPossibleToSpawn(spawnType);

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

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(20, 0); // TYPE
        this.dataWatcher.addObject(21, 1); // GROWTH
        this.dataWatcher.addObject(22, 1); // GAIN
        this.dataWatcher.addObject(23, 1); // STRENGTH
        this.dataWatcher.addObject(24, 0); // LAY_PROGRESS
        this.dataWatcher.addObject(25, (byte) 0); // ANALYZED (boolean)
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        writeStatsNBT(tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        readStatsNBT(tagCompound);
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
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, Entity entity) {
        if (!(entity instanceof EntityChickensChicken chicken)) {
            return;
        }
        tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.tier", chicken.getTier()));

        if (chicken.getStatsAnalyzed() || ChickenConfig.alwaysShowStats) {
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.growth", chicken.getGrowth()));
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.gain", chicken.getGain()));
            tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.strength", chicken.getStrength()));
        }

        if (!chicken.isChild()) {
            int layProgress = chicken.getLayProgress();
            if (layProgress > 0) {
                int totalSeconds = layProgress / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                String timeFormatted = String.format("%d:%02d", minutes, seconds);

                tooltip.add(LibMisc.LANG.localize(LibResources.TOOLTIP + "entity.layProgress", timeFormatted));
            }
        }
    }
}
