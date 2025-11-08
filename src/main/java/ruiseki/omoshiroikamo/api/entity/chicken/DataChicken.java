package ruiseki.omoshiroikamo.api.entity.chicken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import com.enderio.core.common.util.BlockCoord;

import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class DataChicken {

    private static final String GAIN_KEY = "Gain";
    private static final String GROWTH_KEY = "Growth";
    private static final String STRENGTH_KEY = "Strength";
    private static final String TYPE_KEY = "Type";

    private int gain = 1;
    private int growth = 1;
    private int strength = 1;

    private ChickensRegistryItem chicken;

    protected Random rand = new Random();

    private DataChicken(ChickensRegistryItem chickenIn, NBTTagCompound compound) {
        chicken = chickenIn;

        if (compound != null) {
            gain = Math.max(1, Math.min(10, compound.getInteger(GAIN_KEY)));
            growth = Math.max(1, Math.min(10, compound.getInteger(GROWTH_KEY)));
            strength = Math.max(1, Math.min(10, compound.getInteger(STRENGTH_KEY)));
        }
    }

    public int getChickenType() {
        return chicken.getId();
    }

    public void addStatsInfoToTooltip(List<String> tooltip) {
        tooltip.add(new ChatComponentTranslation(LibResources.TOOLTIP + "chicken.growth", growth).getFormattedText());
        tooltip.add(new ChatComponentTranslation(LibResources.TOOLTIP + "chicken.gain", gain).getFormattedText());
        tooltip
            .add(new ChatComponentTranslation(LibResources.TOOLTIP + "chicken.strength", strength).getFormattedText());
    }

    public EntityChickensChicken buildEntity(World world) {
        EntityChickensChicken chicken = new EntityChickensChicken(world);
        chicken.readEntityFromNBT(createTagCompound());
        chicken.setChickenType(getChickenType());
        return chicken;
    }

    public void spawnEntity(World world, BlockCoord coord) {
        EntityChickensChicken chicken = new EntityChickensChicken(world);
        chicken.readEntityFromNBT(createTagCompound());
        chicken.setPosition(coord.x + 0.5, coord.y, coord.z + 0.5);
        chicken.onSpawnWithEgg(null);
        chicken.setChickenType(getChickenType());
        chicken.setStatsAnalyzed(true);
        world.spawnEntityInWorld(chicken);
    }

    public ItemStack buildChickenStack() {
        ItemStack stack = ModItems.CHICKEN_SPAWN_EGG.newItemStack(1, this.getChickenType());
        NBTTagCompound tagCompound = createTagCompound();
        tagCompound.setInteger(TYPE_KEY, getChickenType());
        stack.setTagCompound(tagCompound);
        return stack;
    }

    public boolean hasParents() {
        return chicken.getParent1() != null && chicken.getParent2() != null;
    }

    public List<ItemStack> buildParentChickenStack() {
        if (!hasParents()) {
            return null;
        }
        DataChicken parent1 = new DataChicken(chicken.getParent1(), null);
        DataChicken parent2 = new DataChicken(chicken.getParent2(), null);
        return Arrays.asList(parent1.buildChickenStack(), parent2.buildChickenStack());
    }

    public ItemStack buildCaughtFromStack() {
        return new ItemStack(ModItems.CHICKEN_SPAWN_EGG.get(), 1, getChickenType());
    }

    public ItemStack createDropStack() {
        ItemStack stack = chicken.createLayItem();
        stack.stackSize = gain >= 10 ? 3 : gain >= 5 ? 2 : 1;
        return stack;
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
        if (childData == null) {
            return null;
        }

        return childData.buildChickenStack();
    }

    public ItemStack createChildStack(DataChicken other, World world) {
        if (rand.nextBoolean()) {
            return createChildStack(this, other, world);
        }
        return createChildStack(other, this, world);
    }

    public NBTTagCompound createTagCompound() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger(GAIN_KEY, gain);
        tagCompound.setInteger(GROWTH_KEY, growth);
        tagCompound.setInteger(STRENGTH_KEY, strength);
        return tagCompound;
    }

    public int getAddedTime(ItemStack stack) {
        return growth * (stack == null ? 0 : Math.max(0, stack.stackSize));
    }

    public int getLayTime() {
        int minLayTime = chicken.getMinLayTime();
        int maxLayTime = chicken.getMaxLayTime();
        return minLayTime + rand.nextInt(maxLayTime - minLayTime);
    }

    public String getName() {
        return chicken.getEntityName();
    }

    public String getDisplayName() {
        return chicken.getDisplayName();
    }

    public boolean isEqual(DataChicken other) {
        if (other instanceof DataChicken) {
            return (getChickenType() == ((DataChicken) other).getChickenType())
                && (growth == ((DataChicken) other).growth)
                && (gain == ((DataChicken) other).gain)
                && (strength == ((DataChicken) other).strength);
        }
        return false;
    }

    public String toString() {
        return "DataChickenModded [name=" + getName()
            + " type="
            + getChickenType()
            + ", gain="
            + gain
            + ", growth="
            + growth
            + ", strength="
            + strength
            + "]";
    }

    public String getDisplaySummary() {
        return LibMisc.LANG.localize(getDisplayName()) + ": " + growth + "/" + gain + "/" + strength;
    }

    public static List<DataChicken> getAllChickens() {
        List<DataChicken> chickens = new LinkedList<>();
        addAllChickens(chickens);

        return chickens;
    }

    public static void getItemChickenSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (DataChicken chicken : getAllChickens()) {
            subItems.add(chicken.buildChickenStack());
        }
    }

    public static void addAllChickens(List<DataChicken> chickens) {
        for (ChickensRegistryItem item : getChickenRegistryItems()) {
            chickens.add(new DataChicken(item, null));
        }
    }

    public static DataChicken getDataFromEntity(Entity entity) {
        if (entity instanceof EntityChickensChicken chickenEntity) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            chickenEntity.writeEntityToNBT(tagCompound);
            ChickensRegistryItem chicken = ChickensRegistry.getByType(chickenEntity.getChickenType());
            if (chicken != null) {
                return new DataChicken(chicken, tagCompound);
            }
        }
        return null;
    }

    public static DataChicken getDataFromStack(ItemStack stack) {
        if (!isChicken(stack)) {
            return null;
        }
        ChickensRegistryItem chicken = chickensRegistryItemForStack(stack);
        if (chicken != null) {
            return new DataChicken(chicken, stack.getTagCompound());
        }

        return null;
    }

    private static ChickensRegistryItem chickensRegistryItemForStack(ItemStack stack) {
        return ChickensRegistry.getByType(stack.getItemDamage());
    }

    public static boolean isChicken(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.CHICKEN_SPAWN_EGG.get();
    }

    private static List<ChickensRegistryItem> getChickenRegistryItems() {
        Comparator<ChickensRegistryItem> comparator = new Comparator<ChickensRegistryItem>() {

            @Override
            public int compare(ChickensRegistryItem left, ChickensRegistryItem right) {
                if (left.getTier() != right.getTier()) {
                    return left.getTier() - right.getTier();
                }
                return left.getId() - right.getId();
            }
        };

        Collection<ChickensRegistryItem> chickens = ChickensRegistry.getItems();
        List<ChickensRegistryItem> list = new ArrayList<>(chickens);
        Collections.sort(list, comparator);
        return list;
    }

    public ChickensRegistryItem getChicken() {
        return chicken;
    }
}
