package ruiseki.omoshiroikamo.plugin.cow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.handler.CowNetherPopulateHandler;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.general.CowsConfigs;

public class ModCows {

    public static void preInit() {

        EntityRegistry.registerModEntity(
            EntityCowsCow.class,
            "cow",
            CowsConfigs.cowEntityId,
            OmoshiroiKamo.instance,
            64,
            1,
            true);

        registerModAddons();
    }

    public static void init() {
        loadConfiguration();

        List<BiomeGenBase> biomesForSpawning = getAllSpawnBiomes();
        if (biomesForSpawning.size() > 0) {
            EntityRegistry.addSpawn(
                EntityCowsCow.class,
                CowsConfigs.spawnProbability,
                CowsConfigs.minBroodSize,
                CowsConfigs.maxBroodSize,
                EnumCreatureType.creature,
                biomesForSpawning.toArray(new BiomeGenBase[biomesForSpawning.size()]));
            if (biomesForSpawning.contains(BiomeGenBase.hell)) {
                MinecraftForge.TERRAIN_GEN_BUS
                    .register(new CowNetherPopulateHandler(CowsConfigs.netherSpawnChanceMultiplier));
            }
        }
    }

    public static ArrayList<BaseCowHandler> registeredModAddons = new ArrayList<>();

    private static void registerModAddons() {
        addModAddon(new BaseCows());
    }

    private static List<BiomeGenBase> getAllSpawnBiomes() {
        BiomeGenBase[] allPossibleBiomes = { BiomeGenBase.plains, BiomeGenBase.extremeHills, BiomeGenBase.forest,
            BiomeGenBase.taiga, BiomeGenBase.swampland, BiomeGenBase.icePlains, BiomeGenBase.iceMountains,
            BiomeGenBase.forestHills, BiomeGenBase.taigaHills, BiomeGenBase.extremeHillsEdge, BiomeGenBase.jungle,
            BiomeGenBase.jungleHills, BiomeGenBase.jungleEdge, BiomeGenBase.birchForest, BiomeGenBase.birchForestHills,
            BiomeGenBase.roofedForest, BiomeGenBase.coldTaiga, BiomeGenBase.coldTaigaHills, BiomeGenBase.extremeHills,
            BiomeGenBase.savanna, BiomeGenBase.savannaPlateau, BiomeGenBase.hell };

        List<BiomeGenBase> biomesForSpawning = new ArrayList<BiomeGenBase>();
        for (BiomeGenBase biome : allPossibleBiomes) {
            if (CowsRegistry.INSTANCE.isAnyIn(CowsRegistry.getSpawnType(biome))) {
                biomesForSpawning.add(biome);
            }
        }
        return biomesForSpawning;
    }

    public static void addModAddon(BaseCowHandler addon) {
        if (addon == null) {
            Logger.error("Tried to add null mod addon");
            return;
        }

        registeredModAddons.add(addon);
    }

    private static List<CowsRegistryItem> generateDefaultChickens() {
        List<CowsRegistryItem> cows = new ArrayList<>();

        for (BaseCowHandler addon : registeredModAddons) {
            cows = addon.tryRegisterChickens(cows);
        }

        // SetParents

        for (BaseCowHandler addon : registeredModAddons) {
            Logger.debug("Register " + addon.getModName() + " Parents");
            addon.registerAllParents(cows);
        }

        return cows;

    }

    private static String getCowsParent(Configuration configuration, String propertyName,
        Collection<CowsRegistryItem> allChickens, CowsRegistryItem cow, CowsRegistryItem parent) {
        String Category = cow.getEntityName();
        return configuration.getString(
            propertyName,
            Category,
            parent != null ? parent.getEntityName() : "",
            "First parent, empty if it's base cow.");
    }

    private static void loadConfiguration() {

        File configDirectory = new File("config/" + LibMisc.MOD_ID + "/cow");
        if (!configDirectory.exists()) {
            configDirectory.mkdir();
        }

        File configFile = new File(configDirectory, "base.cfg");
        Configuration configuration = new Configuration(configFile);

        Collection<CowsRegistryItem> allChickens = generateDefaultChickens();

        configuration.addCustomCategoryComment(
            "0",
            "It is Ideal to regenerate this file after updates as your config files may overwrite changes made to core.");

        Logger.info("Chickens Loading Config...");
        for (CowsRegistryItem cow : allChickens) {

            boolean enabled = configuration.getBoolean("enabled", cow.getEntityName(), true, "Is cow enabled?");
            cow.setEnabled(enabled);

            float coefficient = configuration
                .getFloat("coefficient", cow.getEntityName(), 1.0f, 0.01f, 100.f, "Scale time to milk a fluid.");
            cow.setCoefficient(coefficient);

            FluidStack itemStack = loadFluidStack(configuration, cow, "milk", cow.createMilkFluid());
            cow.setMilkFluid(itemStack);

            ItemStack dropItemStack = loadItemStack(configuration, cow, "drop", cow.createDropItem());
            cow.setDropItem(dropItemStack);

            String parent1ID = getCowsParent(configuration, "parent1", allChickens, cow, cow.getParent1());
            String parent2ID = getCowsParent(configuration, "parent2", allChickens, cow, cow.getParent2());

            CowsRegistryItem parent1 = findCow(allChickens, parent1ID);
            CowsRegistryItem parent2 = findCow(allChickens, parent2ID);

            if (parent1 != null && parent2 != null) {
                cow.setParents(parent1, parent2);
            } else {
                cow.setNoParents();
            }

            SpawnType spawnType = SpawnType.valueOf(
                configuration.getString(
                    "spawnType",
                    cow.getEntityName(),
                    cow.getSpawnType()
                        .toString(),
                    "Chicken spawn type, can be: " + String.join(",", SpawnType.names())));
            cow.setSpawnType(spawnType);

            CowsRegistry.INSTANCE.register(cow);
        }
        configuration.save();

    }

    @SuppressWarnings("unused")
    private static ItemStack loadItemStack(Configuration configuration, CowsRegistryItem cow, String prefix,
        ItemStack defaultItemStack) {

        String defaultName = Item.itemRegistry.getNameForObject(defaultItemStack.getItem());
        if (defaultName == null) {
            defaultName = "minecraft:fire";
        }
        String itemName = configuration
            .get(
                cow.getEntityName(),
                prefix + "ItemName",
                defaultName,
                "Item registry name to be laid/dropped (ex: minecraft:egg)")
            .getString();

        int itemAmount = configuration.getInt(
            prefix + "ItemAmount",
            cow.getEntityName(),
            defaultItemStack.stackSize,
            1,
            64,
            "Item amount to be laid/dropped.");
        int itemMeta = configuration.getInt(
            prefix + "ItemMeta",
            cow.getEntityName(),
            defaultItemStack.getItemDamage(),
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Item amount to be laid/dropped.");

        Item item = GameData.getItemRegistry()
            .getObject(itemName);
        if (item == null) {
            if (defaultItemStack != null) {
                return defaultItemStack;
            } else {
                throw new RuntimeException("Cannot find egg item with name: " + itemName);
            }
        }
        return new ItemStack(item, itemAmount, itemMeta);
    }

    @SuppressWarnings("unused")
    private static FluidStack loadFluidStack(Configuration configuration, CowsRegistryItem cow, String prefix,
        FluidStack defaultFluidStack) {

        String defaultName = defaultFluidStack != null && defaultFluidStack.getFluid() != null
            ? defaultFluidStack.getFluid()
                .getName()
            : "water"; // fallback

        String fluidName = configuration
            .get(
                cow.getEntityName(),
                prefix + "FluidName",
                defaultName,
                "Fluid registry name to be used (ex: water, lava, milk)")
            .getString();

        int amount = configuration.getInt(
            prefix + "FluidAmount",
            cow.getEntityName(),
            defaultFluidStack != null ? defaultFluidStack.amount : 1000,
            1,
            Integer.MAX_VALUE,
            "Amount of fluid in mB");

        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if (fluid == null) {
            if (defaultFluidStack != null) {
                return defaultFluidStack;
            } else {
                throw new RuntimeException("Cannot find fluid with name: " + fluidName);
            }
        }

        return new FluidStack(fluid, amount);
    }

    public static CowsRegistryItem findCow(Collection<CowsRegistryItem> cows, String name) {

        for (CowsRegistryItem cow : cows) {
            if (cow.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return cow;
            }
        }

        return null;
    }
}
