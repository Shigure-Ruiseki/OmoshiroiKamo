package ruiseki.omoshiroikamo.core.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.jetbrains.annotations.Nullable;

public final class ItemNBTUtils {

    /**
     * Checks if an ItemStack has a Tag Compound
     **/
    public static boolean detectNBT(ItemStack stack) {
        return stack.hasTagCompound();
    }

    /**
     * Tries to initialize an NBT Tag Compound in an ItemStack,
     * this will not do anything if the stack already has a tag
     * compound
     **/
    public static void initNBT(ItemStack stack) {
        if (!detectNBT(stack)) {
            injectNBT(stack, new NBTTagCompound());
        }
    }

    /**
     * Injects an NBT Tag Compound to an ItemStack, no checks
     * are made previously
     **/
    public static void injectNBT(ItemStack stack, NBTTagCompound nbt) {
        stack.setTagCompound(nbt);
    }

    /**
     * Gets the NBTTagCompound in an ItemStack. Tries to init it
     * previously in case there isn't one present
     **/
    public static NBTTagCompound getNBT(ItemStack stack) {
        initNBT(stack);
        return stack.getTagCompound();
    }

    // SETTERS ///////////////////////////////////////////////////////////////////

    public static void setBoolean(ItemStack stack, String tag, boolean b) {
        getNBT(stack).setBoolean(tag, b);
    }

    public static void setByte(ItemStack stack, String tag, byte b) {
        getNBT(stack).setByte(tag, b);
    }

    public static void setShort(ItemStack stack, String tag, short s) {
        getNBT(stack).setShort(tag, s);
    }

    public static void setInt(ItemStack stack, String tag, int i) {
        getNBT(stack).setInteger(tag, i);
    }

    public static void setIntArray(ItemStack stack, String tag, int[] i) {
        getNBT(stack).setIntArray(tag, i);
    }

    public static void setLong(ItemStack stack, String tag, long l) {
        getNBT(stack).setLong(tag, l);
    }

    public static void setFloat(ItemStack stack, String tag, float f) {
        getNBT(stack).setFloat(tag, f);
    }

    public static void setDouble(ItemStack stack, String tag, double d) {
        getNBT(stack).setDouble(tag, d);
    }

    public static void setCompound(ItemStack stack, String tag, NBTTagCompound cmp) {
        if (!tag.equalsIgnoreCase("ench")) // not override the enchantments
        {
            getNBT(stack).setTag(tag, cmp);
        }
    }

    public static void setString(ItemStack stack, String tag, String s) {
        getNBT(stack).setString(tag, s);
    }

    public static void setList(ItemStack stack, String tag, NBTTagList list) {
        getNBT(stack).setTag(tag, list);
    }

    // GETTERS ///////////////////////////////////////////////////////////////////

    public static boolean verifyExistance(ItemStack stack, String tag) {
        return stack != null && getNBT(stack).hasKey(tag);
    }

    public static void remove(ItemStack stack, String key) {
        if (verifyExistance(stack, key)) {
            getNBT(stack).removeTag(key);
            if (getNBT(stack).hasNoTags()) {
                stack.setTagCompound(null);
            }
        }
    }

    public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getBoolean(tag) : defaultExpected;
    }

    public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getByte(tag) : defaultExpected;
    }

    public static short getShort(ItemStack stack, String tag, short defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getShort(tag) : defaultExpected;
    }

    public static int getInt(ItemStack stack, String tag, int defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getInteger(tag) : defaultExpected;
    }

    public static int[] getIntArray(ItemStack stack, String tag, int defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getIntArray(tag) : new int[defaultExpected];
    }

    public static long getLong(ItemStack stack, String tag, long defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getLong(tag) : defaultExpected;
    }

    public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getFloat(tag) : defaultExpected;
    }

    public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getDouble(tag) : defaultExpected;
    }

    /**
     * If nullifyOnFail is true it'll return null if it doesn't find any
     * compounds, otherwise it'll return a new one.
     **/
    public static NBTTagCompound getCompound(ItemStack stack, String tag, boolean nullifyOnFail) {
        return verifyExistance(stack, tag) ? getNBT(stack).getCompoundTag(tag)
            : nullifyOnFail ? null : new NBTTagCompound();
    }

    @Nullable
    public static NBTTagCompound getCompoundOrNull(ItemStack stack, String tag) {
        return verifyExistance(stack, tag) ? getNBT(stack).getCompoundTag(tag) : null;
    }

    public static NBTTagCompound getOrCreateCompound(ItemStack stack, String tag) {
        NBTTagCompound root = getNBT(stack);
        if (!root.hasKey(tag)) {
            root.setTag(tag, new NBTTagCompound());
        }
        return root.getCompoundTag(tag);
    }

    public static String getString(ItemStack stack, String tag, String defaultExpected) {
        return verifyExistance(stack, tag) ? getNBT(stack).getString(tag) : defaultExpected;
    }

    public static NBTTagList getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
        return verifyExistance(stack, tag) ? getNBT(stack).getTagList(tag, objtype)
            : nullifyOnFail ? null : new NBTTagList();
    }

    public static List<String> listKeys(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return Collections.emptyList();
        }

        NBTTagCompound nbt = getNBT(stack);
        return new ArrayList<>(nbt.func_150296_c());
    }

    public static List<String> listKeys(ItemStack stack, String tag) {
        if (!verifyExistance(stack, tag)) {
            return Collections.emptyList();
        }

        NBTTagCompound nbt = getNBT(stack).getCompoundTag(tag);
        return new ArrayList<>(nbt.func_150296_c());
    }

    public static NBTTagCompound stackToNbt(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        stack.writeToNBT(tag);
        return tag;
    }

    public static ItemStack nbtToStack(NBTTagCompound tag) {
        return ItemStack.loadItemStackFromNBT(tag);
    }

}
