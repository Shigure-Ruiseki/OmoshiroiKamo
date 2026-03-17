package ruiseki.omoshiroikamo.api.recipe.expression;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

/**
 * Utility for inferring NBT types from values with optional type suffixes.
 *
 * Supported suffixes:
 * - 'b' or 'B': Byte (e.g., "5b")
 * - 's' or 'S': Short (e.g., "16s")
 * - 'i' or 'I': Int (e.g., "100i")
 * - 'L': Long (e.g., "100L")
 * - 'f' or 'F': Float (e.g., "1.5f")
 * - 'd' or 'D': Double (e.g., "1.5d")
 *
 * Defaults (no suffix):
 * - Integer values -> Int
 * - Decimal values -> Float
 * - Quoted strings -> String
 */
public class NBTTypeInference {

    /**
     * Parse a string value and create the appropriate NBTBase.
     *
     * @param value The string representation of the value (may include type suffix)
     * @return The corresponding NBTBase object
     */
    public static NBTBase parseValue(String value) {
        if (value == null || value.isEmpty()) {
            return new NBTTagString("");
        }

        // Check for type suffix
        char lastChar = value.charAt(value.length() - 1);
        String numPart = value;

        if (!Character.isDigit(lastChar) && lastChar != '.') {
            // Has a suffix
            numPart = value.substring(0, value.length() - 1);

            try {
                switch (lastChar) {
                    case 'b':
                    case 'B':
                        return new NBTTagByte(Byte.parseByte(numPart));
                    case 's':
                    case 'S':
                        return new NBTTagShort(Short.parseShort(numPart));
                    case 'i':
                    case 'I':
                        return new NBTTagInt(Integer.parseInt(numPart));
                    case 'L':
                        return new NBTTagLong(Long.parseLong(numPart));
                    case 'f':
                    case 'F':
                        return new NBTTagFloat(Float.parseFloat(numPart));
                    case 'd':
                    case 'D':
                        return new NBTTagDouble(Double.parseDouble(numPart));
                    default:
                        // Not a recognized suffix, treat as string
                        return new NBTTagString(value);
                }
            } catch (NumberFormatException e) {
                // Not a number with suffix, treat as string
                return new NBTTagString(value);
            }
        }

        // No suffix, infer type from value
        try {
            if (value.contains(".")) {
                // Decimal -> Double by default (higher precision)
                return new NBTTagDouble(Double.parseDouble(value));
            } else {
                // Integer -> Int by default
                return new NBTTagInt(Integer.parseInt(value));
            }
        } catch (NumberFormatException e) {
            // Not a number, treat as string
            return new NBTTagString(value);
        }
    }

    /**
     * Parse a numeric value and create the appropriate NBTBase.
     *
     * @param value The numeric value
     * @return The corresponding NBTBase object (Int or Double)
     */
    public static NBTBase parseNumeric(double value) {
        if (value == (int) value) {
            // Integer value
            return new NBTTagInt((int) value);
        } else {
            // Decimal value
            return new NBTTagDouble(value);
        }
    }

    /**
     * Create an NBTTagString.
     *
     * @param value The string value
     * @return NBTTagString
     */
    public static NBTBase parseString(String value) {
        return new NBTTagString(value);
    }
}
