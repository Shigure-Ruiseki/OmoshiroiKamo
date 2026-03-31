package ruiseki.omoshiroikamo.module.machinery.common.item;

import lombok.Getter;

/**
 * Define the type of intermediate material.
 * This is a template of new materials.
 */
public enum EnumMaterial {

    NEUTRONIUM(0, "neutronium", "Neutronium"),
    // HYDROGEN(1, "hydrogen", "Hydrogen"),
    // HELIUM(2, "helium", "Helium"),
    // LITHIUM(3, "lithium", "Lithium"),
    // BERYLLIUM(4, "beryllium", "Beryllium"),
    BORON(5, "boron", "Boron"),
    // CARBON(6, "carbon", "Carbon"),
    // NITROGEN(7, "nitrogen", "Nitrogen"),
    // OXYGEN(8, "oxygen", "Oxygen"),
    // FLUORINE(9, "fluorine", "Fluorine"),
    // NEON(10, "neon", "Neon"),
    // SODIUM(11, "sodium", "Sodium"),
    // MAGNESIUM(12, "magnesium", "Magnesium"),
    ALUMINUM(13, "aluminium", "Aluminium"),
    // SILICON(14, "silicon", "Silicon"),
    // PHOSPHORUS(15, "phosphorus", "Phosphorus"),
    // SULFUR(16, "sulfur", "Sulfur"),
    // CHLORINE(17, "chlorine", "Chlorine"),
    // ARGON(18, "argon", "Argon"),
    // POTASSIUM(19, "potassium", "Potassium"),
    // CALCIUM(20, "calcium", "Calcium"),
    // SCANDIUM(21, "scandium", "Scandium"),
    TITANIUM(22, "titanium", "Titanium"),
    VANADIUM(23, "vanadium", "Vanadium"),
    CHROMIUM(24, "chromium", "Chromium"),
    MANGANESE(25, "manganese", "Manganese"),
    // IRON(26, "iron", "Iron"),
    COBALT(27, "cobalt", "Cobalt"),
    NICKEL(28, "nickel", "Nickel"),
    COPPER(29, "copper", "Copper"),
    ZINC(30, "zinc", "Zinc"),
    GALLIUM(31, "gallium", "Gallium"),
    // GERMANIUM(32, "germanium", "Germanium"),
    // ARSENIC(33, "arsenic", "Arsenic"),
    // SELENIUM(34, "selenium", "Selenium"),
    // BROMINE(35, "bromine", "Bromine"),
    // KRYPTON(36, "krypton", "Krypton"),
    // RUBIDIUM(37, "rubidium", "Rubidium"),
    // STRONTIUM(38, "strontium", "Strontium"),
    // YTTRIUM(39, "yttrium", "Yttrium"),
    // ZIRCONIUM(40, "zirconium", "Zirconium"),
    NIOBIUM(41, "niobium", "Niobium"),
    MOLYBDENUM(42, "molybdenum", "Molybdenum"),
    // TECHNETIUM(43, "technetium", "Technetium"),
    RUTHENIUM(44, "ruthenium", "Ruthenium"),
    RHODIUM(45, "rhodium", "Rhodium"),
    PALLADIUM(46, "palladium", "Palladium"),
    SILVER(47, "silver", "Silver"),
    // CADMIUM(48, "cadmium", "Cadmium"),
    INDIUM(49, "indium", "Indium"),
    TIN(50, "tin", "Tin"),
    // ANTIMONY(51, "antimony", "Antimony"),
    // TELLURIUM(52, "tellurium", "Tellurium"),
    // IODINE(53, "iodine", "Iodine"),
    // XENON(54, "xenon", "Xenon"),
    // CESIUM(55, "cesium", "Cesium"),
    // BARIUM(56, "barium", "Barium"),
    // LANTHANUM(57, "lanthanum", "Lanthanum"),
    // CERIUM(58, "cerium", "Cerium"),
    // PRASEODYMIUM(59, "praseodymium", "Praseodymium"),
    // NEODYMIUM(60, "neodymium", "Neodymium"),
    // PROMETHIUM(61, "promethium", "Promethium"),
    // SAMARIUM(62, "samarium", "Samarium"),
    EUROPIUM(63, "europium", "Europium"),
    // GADOLINIUM(64, "gadolinium", "Gadolinium"),
    // TERBIUM(65, "terbium", "Terbium"),
    // DYSPROSIUM(66, "dysprosium", "Dysprosium"),
    HOLMIUM(67, "holmium", "Holmium"),
    // ERBIUM(68, "erbium", "Erbium"),
    // THULIUM(69, "thulium", "Thulium"),
    // YTTERBIUM(70, "ytterbium", "Ytterbium"),
    // LUTETIUM(71, "lutetium", "Lutetium"),
    // HAFNIUM(72, "hafnium", "Hafnium"),
    // TANTALUM(73, "tantalum", "Tantalum"),
    TUNGSTEN(74, "tungsten", "Tungsten"),
    // RHENIUM(75, "rhenium", "Rhenium"),
    OSMIUM(76, "osmium", "Osmium"),
    IRIDIUM(77, "iridium", "Iridium"),
    PLATINUM(78, "platinum", "Platinum"),
    // GOLD(79, "gold", "Gold"),
    // MERCURY(80, "mercury", "Mercury"),
    // THALLIUM(81, "thallium", "Thallium"),
    // LEAD(82, "lead", "Lead"),
    // BISMUTH(83, "bismuth", "Bismuth"),
    // POLONIUM(84, "polonium", "Polonium"),
    // ASTATINE(85, "astatine", "Astatine"),
    // RADON(86, "radon", "Radon"),
    // FRANCIUM(87, "francium", "Francium"),
    RADIUM(88, "radium", "Radium"),
    // ACTINIUM(89, "actinium", "Actinium"),
    THORIUM(90, "thorium", "Thorium"),
    // PROTACTINIUM(91, "protactinium", "Protactinium"),
    URANIUM(92, "uranium", "Uranium"),
    // NEPTUNIUM(93, "neptunium", "Neptunium"),
    // PLUTONIUM(94, "plutonium", "Plutonium"),
    // AMERICIUM(95, "americium", "Americium"),
    // CURIUM(96, "curium", "Curium"),
    // BERKELIUM(97, "berkelium", "Berkelium"),
    // CALIFORNIUM(98, "californium", "Californium"),
    // EINSTEINIUM(99, "einsteinium", "Einsteinium"),
    // FERMIUM(100, "fermium", "Fermium"),
    // MENDELEVIUM(101, "mendelevium", "Mendelevium"),
    // NOBELIUM(102, "nobelium", "Nobelium"),
    // LAWRENCIUM(103, "lawrencium", "Lawrencium"),
    // RUTHERFORDIUM(104, "rutherfordium", "Rutherfordium"),
    // DUBNIUM(105, "dubnium", "Dubnium"),
    // SEABORGIUM(106, "seaborgium", "Seaborgium"),
    // BOHRIUM(107, "bohrium", "Bohrium"),
    // HASSIUM(108, "hassium", "Hassium"),
    // MEITNERIUM(109, "meitnerium", "Meitnerium"),
    // DARMSTADTIUM(110, "darmstadtium", "Darmstadtium"),
    // ROENTGENIUM(111, "roentgenium", "Roentgenium"),
    // COPERNICIUM(112, "copernicium", "Copernicium"),
    // NIHONIUM(113, "nihonium", "Nihonium"),
    // FLEROVIUM(114, "flerovium", "Flerovium"),
    // MOSCOVIUM(115, "moscovium", "Moscovium"),
    // LIVERMORIUM(116, "livermorium", "Livermorium"),
    // TENNESSINE(117, "tennessine", "Tennessine"),
    // OGANESSON(118, "oganesson", "Oganesson")

    // Alloy
    OBSIDIAN(130, "obsidian", "Obsidian"),
    BRONZE(131, "bronze", "Bronze"),
    WROUGHTIRON(132, "wroughtiron", "WroughtIron"),
    GRAPHITE(133, "graphite", "Graphite"),
    STEEL(134, "steel", "Steel"),
    CUPRONICKEL(135, "cupronickel", "Cupronickel"),
    KANTHAL(136, "kanthal", "Kanthal"),
    STAINLESSSTEEL(160, "stainlesssteel", "StainlessSteel"),
    TUNGSTENSTEEL(161, "tungstensteel", "TungstenSteel"),
    VANADIUMSTEEL(162, "vanadiumsteel", "VanadiumSteel"),
    HSS_G(220, "hss_g", "HSS-G"),
    HSS_S(221, "hss_s", "HSS-S"),
    OSMIRIDIUM(250, "osmiridium", "Osmiridium"),
    RHODIUMPALLADIUM(251, "rhodiumpalladium", "RhodiumPalladium"),
    THOURANIUM(252, "thouranium", "Thouranium"),
    TOUGHALLOY(270, "toughalloy", "ToughAlloy"),

    // MYTHIC
    ADAMANTIUM(1200, "adamantium", "Adamantium"),
    AURARIUM(1210, "aurarium", "Aurarium"),
    FREEZIUM(1220, "freezium", "Freezium"),
    ENDERALLOY(1230, "enderalloy", "EnderAlloy"),
    HIHIIRIOKANE(1240, "hihiirokane", "Hihiirokane"),
    OPTICALLIUM(1250, "opticallium", "Opticallium"),
    ORIHalkON(1260, "orihalkon", "Orihalkon"),
    PARADOX(1270, "paradox", "Paradox"),
    VOIDSTEEL(1280, "voidsteel", "VoidSteel"),
    VULCANIUM(1290, "vulcanium", "Vulcanium"),

    ;

    @Getter
    private final int meta;
    @Getter
    private final String name; // internal name, texture name, translation key
    @Getter
    private final String oreName; // suffix for ore dictionary

    EnumMaterial(int meta, String name, String oreName) {
        this.meta = meta;
        this.name = name;
        this.oreName = oreName;
    }

    public static EnumMaterial byMetadata(int meta) {
        for (EnumMaterial material : values()) {
            if (material.getMeta() == meta) {
                return material;
            }
        }
        return null;
    }
}
