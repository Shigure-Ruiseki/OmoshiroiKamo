package ruiseki.omoshiroikamo.plugin.chicken;

import static ruiseki.omoshiroikamo.config.general.ChickenConfigs.maxBroodSize;
import static ruiseki.omoshiroikamo.config.general.ChickenConfigs.minBroodSize;
import static ruiseki.omoshiroikamo.config.general.ChickenConfigs.netherSpawnChanceMultiplier;
import static ruiseki.omoshiroikamo.config.general.ChickenConfigs.spawnProbability;

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

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.entity.chicken.ChickenNetherPopulateHandler;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.general.ChickenConfigs;

public class ModChickens {

    public static void preInit() {

        EntityRegistry.registerModEntity(
            EntityChickensChicken.class,
            "chicken",
            ChickenConfigs.chickenEntityId,
            OmoshiroiKamo.instance,
            64,
            3,
            true);

        registerModAddons();
    }

    public static void init() {
        loadConfiguration();

        List<BiomeGenBase> biomesForSpawning = getAllSpawnBiomes();
        if (biomesForSpawning.size() > 0) {
            EntityRegistry.addSpawn(
                EntityChickensChicken.class,
                spawnProbability,
                minBroodSize,
                maxBroodSize,
                EnumCreatureType.creature,
                biomesForSpawning.toArray(new BiomeGenBase[biomesForSpawning.size()]));
            if (biomesForSpawning.contains(BiomeGenBase.hell)) {
                MinecraftForge.TERRAIN_GEN_BUS.register(new ChickenNetherPopulateHandler(netherSpawnChanceMultiplier));
            }
        }
    }

    public static ArrayList<BaseChickenHandler> registeredModAddons = new ArrayList<>();

    private static void registerModAddons() {
        addModAddon(new BaseChickens());
        addModAddon(new BotaniaChickens());
        addModAddon(new MetalsChickens());
        addModAddon(new EnderIOChickens());
        addModAddon(new ThermalChickens());
        addModAddon(new TinkersChickens());
        addModAddon(new MineFactoryReloadedChickens());
        addModAddon(new MekanismChickens());
        addModAddon(new BigReactorsChickens());
        addModAddon(new DraconicEvolutionChickens());
        addModAddon(new ActuallyAdditionsChickens());
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
            if (ChickensRegistry.isAnyIn(ChickensRegistry.getSpawnType(biome))) {
                biomesForSpawning.add(biome);
            }
        }
        return biomesForSpawning;
    }

    public static void addModAddon(BaseChickenHandler addon) {
        if (addon == null) {
            Logger.error("Tried to add null mod addon");
            return;
        }

        registeredModAddons.add(addon);
    }

    private static List<ChickensRegistryItem> generateDefaultChickens() {
        List<ChickensRegistryItem> chickens = new ArrayList<ChickensRegistryItem>();

        for (BaseChickenHandler addon : registeredModAddons) {
            chickens = addon.tryRegisterChickens(chickens);
        }

        // SetParents

        for (BaseChickenHandler addon : registeredModAddons) {
            Logger.debug("Register " + addon.getModName() + " Parents");
            addon.RegisterAllParents(chickens);
        }

        return chickens;

    }

    private static String getChickenParent(Configuration configuration, String propertyName,
        Collection<ChickensRegistryItem> allChickens, ChickensRegistryItem chicken, ChickensRegistryItem parent) {
        String Category = chicken.getEntityName();
        return configuration.getString(
            propertyName,
            Category,
            parent != null ? parent.getEntityName() : "",
            "First parent, empty if it's base chicken.");
    }

    private static void loadConfiguration() {

        File configDirectory = new File("config/" + LibMisc.MOD_ID + "/chicken");
        if (!configDirectory.exists()) {
            configDirectory.mkdir();
        }

        File configFile = new File(configDirectory, "base.cfg");
        Configuration configuration = new Configuration(configFile);

        Collection<ChickensRegistryItem> allChickens = generateDefaultChickens();

        configuration.addCustomCategoryComment(
            "0",
            "It is Ideal to regenerate this file after updates as your config files may overwrite changes made to core.");

        Logger.info("Chickens Loading Config...");
        for (ChickensRegistryItem chicken : allChickens) {

            boolean enabled = configuration.getBoolean("enabled", chicken.getEntityName(), true, "Is chicken enabled?");
            chicken.setEnabled(enabled);

            float layCoefficient = configuration
                .getFloat("layCoefficient", chicken.getEntityName(), 1.0f, 0.01f, 100.f, "Scale time to lay an egg.");
            chicken.setLayCoefficient(layCoefficient);

            ItemStack itemStack = loadItemStack(configuration, chicken, "egg", chicken.createLayItem());
            chicken.setLayItem(itemStack);

            ItemStack dropItemStack = loadItemStack(configuration, chicken, "drop", chicken.createDropItem());
            chicken.setDropItem(dropItemStack);

            String parent1ID = getChickenParent(configuration, "parent1", allChickens, chicken, chicken.getParent1());
            String parent2ID = getChickenParent(configuration, "parent2", allChickens, chicken, chicken.getParent2());

            ChickensRegistryItem parent1 = findChicken(allChickens, parent1ID);
            ChickensRegistryItem parent2 = findChicken(allChickens, parent2ID);

            if (parent1 != null && parent2 != null) {
                chicken.setParentsNew(parent1, parent2);
            } else {
                chicken.setNoParents();
            }

            SpawnType spawnType = SpawnType.valueOf(
                configuration.getString(
                    "spawnType",
                    chicken.getEntityName(),
                    chicken.getSpawnType()
                        .toString(),
                    "Chicken spawn type, can be: " + String.join(",", SpawnType.names())));
            chicken.setSpawnType(spawnType);

            ChickensRegistry.register(chicken);
        }
        configuration.save();

    }

    @SuppressWarnings("unused")
    private static ItemStack loadItemStack(Configuration configuration, ChickensRegistryItem chicken, String prefix,
        ItemStack defaultItemStack) {

        String defaultName = Item.itemRegistry.getNameForObject(defaultItemStack.getItem());
        if (defaultName == null) {
            defaultName = "minecraft:fire";
        }
        String itemName = configuration
            .get(
                chicken.getEntityName(),
                prefix + "ItemName",
                defaultName,
                "Item registry name to be laid/dropped (ex: minecraft:egg)")
            .getString();

        int itemAmount = configuration.getInt(
            prefix + "ItemAmount",
            chicken.getEntityName(),
            defaultItemStack.stackSize,
            1,
            64,
            "Item amount to be laid/dropped.");
        int itemMeta = configuration.getInt(
            prefix + "ItemMeta",
            chicken.getEntityName(),
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

    public static ChickensRegistryItem findChicken(Collection<ChickensRegistryItem> chickens, String name) {

        for (ChickensRegistryItem chicken : chickens) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return chicken;
            }
        }

        return findChickenChickensMod(name);
    }

    public static ChickensRegistryItem findChickenChickensMod(String name) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {

                return chicken;
            }
        }

        return null;
    }

}
