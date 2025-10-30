package ruiseki.omoshiroikamo.common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import ruiseki.omoshiroikamo.api.fluid.FluidEntry;
import ruiseki.omoshiroikamo.api.fluid.FluidRegistry;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.api.ore.OreEntry;
import ruiseki.omoshiroikamo.api.ore.OreRegistry;

public class LangSectionInserter {

    private static final String LANG_PATH = "src/main/resources/assets/omoshiroikamo/lang/en_US.lang";

    private static final List<Section> SECTIONS = new ArrayList<>();

    static {
        addMaterialSection(
            "#Item Wire Coil",
            mat -> "item.itemWireCoil." + mat.getUnlocalizedName() + ".name=Wire Coil (" + mat.getName() + ")");
        addMaterialSection(
            "#Item Material Ingot",
            mat -> "item.itemMaterial.ingot." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Ingot");
        addMaterialSection(
            "#Item Material Nugget",
            mat -> "item.itemMaterial.nugget." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Nugget");
        addMaterialSection(
            "#Item Material Plate",
            mat -> "item.itemMaterial.plate." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Plate");
        addMaterialSection(
            "#Item Material Rod",
            mat -> "item.itemMaterial.rod." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Rod");
        addMaterialSection(
            "#Item Material Dust",
            mat -> "item.itemMaterial.dust." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Dust");
        addMaterialSection(
            "#Item Material Gear",
            mat -> "item.itemMaterial.gear." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Gear");
        addMaterialSection(
            "#Item Bucket Material",
            mat -> "item.itemBucketMaterial." + mat.getUnlocalizedName() + ".name=Molten " + mat.getName() + " Bucket");
        addFluidSection(
            "#Item Bucket Fluid",
            mat -> "item.itemBucketFluid." + mat.getUnlocalizedName() + ".name=" + mat.getName() + " Bucket");

        addOreSection(
            "#Ore Crushed",
            ore -> "item.itemOre.crushed." + ore.getUnlocalizedName() + ".name=Crushed " + ore.getName() + " Ore");

        addOreSection(
            "#Ore Washed",
            ore -> "item.itemOre.washed." + ore.getUnlocalizedName() + ".name=Washed " + ore.getName() + " Ore");

        addOreSection(
            "#Ore Enriched",
            ore -> "item.itemOre.enriched." + ore.getUnlocalizedName() + ".name=Enriched " + ore.getName() + " Ore");

        // Block
        addMaterialSection(
            "#Block of Material",
            mat -> "tile.blockMaterial." + mat.getUnlocalizedName() + ".name=Block of " + mat.getName());
        addMaterialSection(
            "#Energy Input",
            mat -> "tile.blockEnergyInOut.input." + mat.getUnlocalizedName()
                + ".name=Energy Input ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Energy Output",
            mat -> "tile.blockEnergyInOut.output." + mat.getUnlocalizedName()
                + ".name=Energy Output ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Fluid Input",
            mat -> "tile.blockFluidInOut.input." + mat.getUnlocalizedName()
                + ".name=Fluid Input ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Fluid Output",
            mat -> "tile.blockFluidInOut.output." + mat.getUnlocalizedName()
                + ".name=Fluid Output ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Item Input",
            mat -> "tile.blockItemInOut.input." + mat.getUnlocalizedName()
                + ".name=Item Input ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Item Output",
            mat -> "tile.blockItemInOut.output." + mat.getUnlocalizedName()
                + ".name=Item Output ("
                + mat.getName()
                + ")");
        addMaterialSection(
            "#Block Fluid Material",
            mat -> "tile.fluid.molten." + StringUtils.uncapitalize(mat.getUnlocalizedName())
                + ".name=Molten "
                + mat.getName());
        addMaterialSection(
            "#Fluid Material",
            mat -> "fluid." + StringUtils.uncapitalize(mat.getUnlocalizedName()) + ".molten=Molten " + mat.getName());
        addMaterialSection(
            "#Tic Material",
            mat -> "material." + StringUtils.uncapitalize(mat.getUnlocalizedName()) + "=" + mat.getName());
        addFluidSection(
            "#Block Fluid",
            fluid -> "tile.fluid." + StringUtils.uncapitalize(fluid.getUnlocalizedName()) + ".name=" + fluid.getName());
        addFluidSection(
            "#Fluid",
            fluid -> "fluid." + StringUtils.uncapitalize(fluid.getUnlocalizedName()) + "=" + fluid.getName());

        addOreSection("#Ore", ore -> "tile.ore_" + ore.getUnlocalizedName() + ".name=" + ore.getName() + " Ore");
    }

    public static void main(String[] args) throws IOException {
        MaterialRegistry.preInit();
        FluidRegistry.preInit();
        OreRegistry.preInit();
        generateLang(
            new File(LANG_PATH),
            new ArrayList<>(MaterialRegistry.all()),
            new ArrayList<>(FluidRegistry.all()),
            new ArrayList<>(OreRegistry.all()),
            "# BEGIN AUTOGEN",
            "# END AUTOGEN");
    }

    // public static void insertCustomMaterialsLang(String[] materialNames) {
    // try {
    // File file = new File(Config.configDirectory.getAbsolutePath() + "/lang", "en_US.lang");
    //
    // List<MaterialEntry> materials = new ArrayList<>();
    // List<FluidEntry> fluids = new ArrayList<>();
    // List<OreEntry> ores = new ArrayList<>();
    //
    // for (String name : materialNames) {
    // MaterialEntry mat = MaterialRegistry.get(name);
    // if (mat != null) {
    // materials.add(mat);
    // }
    // FluidEntry fl = FluidRegistry.get(name);
    // if (fl != null) {
    // fluids.add(fl);
    // }
    // OreEntry or = OreRegistry.get(name);
    // if (or != null) {
    // ores.add(or);
    // } else {
    // System.out.println("[WARN] Missing ore entry: " + name);
    // }
    // }
    //
    // generateLang(file, materials, fluids, ores, "# BEGIN AUTOGEN", "# END AUTOGEN");
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    private static void generateLang(File file, Collection<MaterialEntry> materials, Collection<FluidEntry> fluids,
                                     Collection<OreEntry> ores, String beginTag, String endTag) throws IOException {

        List<String> existingLines = file.exists() ? Files.readAllLines(file.toPath()) : new ArrayList<>();
        List<String> cleaned = new ArrayList<>();
        boolean insideAutogen = false;

        for (String line : existingLines) {
            if (line.trim()
                .equals(beginTag)) {
                insideAutogen = true;
                continue;
            }
            if (line.trim()
                .equals(endTag)) {
                insideAutogen = false;
                continue;
            }
            if (!insideAutogen) {
                cleaned.add(line);
            }
        }

        Set<String> addedKeys = new HashSet<>();
        List<String> autogenBlock = new ArrayList<>();
        autogenBlock.add(beginTag);

        for (Section section : SECTIONS) {
            autogenBlock.add(section.label);

            if (section.generator instanceof MaterialLangGenerator matGen) {
                for (MaterialEntry material : materials) {
                    String line = matGen.generate(material);
                    String key = line.substring(0, line.indexOf("="));
                    if (addedKeys.add(key)) {
                        autogenBlock.add(line);
                    }
                }
            }

            if (section.generator instanceof FluidLangGenerator fluidGen) {
                for (FluidEntry fluid : fluids) {
                    String line = fluidGen.generate(fluid);
                    String key = line.substring(0, line.indexOf("="));
                    if (addedKeys.add(key)) {
                        autogenBlock.add(line);
                    }
                }
            }

            if (section.generator instanceof OreLangGenerator oreGen) {
                for (OreEntry ore : ores) {
                    String line = oreGen.generate(ore);
                    String key = line.substring(0, line.indexOf("="));
                    if (addedKeys.add(key)) {
                        autogenBlock.add(line);
                    }
                }
            }

            autogenBlock.add("");
        }

        autogenBlock.add(endTag);

        if (!cleaned.isEmpty() && !cleaned.get(cleaned.size() - 1)
            .isEmpty()) {
            cleaned.add("");
        }
        cleaned.addAll(autogenBlock);

        try (PrintWriter out = new PrintWriter(file)) {
            for (String line : cleaned) {
                out.println(line);
            }
        }
    }

    private static void addMaterialSection(String label, MaterialLangGenerator generator) {
        SECTIONS.add(new Section(label, generator));
    }

    private static void addFluidSection(String label, FluidLangGenerator generator) {
        SECTIONS.add(new Section(label, generator));
    }

    private static void addOreSection(String label, OreLangGenerator generator) {
        SECTIONS.add(new Section(label, generator));
    }

    private static class Section {

        String label;
        LangGenerator generator;

        Section(String label, LangGenerator generator) {
            this.label = label;
            this.generator = generator;
        }
    }

    interface LangGenerator {
    }

    interface MaterialLangGenerator extends LangGenerator {

        String generate(MaterialEntry material);
    }

    interface FluidLangGenerator extends LangGenerator {

        String generate(FluidEntry fluid);
    }

    interface OreLangGenerator extends LangGenerator {

        String generate(OreEntry fluid);
    }
}
