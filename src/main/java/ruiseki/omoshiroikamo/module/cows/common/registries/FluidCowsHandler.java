package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.config.backport.CowConfig;
import ruiseki.omoshiroikamo.core.common.util.Logger;

public class FluidCowsHandler extends BaseCowHandler {

    public FluidCowsHandler() {
        super("Misc", "Miscellaneous Fluids", "textures/entity/cows/base/");
        this.setStartID(3000);
        this.setNeedsModPresent(false);
        this.configFileName = "misc_cows.json";
    }

    @Override
    public List<CowsRegistryItem> registerCows() {
        List<CowsRegistryItem> generated = new ArrayList<>();
        Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();

        for (Map.Entry<String, Fluid> entry : fluids.entrySet()) {
            String fluidName = entry.getKey();
            Fluid fluid = entry.getValue();

            if (isBlacklisted(fluidName)) {
                continue;
            }

            // Create unique internal name
            String cowName = "Cow" + capitalize(fluidName);

            // Get color from fluid
            int color = 0xFFFFFF;
            try {
                color = fluid.getColor();
                if (color == 0xFFFFFFFF || color == 0) {
                    color = 0xAAAAAA;
                }
            } catch (Exception e) {
                Logger.error("Failed to get color from fluid " + fluidName);
            }

            // Use fluid color for bgColor, white for fgColor for better contrast
            CowsRegistryItem cow = addCow(cowName, this.nextID(), color, 0xFFFFFF, SpawnType.NORMAL);
            cow.setFluid(new FluidStack(fluid, 1000));
            cow.setTintColor(color);
            // Note: overlay.png does not exist, so we don't set a texture overlay
            // cow.setTextureOverlay(new ResourceLocation("omoshiroikamo", "textures/entity/cows/base/overlay.png"));

            // Generate localization
            try {
                String localizedFluid = fluid.getLocalizedName(new FluidStack(fluid, 1000));

                // Use fluid name as fallback if localized name is empty or just the unlocalized key
                String displayName = localizedFluid;
                if (displayName == null || displayName.isEmpty() || displayName.startsWith("fluid.") || displayName.startsWith("tile.")) {
                    displayName = fluidName;
                }

                cow.setLang("en_US", displayName + " Cow");
                cow.setLang("ja_JP", displayName + " ウシ");
            } catch (Exception e) {
                Logger.error("Failed to get localized name from fluid " + fluidName, e);
            }

            generated.add(cow);
        }

        return generated;
    }

    private static final Set<String> BLACKLIST = new HashSet<>(
        Arrays.asList(
            "water",
            "lava",
            // Big Reactors
            "yellorium",
            "cyanite",
            "steam",
            // BuildCraft
            "oil",
            "fuel",
            "redplasma",
            // EnderIO
            "nutrient_distillation",
            "hootch",
            "rocket_fuel",
            "fire_water",
            "liquid_sunshine",
            "cloud_seed",
            "cloud_seed_concentrated",
            "ender_distillation",
            "vapor_of_levity",
            // Mekanism
            "heavywater",
            "brine",
            "lithium",
            // MineFactory Reloaded
            "sludge",
            "sewage",
            "mobessence",
            "biofuel",
            "meat",
            "pinkslime",
            "chocolatemilk",
            "mushroomsoup",
            // Tinkers' Construct
            "iron.molten",
            "gold.molten",
            "copper.molten",
            "tin.molten",
            "aluminum.molten",
            "cobalt.molten",
            "ardite.molten",
            "bronze.molten",
            "aluminumbrass.molten",
            "manyullyn.molten",
            "obsidian.molten",
            "steel.molten",
            "glass.molten",
            "stone.seared",
            "emerald.liquid",
            "nickel.molten",
            "lead.molten",
            "silver.molten",
            "platinum.molten",
            "invar.molten",
            "electrum.molten",
            "lumium.molten",
            "signalum.molten",
            "mithril.molten",
            "enderium.molten",
            "pigiron.molten"));

    private boolean isBlacklisted(String name) {
        if (BLACKLIST.contains(name)) {
            return true;
        }

        // Check user config blacklist
        String configBlacklist = CowConfig.autogenBlacklist;
        if (configBlacklist != null && !configBlacklist.isEmpty()) {
            String[] split = configBlacklist.split(",");
            for (String s : split) {
                if (name.equalsIgnoreCase(s.trim())) {
                    return true;
                }
            }
        }

        return false;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        // Remove periods for name safety
        s = s.replace(".", "");
        return s.substring(0, 1)
            .toUpperCase() + s.substring(1);
    }
}
