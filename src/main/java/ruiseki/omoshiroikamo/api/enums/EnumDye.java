package ruiseki.omoshiroikamo.api.enums;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public enum EnumDye {

    BLACK,
    RED,
    GREEN,
    BROWN,
    BLUE,
    PURPLE,
    CYAN,
    LIGHT_GRAY,
    GRAY,
    PINK,
    LIME,
    YELLOW,
    LIGHT_BLUE,
    MAGENTA,
    ORANGE,
    WHITE,
    CRYSTAL;

    public static final String[] DYE_ORE_NAMES = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple",
        "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta",
        "dyeOrange", "dyeWhite" };

    public static EnumDye getNext(EnumDye col) {
        int ord = col.ordinal() + 1;
        if (ord >= EnumDye.values().length) {
            ord = 0;
        }
        return EnumDye.values()[ord];
    }

    public static EnumDye getColorFromDye(ItemStack dye) {
        if (dye == null) {
            return null;
        }
        int[] oreIDs = OreDictionary.getOreIDs(dye);
        for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
            int dyeID = OreDictionary.getOreID(DYE_ORE_NAMES[i]);
            for (int oreId : oreIDs) {
                if (dyeID == oreId) {
                    return EnumDye.values()[i];
                }
            }
        }
        return null;
    }

    public static EnumDye fromIndex(int index) {
        return EnumDye.values()[index];
    }

    private EnumDye() {}

    public int getColor() {
        if (this == CRYSTAL) {
            return 0xAAFFFF; // Crystal color (Light Blue-ish)
        }
        return ItemDye.field_150922_c[ordinal()];
    }

    public String getName() {
        if (this == CRYSTAL) {
            return "crystal";
        }
        return ItemDye.field_150921_b[ordinal()];
    }

    @Override
    public String toString() {
        return getName();
    }

    public static int rgbToAbgr(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return (0xFF << 24) | (b << 16) | (g << 8) | r;
    }

    public static int rgbToAbgr(int r, int g, int b) {
        return (0xFF << 24) | (b << 16) | (g << 8) | r;
    }

    public static int rgbToHex(int r, int g, int b) {
        int a = 255; // alpha full
        return ((a & 0xFF) << 24) | // alpha
            ((r & 0xFF) << 16) | // red
            ((g & 0xFF) << 8) | // green
            (b & 0xFF); // blue
    }

    /**
     * Converts RGB components to RGB hex format (0xRRGGBB) for GTNHLib 0.9.45+ IBlockColor.
     * This is the correct format for colorMultiplier methods.
     *
     * @param r Red component (0-255)
     * @param g Green component (0-255)
     * @param b Blue component (0-255)
     * @return RGB color as 0xRRGGBB
     */
    public static int toRgb(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Converts RGB hex to RGB hex format (strips alpha if present).
     *
     * @param rgb RGB or ARGB color value
     * @return RGB color as 0xRRGGBB
     */
    public static int toRgb(int rgb) {
        return rgb & 0xFFFFFF;
    }

    public int dyeToAbgr() {
        int rgb = this.getColor();
        return rgbToAbgr(rgb);
    }

    /**
     * Converts this dye's color to RGB hex format (0xRRGGBB) for GTNHLib 0.9.45+ IBlockColor.
     *
     * @return RGB color as 0xRRGGBB
     */
    public int dyeToRgb() {
        return toRgb(this.getColor());
    }
}
