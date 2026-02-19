package ruiseki.omoshiroikamo.config.backport;

import com.gtnewhorizon.gtnhlib.config.Config;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

@Config.Comment("Modular Machinery settings")
@Config.LangKey(LibResources.CONFIG + "modularConfig")
@Config(
    modid = LibMisc.MOD_ID,
    category = "Modular",
    configSubDirectory = LibMisc.MOD_ID + "/modular",
    filename = "modular")
public class MachineryConfig {

    @Config.Comment("Default tint color for machine blocks (hex color code)")
    @Config.DefaultString("#FFFFFF")
    public static String defaultTintColor;

    @Config.Comment("Item Input/Output Port Slots (Tier 1-6)")
    @Config.DefaultString("1,4,6,9,12,16")
    public static String itemPortSlots = "1,4,6,9,12,16";

    @Config.Comment("Fluid Input/Output Port Capacity in mB (Tier 1-6)")
    @Config.DefaultString("1000,4000,16000,64000,256000,1024000")
    public static String fluidPortCapacity = "1000,4000,16000,64000,256000,1024000";

    @Config.Comment("Gas Input/Output Port Capacity in mB (Tier 1-6)")
    @Config.DefaultString("1000,4000,16000,64000,256000,1024000")
    public static String gasPortCapacity = "1000,4000,16000,64000,256000,1024000";

    @Config.Comment("Energy Input/Output Port Capacity in RF (Tier 1-6)")
    @Config.DefaultString("2048,8192,32768,131072,524288,2097152")
    public static String energyPortCapacity = "2048,8192,32768,131072,524288,2097152";

    @Config.Comment("Energy Input/Output Port Transfer Rate in RF/t (Tier 1-6)")
    @Config.DefaultString("128,512,2048,8192,32768,131072")
    public static String energyPortTransfer = "128,512,2048,8192,32768,131072";

    @Config.Comment("Mana Input/Output Port Capacity")
    @Config.DefaultInt(1000000)
    public static int manaPortCapacity = 100000;

    @Config.Comment("Mana Input/Output Port Transfer Rate")
    @Config.DefaultInt(10000)
    public static int manaPortTransfer = 10000;

    @Config.Comment("Essentia Input/Output Port Capacity per Aspect")
    @Config.DefaultInt(64)
    public static int essentiaPortCapacity = 64;

    @Config.Comment("Vis Input/Output Port Capacity per Aspect (centi-vis)")
    @Config.DefaultInt(100)
    public static int visPortCapacity = 100;

    @Config.Ignore
    private static int[] _itemPortSlots;
    @Config.Ignore
    private static int[] _fluidPortCapacity;
    @Config.Ignore
    private static int[] _gasPortCapacity;
    @Config.Ignore
    private static int[] _energyPortCapacity;
    @Config.Ignore
    private static int[] _energyPortTransfer;

    public static int[] getItemPortSlots() {
        if (_itemPortSlots == null) {
            _itemPortSlots = parseIntArray(itemPortSlots);
        }
        return _itemPortSlots;
    }

    public static int[] getFluidPortCapacity() {
        if (_fluidPortCapacity == null) {
            _fluidPortCapacity = parseIntArray(fluidPortCapacity);
        }
        return _fluidPortCapacity;
    }

    public static int[] getGasPortCapacity() {
        if (_gasPortCapacity == null) {
            _gasPortCapacity = parseIntArray(gasPortCapacity);
        }
        return _gasPortCapacity;
    }

    public static int[] getEnergyPortCapacity() {
        if (_energyPortCapacity == null) {
            _energyPortCapacity = parseIntArray(energyPortCapacity);
        }
        return _energyPortCapacity;
    }

    public static int[] getEnergyPortTransfer() {
        if (_energyPortTransfer == null) {
            _energyPortTransfer = parseIntArray(energyPortTransfer);
        }
        return _energyPortTransfer;
    }

    private static int[] parseIntArray(String str) {
        try {
            String[] split = str.split(",");
            int[] arr = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                arr[i] = Integer.parseInt(split[i].trim());
            }
            return arr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static int getItemPortSlots(int tier) {
        int[] slots = getItemPortSlots();
        if (tier < 1 || tier > slots.length) return 1;
        return slots[tier - 1];
    }

    public static int getFluidPortCapacity(int tier) {
        int[] caps = getFluidPortCapacity();
        if (tier < 1 || tier > caps.length) return 1000;
        return caps[tier - 1];
    }

    public static int getGasPortCapacity(int tier) {
        int[] caps = getGasPortCapacity();
        if (tier < 1 || tier > caps.length) return 1000;
        return caps[tier - 1];
    }

    public static int getEnergyPortCapacity(int tier) {
        int[] caps = getEnergyPortCapacity();
        if (tier < 1 || tier > caps.length) return 2048;
        return caps[tier - 1];
    }

    public static int getEnergyPortTransfer(int tier) {
        int[] transfers = getEnergyPortTransfer();
        if (tier < 1 || tier > transfers.length) return 128;
        return transfers[tier - 1];
    }

    /**
     * Parse the configured tint color string to an ARGB integer.
     * 
     * @return ARGB color value with full alpha, or white (0xFFFFFFFF) on parse
     *         error
     */
    public static int getDefaultTintColorInt() {
        try {
            String hex = defaultTintColor.replace("#", "");
            return (int) Long.parseLong(hex, 16) | 0xFF000000; // Add alpha channel
        } catch (Exception e) {
            return 0xFFFFFFFF; // White fallback
        }
    }
}
