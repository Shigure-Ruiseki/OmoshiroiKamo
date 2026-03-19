package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.recipe.expression.NBTPattern.OperationType;

/**
 * Handles operations on NBT lists (e.g., enchantment arrays).
 * Supports pattern matching and modification of list items.
 */
public class NBTListOperation {

    private final String path;
    private final List<NBTPattern> patterns;

    public NBTListOperation(String path, List<NBTPattern> patterns) {
        this.path = path;
        this.patterns = patterns;
    }

    /**
     * Check if all REQUIRE patterns are satisfied.
     *
     * @param nbt The NBT compound containing the list
     * @return True if all requirements are met
     */
    public boolean matches(NBTTagCompound nbt) {
        if (nbt == null || !nbt.hasKey(path)) return false;

        NBTBase base = nbt.getTag(path);
        if (!(base instanceof NBTTagList)) return false;

        NBTTagList list = (NBTTagList) base;

        // Check all REQUIRE patterns
        for (NBTPattern pattern : patterns) {
            if (pattern.getOperationType() == OperationType.REQUIRE) {
                boolean found = false;
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound item = list.getCompoundTagAt(i);
                    if (pattern.matches(item)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Apply all patterns to the NBT list.
     *
     * @param nbt The NBT compound containing the list
     * @return True if any modifications were made
     */
    public boolean apply(NBTTagCompound nbt) {
        if (nbt == null) return false;

        // Create list if it doesn't exist
        if (!nbt.hasKey(path)) {
            nbt.setTag(path, new NBTTagList());
        }

        NBTBase base = nbt.getTag(path);
        if (!(base instanceof NBTTagList)) return false;

        NBTTagList list = (NBTTagList) base;
        boolean modified = false;

        // Apply each pattern
        for (NBTPattern pattern : patterns) {
            switch (pattern.getOperationType()) {
                case REQUIRE:
                    // No modification for require patterns
                    break;

                case REMOVE:
                    modified |= removeMatching(list, pattern);
                    break;

                case MODIFY:
                case SET:
                    modified |= modifyOrSet(list, pattern);
                    break;
            }
        }

        // Update the list in the NBT
        if (modified) {
            nbt.setTag(path, list);
        }

        return modified;
    }

    /**
     * Remove all items matching the pattern from the list.
     */
    private boolean removeMatching(NBTTagList list, NBTPattern pattern) {
        boolean removed = false;

        // Iterate backwards to safely remove items
        for (int i = list.tagCount() - 1; i >= 0; i--) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            if (pattern.matches(item)) {
                list.removeTag(i);
                removed = true;
            }
        }

        return removed;
    }

    /**
     * Modify or set values in matching items, or add new item if none match.
     */
    private boolean modifyOrSet(NBTTagList list, NBTPattern pattern) {
        boolean found = false;

        // Try to find and modify existing items
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            // Use matchesForUpdate to identify the item even if SET value differs (to allow
            // update)
            if (pattern.matchesForUpdate(item)) {
                pattern.apply(item);
                found = true;
            }
        }

        // If SET pattern and no matching item found, add new item
        if (!found && pattern.getOperationType() == NBTPattern.OperationType.SET) {
            NBTTagCompound newItem = new NBTTagCompound();
            pattern.apply(newItem);
            list.appendTag(newItem);
            return true;
        }

        return found;
    }

    /**
     * Get the path to the NBT list.
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the list of patterns.
     */
    public List<NBTPattern> getPatterns() {
        return patterns;
    }

    /**
     * Parse NBTListOperation from JSON.
     *
     * Expected format:
     * {
     * "nbtlist": {
     * "path": "ench",
     * "ops": [
     * {"id": 16, "lvl": ">=5"},
     * {"id": 20, "lvl": 0},
     * {"id": 34, "lvl": "+1"}
     * ]
     * }
     * }
     */
    public static NBTListOperation fromJson(JsonObject json) {
        if (!json.has("nbtlist")) {
            throw new IllegalArgumentException("Missing 'nbtlist' field");
        }

        JsonObject nbtlist = json.getAsJsonObject("nbtlist");

        String path = nbtlist.has("path") ? nbtlist.get("path")
            .getAsString() : "Items";

        List<NBTPattern> patterns = new ArrayList<>();

        if (nbtlist.has("ops")) {
            JsonArray ops = nbtlist.getAsJsonArray("ops");
            for (JsonElement element : ops) {
                if (element.isJsonObject()) {
                    patterns.add(NBTPattern.fromJson(element.getAsJsonObject()));
                }
            }
        }

        return new NBTListOperation(path, patterns);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("path", path);
        NBTTagList patternList = new NBTTagList();
        for (NBTPattern pattern : patterns) {
            NBTTagCompound pTag = new NBTTagCompound();
            pattern.writeToNBT(pTag);
            patternList.appendTag(pTag);
        }
        nbt.setTag("patterns", patternList);
    }

    public static NBTListOperation readFromNBT(NBTTagCompound nbt) {
        String path = nbt.getString("path");
        NBTTagList patternList = nbt.getTagList("patterns", 10); // 10 is TAG_COMPOUND
        List<NBTPattern> patterns = new ArrayList<>();
        for (int i = 0; i < patternList.tagCount(); i++) {
            patterns.add(NBTPattern.readFromNBT(patternList.getCompoundTagAt(i)));
        }
        return new NBTListOperation(path, patterns);
    }

    @Override
    public String toString() {
        return "NBTListOperation{path='" + path + "', patterns=" + patterns.size() + "}";
    }
}
