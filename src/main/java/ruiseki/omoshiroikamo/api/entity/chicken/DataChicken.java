package ruiseki.omoshiroikamo.api.entity.chicken;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

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

    private static final String GAIN_KEY = "Gain";
    private static final String GROWTH_KEY = "Growth";
    private static final String STRENGTH_KEY = "Strength";
    private static final String TYPE_KEY = "Type";

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
    private DataChicken(ChickensRegistryItem chickenIn, NBTTagCompound compound) {
        chicken = chickenIn;
        if (compound != null) {
            gain = Math.max(1, Math.min(10, compound.getInteger(GAIN_KEY)));
            growth = Math.max(1, Math.min(10, compound.getInteger(GROWTH_KEY)));
            strength = Math.max(1, Math.min(10, compound.getInteger(STRENGTH_KEY)));
        }
    }

    /**
     * @return The numeric type ID of this chicken
     */
    public int getType() {
        return chicken.getId();
    }

    /**
     * @return The registry item for this chicken
     */
    public ChickensRegistryItem getItems() {
        return chicken;
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
    public void addStatsInfoToTooltip(List<String> tooltip) {
        tooltip.add(new ChatComponentTranslation(LibResources.TOOLTIP + "mob.growth", growth).getFormattedText());
        tooltip.add(new ChatComponentTranslation(LibResources.TOOLTIP + "mob.gain", gain).getFormattedText());
        tooltip.add(new ChatComponentTranslation(LibResources.TOOLTIP + "mob.strength", strength).getFormattedText());
    }

    /**
     * @return True if another DataChicken has identical type and stats
     */
    public boolean isEqual(DataChicken other) {
        if (other == null) {
            return false;
        }
        return getType() == other.getType() && growth == other.growth
            && gain == other.gain
            && strength == other.strength;
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
        entity.setType(getType());
        return entity;
    }

    /**
     * Spawns the chicken at a block coordinate
     */
    public void spawnEntity(World world, BlockCoord coord) {
        EntityChickensChicken entity = buildEntity(world);
        entity.setPosition(coord.x + 0.5, coord.y, coord.z + 0.5);
        entity.onSpawnWithEgg(null);
        entity.addRandomTraits();
        entity.setStatsAnalyzed(true);
        entity.setType(getType());
        world.spawnEntityInWorld(entity);
    }

    /**
     * @return A spawn egg ItemStack representing this chicken
     */
    public ItemStack buildStack() {
        ItemStack stack = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, getType());
        NBTTagCompound tag = createTagCompound();
        tag.setInteger(TYPE_KEY, getType());
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
        return new ItemStack(ModItems.CHICKEN_SPAWN_EGG.get(), 1, getType());
    }

    /**
     * @return ItemStack of the lay item, amount based on Gain
     */
    public ItemStack createLayStack() {
        ItemStack stack = chicken.createLayItem();
        stack.stackSize = gain >= 10 ? 3 : gain >= 5 ? 2 : 1;
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
        tag.setInteger(GAIN_KEY, gain);
        tag.setInteger(GROWTH_KEY, growth);
        tag.setInteger(STRENGTH_KEY, strength);
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

    /**
     * @return True if ItemStack is a chicken spawn egg
     */
    public static boolean isChicken(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.CHICKEN_SPAWN_EGG.get();
    }

    @Override
    public String toString() {
        return "DataChicken [name=" + getName()
            + " type="
            + getType()
            + ", gain="
            + gain
            + ", growth="
            + growth
            + ", strength="
            + strength
            + "]";
    }
}
