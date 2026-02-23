package ruiseki.omoshiroikamo.api.entity.chicken;

import static ruiseki.omoshiroikamo.api.entity.IMobStats.GAIN_NBT;
import static ruiseki.omoshiroikamo.api.entity.IMobStats.GROWTH_NBT;
import static ruiseki.omoshiroikamo.api.entity.IMobStats.STRENGTH_NBT;
import static ruiseki.omoshiroikamo.api.entity.IMobStats.TYPE_NBT;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;

/**
 * Represents all data for a chicken entity in the mod, including stats,
 * breeding information, and methods to create ItemStacks and entities.
 *
 * <p>
 * A DataChicken wraps a {@link ChickensRegistryItem} and contains:
 * <ul>
 * <li>Genetic stats: growth, gain, strength</li>
 * <li>Parent information for breeding</li>
 * <li>Methods to build entities, spawn eggs, and calculate lay times</li>
 * </ul>
 *
 * <p>
 * This class is immutable in terms of registry item, but stats can vary.
 */
public class DataChicken {

    private int gain = 1;
    private int growth = 1;
    private int strength = 1;

    private final ChickensRegistryItem chicken;
    protected Random rand = new Random();

    /**
     * Creates a new DataChicken from a registry item and optional NBT data.
     *
     * @param chickenIn The registry item defining the chicken type
     * @param compound  Optional NBT data to load stats from
     */
    public DataChicken(ChickensRegistryItem chickenIn, NBTTagCompound compound) {
        chicken = chickenIn;
        if (compound != null) {
            gain = clampStat(compound.getInteger(GAIN_NBT), ChickenConfig.getMaxGainStat());
            growth = clampStat(compound.getInteger(GROWTH_NBT), ChickenConfig.getMaxGrowthStat());
            strength = clampStat(compound.getInteger(STRENGTH_NBT), ChickenConfig.getMaxStrengthStat());
        }
    }

    /**
     * @return The numeric type ID of this chicken
     */
    public int getId() {
        return chicken.getId();
    }

    /**
     * @return The registry item for this chicken
     */
    public ChickensRegistryItem getItem() {
        return chicken;
    }

    public int getGrowthStat() {
        return growth;
    }

    public int getGainStat() {
        return gain;
    }

    public int getStrengthStat() {
        return strength;
    }

    /**
     * @return The name of this chicken
     */
    public String getName() {
        return chicken.getEntityName();
    }

    /**
     * @return The localized display name of this chicken
     */
    public String getDisplayName() {
        return chicken.getDisplayName();
    }

    /**
     * @return True if this chicken has parents
     */
    public boolean hasParents() {
        return chicken.getParent1() != null && chicken.getParent2() != null;
    }

    /**
     * Add tooltip information describing growth, gain, strength
     */
    public List<String> getStatsInfoTooltip() {
        return TooltipUtils.builder()
            .addLang(LibResources.TOOLTIP + "mob.growth", growth)
            .addLang(LibResources.TOOLTIP + "mob.gain", gain)
            .addLang(LibResources.TOOLTIP + "mob.strength", strength)
            .build();
    }

    /**
     * @return True if another DataChicken has identical type and stats
     */
    public boolean isEqual(DataChicken other) {
        if (other == null) {
            return false;
        }
        return getId() == other.getId() && growth == other.growth && gain == other.gain && strength == other.strength;
    }

    /**
     * @return A compact summary: "DisplayName: growth/gain/strength"
     */
    public String getDisplaySummary() {
        return LibMisc.LANG.localize(getDisplayName()) + ": " + growth + "/" + gain + "/" + strength;
    }

    /**
     * Builds an entity based on this chicken
     */
    public EntityChickensChicken buildEntity(World world) {
        EntityChickensChicken entity = new EntityChickensChicken(world);
        entity.readEntityFromNBT(createTagCompound());
        entity.setType(getId());
        return entity;
    }

    /**
     * Spawns the chicken at a block coordinate
     */
    public void spawnEntity(BlockPos pos) {
        EntityChickensChicken entity = buildEntity(pos.world);
        entity.setPosition(pos.x + 0.5, pos.y, pos.z + 0.5);
        entity.onSpawnWithEgg(null);
        entity.setStatsAnalyzed(true);
        entity.setType(getId());
        pos.world.spawnEntityInWorld(entity);
    }

    /**
     * @return A spawn egg ItemStack representing this chicken
     */
    public ItemStack buildStack() {
        ItemStack stack = ChickensItems.CHICKEN.newItemStack(1, getId());
        NBTTagCompound tag = createTagCompound();
        tag.setInteger(TYPE_NBT, getId());
        stack.setTagCompound(tag);
        return stack;
    }

    /**
     * @return Spawn eggs for both parents, or null if no parents
     */
    public List<ItemStack> buildParentChickenStack() {
        if (!hasParents()) {
            return null;
        }
        DataChicken parent1 = new DataChicken(chicken.getParent1(), null);
        DataChicken parent2 = new DataChicken(chicken.getParent2(), null);
        return Arrays.asList(parent1.buildStack(), parent2.buildStack());
    }

    /**
     * @return A spawn egg as if caught from the world
     */
    public ItemStack buildCaughtFromStack() {
        return new ItemStack(ChickensItems.CHICKEN.getItem(), 1, getId());
    }

    /**
     * @return ItemStack of the lay item, amount based on Gain
     */
    public ItemStack createLayStack() {
        ItemStack stack = chicken.createLayItem();
        if (stack == null) {
            return null;
        }
        int factor = calculateLayStackFactor(gain);
        stack.stackSize *= factor;
        return stack;
    }

    /**
     * Creates a child spawn egg from this and another DataChicken
     *
     * @param other The other parent
     * @param world The world context
     * @return Spawn egg of the child, or null
     */
    public ItemStack createChildStack(DataChicken other, World world) {
        return rand.nextBoolean() ? createChildStack(this, other, world) : createChildStack(other, this, world);
    }

    private static ItemStack createChildStack(DataChicken chickenA, DataChicken chickenB, World world) {
        EntityChickensChicken parentA = chickenA.buildEntity(world);
        EntityChickensChicken parentB = chickenB.buildEntity(world);

        if (parentA == null || parentB == null) {
            return null;
        }
        EntityChicken childEntity = parentA.createChild(parentB);
        if (!(childEntity instanceof EntityChickensChicken child)) {
            return null;
        }

        DataChicken childData = DataChicken.getDataFromEntity(child);
        return childData != null ? childData.buildStack() : null;
    }

    /**
     * @return NBTTagCompound representing this chicken's stats
     */
    public NBTTagCompound createTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(GAIN_NBT, clampStat(gain, ChickenConfig.getMaxGainStat()));
        tag.setInteger(GROWTH_NBT, clampStat(growth, ChickenConfig.getMaxGrowthStat()));
        tag.setInteger(STRENGTH_NBT, clampStat(strength, ChickenConfig.getMaxStrengthStat()));
        return tag;
    }

    /**
     * @return Additional time added based on stack size and growth
     */
    public int getAddedTime(ItemStack stack) {
        return growth * (stack == null ? 0 : Math.max(0, stack.stackSize));
    }

    /**
     * @return Lay time in ticks, randomly between min and max of species
     */
    public int getTime() {
        int minLayTime = chicken.getMinTime();
        int maxLayTime = chicken.getMaxTime();
        return minLayTime + rand.nextInt(maxLayTime - minLayTime);
    }

    /**
     * @return All DataChicken instances in the registry
     */
    public static List<DataChicken> getAllChickens() {
        List<DataChicken> chickens = new LinkedList<>();
        addAllChickens(chickens);
        return chickens;
    }

    /**
     * Adds all registry chickens to a list
     */
    public static void addAllChickens(List<DataChicken> chickens) {
        for (ChickensRegistryItem item : ChickensRegistry.INSTANCE.getItems()) {
            chickens.add(new DataChicken(item, null));
        }
    }

    /**
     * @return DataChicken from an entity instance, or null
     */
    public static DataChicken getDataFromEntity(Entity entity) {
        if (entity instanceof EntityChickensChicken chickenEntity) {
            NBTTagCompound tag = new NBTTagCompound();
            chickenEntity.writeEntityToNBT(tag);
            ChickensRegistryItem chicken = ChickensRegistry.INSTANCE.getByType(chickenEntity.getType());
            return chicken != null ? new DataChicken(chicken, tag) : null;
        }
        return null;
    }

    private static int clampStat(int value, int maxValue) {
        return Math.max(1, Math.min(maxValue, value));
    }

    /**
     * @return DataChicken from a spawn egg stack, or null
     */
    public static DataChicken getDataFromStack(ItemStack stack) {
        if (!isChicken(stack)) {
            return null;
        }
        ChickensRegistryItem chicken = ChickensRegistry.INSTANCE.getByType(stack.getItemDamage());
        return chicken != null ? new DataChicken(chicken, stack.getTagCompound()) : null;
    }

    public static int calculateLayStackFactor(int gainStat) {
        if (gainStat < 5) {
            return 1;
        }
        double ratio = gainStat / 5.0;
        int multiplier = (int) Math.floor(Math.log(ratio) / Math.log(2.0));
        multiplier = Math.max(0, multiplier) + 1;
        return Math.max(1, 1 + multiplier);
    }

    /**
     * @return True if ItemStack is a chicken spawn egg
     */
    public static boolean isChicken(ItemStack stack) {
        return stack != null && stack.getItem() == ChickensItems.CHICKEN.getItem();
    }

    @Override
    public String toString() {
        return "DataChicken [name=" + getName()
            + " type="
            + getId()
            + ", gain="
            + gain
            + ", growth="
            + growth
            + ", strength="
            + strength
            + "]";
    }
}
