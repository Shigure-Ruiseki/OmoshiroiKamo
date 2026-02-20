package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.registry.EntityRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.event.NetherPopulateEvent;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

public class ModCows {

    public static void preInit() {
        if (!BackportConfigs.enableCows) return;

        EntityRegistry
            .registerModEntity(EntityCowsCow.class, "cow", CowConfig.cowEntityId, OmoshiroiKamo.instance, 64, 1, true);

    }

    public static void init() {
        if (!BackportConfigs.enableCows) return;
        registerModAddons();
    }

    public static void postInit() {
        if (!BackportConfigs.enableCows) return;

        loadConfiguration();

        List<BiomeGenBase> biomesForSpawning = getAllSpawnBiomes();
        if (!biomesForSpawning.isEmpty()) {
            EntityRegistry.addSpawn(
                EntityCowsCow.class,
                CowConfig.spawnProbability,
                CowConfig.minBroodSize,
                CowConfig.maxBroodSize,
                EnumCreatureType.creature,
                biomesForSpawning.toArray(new BiomeGenBase[biomesForSpawning.size()]));
            if (biomesForSpawning.contains(BiomeGenBase.hell)) {
                MinecraftForge.TERRAIN_GEN_BUS
                    .register(new NetherPopulateEvent(CowConfig.netherSpawnChanceMultiplier, EntityCowsCow.class));
            }
        }
    }

    public static ArrayList<BaseCowHandler> registeredModAddons = new ArrayList<>();

    private static void registerModAddons() {
        addModAddon(new BaseCows());
        addModAddon(new TinkersCows());
        addModAddon(new EnderIOCows());
        addModAddon(new MekanismCows());
        addModAddon(new BuildCraftCows());
        addModAddon(new BigReactorsCows());
        addModAddon(new MineFactoryReloadedCows());
        addModAddon(new OriginalCows());
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
            cows = addon.tryRegisterCows(cows);
        }

        return cows;

    }

    private static void loadConfiguration() {
        Collection<CowsRegistryItem> allChickens = generateDefaultChickens();
        Logger.info("Cows Loading Config...");
        for (CowsRegistryItem cow : allChickens) {
            CowsRegistry.INSTANCE.register(cow);
        }
    }
}
