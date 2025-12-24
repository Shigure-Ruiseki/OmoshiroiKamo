package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.registry.EntityRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.entity.chicken.LiquidEggRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.LiquidEggRegistryItem;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.common.handler.NetherPopulateHandler;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

public class ModChickens {

    public static void preInit() {
        if (!BackportConfigs.useChicken) return;

        EntityRegistry.registerModEntity(
            EntityChickensChicken.class,
            "chicken",
            ChickenConfig.chickenEntityId,
            OmoshiroiKamo.instance,
            64,
            3,
            true);

        LiquidEggRegistry.register(new LiquidEggRegistryItem(0, Blocks.flowing_water, 0x0000ff, FluidRegistry.WATER));
        LiquidEggRegistry.register(new LiquidEggRegistryItem(1, Blocks.flowing_lava, 0xff0000, FluidRegistry.LAVA));

    }

    public static void init() {
        if (!BackportConfigs.useChicken) return;
        registerModAddons();
    }

    public static void postInit() {
        if (!BackportConfigs.useChicken) return;

        loadConfiguration();

        List<BiomeGenBase> biomesForSpawning = getAllSpawnBiomes();
        if (!biomesForSpawning.isEmpty()) {
            EntityRegistry.addSpawn(
                EntityChickensChicken.class,
                ChickenConfig.spawnProbability,
                ChickenConfig.minBroodSize,
                ChickenConfig.maxBroodSize,
                EnumCreatureType.creature,
                biomesForSpawning.toArray(new BiomeGenBase[biomesForSpawning.size()]));
            if (biomesForSpawning.contains(BiomeGenBase.hell)) {
                MinecraftForge.TERRAIN_GEN_BUS.register(
                    new NetherPopulateHandler(ChickenConfig.netherSpawnChanceMultiplier, EntityChickensChicken.class));
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
        addModAddon(new OriginalChickens());
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
            if (ChickensRegistry.INSTANCE.isAnyIn(ChickensRegistry.getSpawnType(biome))) {
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
        List<ChickensRegistryItem> chickens = new ArrayList<>();

        for (BaseChickenHandler addon : registeredModAddons) {
            chickens = addon.tryRegisterChickens(chickens);
        }

        // SetParents
        for (BaseChickenHandler addon : registeredModAddons) {
            Logger.debug("Register " + addon.getModName() + " Parents");
            addon.loadParents(chickens);
        }

        return chickens;

    }

    private static void loadConfiguration() {
        Collection<ChickensRegistryItem> allChickens = generateDefaultChickens();
        Logger.info("Chickens Loading Config...");
        for (ChickensRegistryItem chicken : allChickens) {
            ChickensRegistry.INSTANCE.register(chicken);
        }
    }
}
