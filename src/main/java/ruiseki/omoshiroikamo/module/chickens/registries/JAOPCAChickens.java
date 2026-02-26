package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.materials.MaterialHandler;
import thelm.jaopca.modules.ModuleHandler;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule
public class JAOPCAChickens extends BaseChickenHandler implements IModule {

    public static final Set<String> BLACKLIST = new TreeSet<>(
        Arrays.asList(
            // BaseChickens
            "Iron",
            "Gold",
            "Diamond",
            "Emerald",
            "Quartz",
            "Coal",
            "Lapis",
            "Redstone",
            "Glowstone",
            "Obsidian",
            "Clay",
            "Slime",
            "Leather",
            "Netherwart",
            "Blaze",
            "Ender",
            "Ghast",
            "Gunpowder",
            // MetalsChickens
            "Brass",
            "Cupronickel",
            "Electrum",
            "Invar",
            "Bronze",
            "Copper",
            "Lead",
            "Nickel",
            "Platinum",
            "Silver",
            "Tin",
            "Zinc",
            "Steel",
            "Silicon",
            "Sulfur",
            "Saltpeter",
            "Aluminium",
            "Aluminum",
            "Amber",
            "Amethyst",
            "Malachite",
            "Peridot",
            "Ruby",
            "Sapphire",
            "Tanzanite",
            "Topaz",
            "Garnet",
            "Salt",
            "Rubber",
            "Uranium"));

    static {
        if (LibMods.Botania.isLoaded()) {
            Collections.addAll(BLACKLIST, "Manasteel", "Terrasteel", "Elementium");
        }
        if (LibMods.TConstruct.isLoaded()) {
            Collections.addAll(BLACKLIST, "Ardite", "Cobalt", "Manyullyn", "PigIron", "BloodSlime", "BlueSlime");
        }
        if (LibMods.ThermalFoundation.isLoaded()) {
            Collections
                .addAll(BLACKLIST, "Basalz", "Blitz", "Blizz", "Cinnabar", "Enderium", "Lumium", "Mithril", "Signalum");
        }
        if (LibMods.EnderIO.isLoaded()) {
            Collections.addAll(
                BLACKLIST,
                "ElectricalSteel",
                "EnergeticAlloy",
                "VibrantAlloy",
                "RedstoneAlloy",
                "ConductiveIron",
                "PulsatingIron",
                "DarkSteel",
                "EndSteel",
                "Soularium");
        }
        if (LibMods.Mekanism.isLoaded()) {
            Collections.addAll(BLACKLIST, "Osmium");
        }
        if (LibMods.ActuallyAdditions.isLoaded()) {
            Collections
                .addAll(BLACKLIST, "Restonia", "Palis", "Diamantine", "Void", "Emeradic", "Enori", "BlackQuartz");
        }
        if (LibMods.MinefactoryReloaded.isLoaded()) {
            Collections.addAll(BLACKLIST, "PinkSlime");
        }
        if (LibMods.BigReactors.isLoaded()) {
            Collections.addAll(BLACKLIST, "Yellorium", "Cyanite", "Blutonium", "Ludicrite", "Graphite");
        }
        if (LibMods.DraconicEvolution.isLoaded()) {
            Collections.addAll(BLACKLIST, "Draconium", "DraconiumAwakened");
        }
    }

    public JAOPCAChickens() {
        super("jaopca", "JAOPCA", "textures/entity/chicken/original/");
        this.setStartID(3000);
    }

    private Map<IMaterial, IDynamicSpecConfig> configs;

    @Override
    public String getName() {
        return "chicken";
    }

    @Override
    public boolean isPassive() {
        return true;
    }

    @Override
    public Set<MaterialType> getMaterialTypes() {
        return EnumSet.allOf(MaterialType.class);
    }

    @Override
    public Set<String> getDefaultMaterialBlacklist() {
        return BLACKLIST;
    }

    @Override
    public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
        this.configs = configs;
    }

    @Override
    public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
        ModChickens.addModAddon(this);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        Logger.info("JAOPCA Chickens is loading.");
        List<ChickensRegistryItem> chickens = new ArrayList<>();
        if (!LibMods.JAOPCA.isLoaded()) {
            Logger.info("JAOPCA is not loaded.");
            return chickens;
        }

        IMiscHelper miscHelper = MiscHelper.INSTANCE;
        ItemFormType itemFormType = ItemFormType.INSTANCE;
        Logger.info(
            "JAOPCA Form has {} materials.",
            MaterialHandler.getMaterials()
                .size());

        IModuleData moduleData = ModuleHandler.getModuleData(this);

        for (IMaterial material : MaterialHandler.getMaterials()) {
            if (material == null) {
                Logger.info("Material {} is null.", material);
                continue;
            }
            String name = material.getName();
            if (BLACKLIST.contains(name)) continue;
            MaterialType type = material.getType();
            if (!(type.isIngot() || type.isGem() || type.isDust())) continue;

            int color = material.getColor();
            int bgColor = color;
            int fgColor = 0xFFFFFF;

            ChickensRegistryItem chicken = addChicken(
                name + "Chicken",
                nextID(),
                "base_chicken",
                bgColor,
                fgColor,
                SpawnType.NONE);

            chicken.setTextureOverlay(
                new ResourceLocation(LibMisc.MOD_ID, "textures/entity/chicken/original/base_chicken_overlay.png"));
            chicken.setTintColor(color);

            // Item Icon
            chicken.setIconName("omoshiroikamo:chicken/original/base_chicken");
            chicken.setIconOverlayName("omoshiroikamo:chicken/original/base_chicken_overlay");

            String prefix = getPrefix(material);
            String oredict = miscHelper.getOredictName(prefix, material.getName());
            chicken.setLayString("ore:" + oredict);

            // TODO: Add multi language support
            String enName = name + " Chicken";
            String jaName = name + "のニワトリ";

            chicken.getLang()
                .put("en_US", enName);
            chicken.getLang()
                .put("ja_JP", jaName);

            chickens.add(chicken);
        }

        return chickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {} // No parents auto generate.

    private String getPrefix(IMaterial material) {
        MaterialType type = material.getType();
        if (type.isIngot()) return "ingot";
        if (type.isGem()) return "gem";
        if (type.isCrystal()) return "crystal";
        if (type.isDust()) return "dust";
        return type.getFormName();
    }
}
