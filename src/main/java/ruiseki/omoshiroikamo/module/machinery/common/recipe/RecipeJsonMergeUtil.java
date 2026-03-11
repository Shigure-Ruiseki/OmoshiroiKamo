package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility for merging JSON objects for recipe inheritance.
 */
public class RecipeJsonMergeUtil {

    /**
     * Merges parent JSON into child JSON.
     * Specific arrays (input, output, condition) are merged by prepending parent
     * items.
     * Other properties are only taken from parent if missing in child.
     * 
     * @param child  The child JSON (will be modified)
     * @param parent The parent JSON
     * @return The merged JSON (same instance as child)
     */
    public static JsonObject merge(JsonObject child, JsonObject parent) {
        if (parent == null) return child;

        for (Map.Entry<String, JsonElement> entry : parent.entrySet()) {
            String key = entry.getKey();
            JsonElement parentVal = entry.getValue();

            if (isNonInheritable(key)) continue;

            if (!child.has(key)) {
                // If child doesn't have it, just copy from parent
                child.add(key, copy(parentVal));
            } else {
                // If both have it, handle special cases (arrays)
                JsonElement childVal = child.get(key);
                if (isMergeableArray(key) && childVal.isJsonArray() && parentVal.isJsonArray()) {
                    child.add(key, mergeArrays(childVal.getAsJsonArray(), parentVal.getAsJsonArray()));
                }
                // For other primitives/objects, child takes precedence (already in child)
            }
        }
        return child;
    }

    private static boolean isNonInheritable(String key) {
        return key.equals("abstract") || key.equals("parent") || key.equals("registryName");
    }

    private static boolean isMergeableArray(String key) {
        return key.equals("input") || key.equals("inputs")
            || key.equals("output")
            || key.equals("outputs")
            || key.equals("condition")
            || key.equals("conditions");
    }

    private static JsonArray mergeArrays(JsonArray childArr, JsonArray parentArr) {
        // Current behavior: parent + child
        JsonArray result = new JsonArray();
        for (int i = 0; i < parentArr.size(); i++) {
            result.add(copy(parentArr.get(i)));
        }
        for (int i = 0; i < childArr.size(); i++) {
            result.add(copy(childArr.get(i)));
        }
        return result;
    }

    private static JsonElement copy(JsonElement element) {
        // Workaround for missing deepCopy() in older GSON versions
        return new JsonParser().parse(element.toString());
    }
}
