package ruiseki.omoshiroikamo.core.json;

import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Utility class to convert between GSON JsonObjects and Minecraft
 * NBTTagCompounds.
 */
public class JsonNBTUtils {

    /**
     * Converts a JsonObject to an NBTTagCompound.
     */
    public static NBTTagCompound jsonToNBT(JsonObject json) {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            nbt.setTag(entry.getKey(), elementToNBT(entry.getValue()));
        }
        return nbt;
    }

    private static NBTBase elementToNBT(JsonElement element) {
        if (element.isJsonObject()) {
            return jsonToNBT(element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            NBTTagList list = new NBTTagList();
            for (JsonElement e : arr) {
                list.appendTag(elementToNBT(e));
            }
            return list;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive p = element.getAsJsonPrimitive();
            if (p.isBoolean()) {
                return new NBTTagByte((byte) (p.getAsBoolean() ? 1 : 0));
            } else if (p.isString()) {
                return new NBTTagString(p.getAsString());
            } else if (p.isNumber()) {
                Number n = p.getAsNumber();
                if (n instanceof Integer) return new NBTTagInt(n.intValue());
                if (n instanceof Long) return new NBTTagLong(n.longValue());
                if (n instanceof Double) return new NBTTagDouble(n.doubleValue());
                if (n instanceof Float) return new NBTTagFloat(n.floatValue());
                if (n instanceof Short) return new NBTTagShort(n.shortValue());
                if (n instanceof Byte) return new NBTTagByte(n.byteValue());

                // Fallback for GSON's LazilyParsedNumber
                String s = n.toString();
                try {
                    if (s.contains(".")) return new NBTTagDouble(n.doubleValue());
                    long l = n.longValue();
                    if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) return new NBTTagInt((int) l);
                    return new NBTTagLong(l);
                } catch (NumberFormatException e) {
                    return new NBTTagDouble(n.doubleValue());
                }
            }
        }
        return new NBTTagString("");
    }

    /**
     * Converts an NBTTagCompound to a JsonObject.
     */
    public static JsonObject nbtToJSON(NBTTagCompound nbt) {
        JsonObject json = new JsonObject();
        for (Object keyObj : nbt.func_150296_c()) {
            String key = (String) keyObj;
            json.add(key, nbtToElement(nbt.getTag(key)));
        }
        return json;
    }

    private static JsonElement nbtToElement(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound compound) {
            return nbtToJSON(compound);
        } else if (nbt instanceof NBTTagList list) {
            JsonArray arr = new JsonArray();
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound cmp = list.getCompoundTagAt(i);
                if (cmp.hasNoTags() && list.func_150303_d() != 10) {
                    if (list.func_150303_d() == 8) {
                        arr.add(new JsonPrimitive(list.getStringTagAt(i)));
                    } else {
                        arr.add(
                            new JsonPrimitive(
                                list.getCompoundTagAt(i)
                                    .toString()));
                    }
                } else {
                    arr.add(nbtToJSON(cmp));
                }
            }
            return arr;
        } else if (nbt instanceof NBTTagString s) {
            return new JsonPrimitive(s.func_150285_a_());
        } else if (nbt instanceof NBTTagInt i) {
            return new JsonPrimitive(i.func_150287_d());
        } else if (nbt instanceof NBTTagDouble d) {
            return new JsonPrimitive(d.func_150286_g());
        } else if (nbt instanceof NBTTagFloat f) {
            return new JsonPrimitive(f.func_150288_h());
        } else if (nbt instanceof NBTTagLong l) {
            return new JsonPrimitive(l.func_150291_c());
        } else if (nbt instanceof NBTTagShort s) {
            return new JsonPrimitive(s.func_150289_e());
        } else if (nbt instanceof NBTTagByte b) {
            return new JsonPrimitive(b.func_150290_f());
        } else if (nbt instanceof NBTTagIntArray ia) {
            JsonArray arr = new JsonArray();
            for (int val : ia.func_150302_c()) {
                arr.add(new JsonPrimitive(val));
            }
            return arr;
        }
        return new JsonPrimitive(nbt.toString());
    }
}
