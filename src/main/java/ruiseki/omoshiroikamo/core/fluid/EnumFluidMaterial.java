package ruiseki.omoshiroikamo.core.fluid;

import lombok.Getter;

/**
 * Material definitions for various fluids and gases.
 *
 * density : positive → heavier than air → flows DOWN
 * negative → lighter than air → flows UP (water = 1000)
 * viscosity : Forge fluid viscosity. Affects tick rate (higher = slower flow).
 * Water = 1000, lava = 6000.
 * quanta : How many blocks the fluid spreads horizontally (quantaPerBlock).
 * Water = 8, lava = 4.
 * drag : Velocity multiplied by (1 - drag) per tick while inside.
 * 0.0 = no resistance, 1.0 = instant stop.
 */
public enum EnumFluidMaterial {

    // -------------------------------------------------------------------------
    // Noble Gases
    // He(0.164), Ne(0.90) < air(1.225) → UP
    // Ar(1.78), Kr(3.75), Xe(5.9), Rn(9.7), Og(predicted ~40) > air → DOWN
    // -------------------------------------------------------------------------
    // name color density temp gas visc quanta drag
    HELIUM("helium", 0xCEF4FB, -1000, 300, true, 200, 8, 0.01f),
    NEON("neon", 0xFF6600, -300, 300, true, 250, 8, 0.02f),
    ARGON("argon", 0x9933FF, 300, 300, true, 300, 7, 0.03f),
    KRYPTON("krypton", 0xC0C0C0, 600, 300, true, 350, 7, 0.04f),
    XENON("xenon", 0x66CCFF, 900, 300, true, 400, 6, 0.05f),
    RADON("radon", 0xFF00FF, 1500, 300, true, 500, 6, 0.05f),
    OGANESSON("oganesson", 0x333333, 2000, 300, true, 600, 4, 0.06f),

    // -------------------------------------------------------------------------
    // Halogens
    // F2(1.70), Cl2(3.21) > air → DOWN (gases)
    // Br2: liquid (3120 kg/m³), I2: solid (~4930 kg/m³), At/Ts: solid (estimated)
    // -------------------------------------------------------------------------
    FLUORINE("fluorine", 0xFFFF99, 200, 300, true, 300, 8, 0.03f),
    CHLORINE("chlorine", 0xCCFF33, 800, 300, true, 400, 6, 0.05f),
    BROMINE("bromine", 0x990000, 3000, 300, false, 2000, 6, 0.65f),
    IODINE("iodine", 0x660066, 4000, 300, false, 4000, 4, 0.70f),
    ASTATINE("astatine", 0x330033, 6000, 300, false, 5000, 3, 0.75f),
    TENNESSINE("tennessine", 0x110011, 7000, 300, false, 6000, 3, 0.80f),

    // -------------------------------------------------------------------------
    // Industrial gases
    // H2(0.09), steam(~0.8) < air → UP
    // O2(1.43), N2(1.25), CO2(1.98) > air → DOWN
    // -------------------------------------------------------------------------
    HYDROGEN("hydrogen", 0xFFFFFF, -1200, 300, true, 100, 8, 0.01f),
    OXYGEN("oxygen", 0x6080FF, 150, 300, true, 250, 8, 0.02f),
    NITROGEN("nitrogen", 0xCCFFFF, 50, 300, true, 200, 8, 0.02f),
    CARBON_DIOXIDE("carbon_dioxide", 0x444444, 500, 300, true, 300, 6, 0.03f),
    STEAM("steam", 0xBBBBBB, -200, 600, true, 150, 8, 0.02f),

    // -------------------------------------------------------------------------
    // Industrial liquids
    // -------------------------------------------------------------------------
    OIL("oil", 0x111111, 850, 300, false, 3000, 3, 0.90f),
    FUEL("fuel", 0xEEEE00, 740, 300, false, 1500, 4, 0.50f),
    BIOFUEL("biofuel", 0x44AA44, 850, 300, false, 2500, 4, 0.55f),
    ETHANOL("ethanol", 0xAA6600, 790, 300, false, 700, 8, 0.40f),
    MANA("mana", 0x44FFFF, 500, 300, false, 800, 6, 0.25f),

    ;

    @Getter
    private final String name;
    @Getter
    private final int color;
    @Getter
    private final int density;
    @Getter
    private final int temperature;
    @Getter
    private final boolean gaseous;
    @Getter
    private final int viscosity;
    @Getter
    private final int quantaPerBlock;
    @Getter
    private final float drag;

    EnumFluidMaterial(String name, int color, int density, int temperature, boolean gaseous, int viscosity,
        int quantaPerBlock, float drag) {
        this.name = name;
        this.color = color;
        this.density = density;
        this.temperature = temperature;
        this.gaseous = gaseous;
        this.viscosity = viscosity;
        this.quantaPerBlock = quantaPerBlock;
        this.drag = drag;
    }
}
