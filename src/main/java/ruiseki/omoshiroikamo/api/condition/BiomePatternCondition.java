package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * A condition that checks a 2D pattern of conditions around the target
 * position.
 */
public class BiomePatternCondition implements ICondition {

    private final String[] pattern;
    private final Map<Character, ICondition> keys;

    public BiomePatternCondition(String[] pattern, Map<Character, ICondition> keys) {
        this.pattern = pattern;
        this.keys = keys;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        int rows = pattern.length;
        if (rows == 0) return true;
        int cols = pattern[0].length();

        int centerRow = rows / 2;
        int centerCol = cols / 2;

        for (int r = 0; r < rows; r++) {
            String rowStr = pattern[r];
            for (int c = 0; c < rowStr.length(); c++) {
                char ch = rowStr.charAt(c);
                if (ch == ' ' || !keys.containsKey(ch)) continue;

                int dx = c - centerCol;
                int dz = r - centerRow;

                ConditionContext offsetContext = new ConditionContext(
                    context.getWorld(),
                    context.getX() + dx,
                    context.getY(),
                    context.getZ() + dz,
                    context.getRecipeContext());

                if (!keys.get(ch)
                    .isMet(offsetContext)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "pattern(" + pattern.length + "x" + (pattern.length > 0 ? pattern[0].length() : 0) + ")";
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "pattern");
        JsonArray patternArray = new JsonArray();
        for (String s : pattern) patternArray.add(new JsonPrimitive(s));
        json.add("pattern", patternArray);

        JsonObject keysJson = new JsonObject();
        for (Map.Entry<Character, ICondition> entry : keys.entrySet()) {
            JsonObject condJson = new JsonObject();
            entry.getValue()
                .write(condJson);
            keysJson.add(String.valueOf(entry.getKey()), condJson);
        }
        json.add("keys", keysJson);
    }

    public static ICondition fromJson(JsonObject json) {
        List<String> patternList = new ArrayList<>();
        if (json.has("pattern")) {
            JsonArray array = json.getAsJsonArray("pattern");
            for (JsonElement e : array) patternList.add(e.getAsString());
        }
        String[] pattern = patternList.toArray(new String[0]);

        Map<Character, ICondition> keys = new HashMap<>();
        if (json.has("keys")) {
            JsonObject keysObj = json.getAsJsonObject("keys");
            for (Map.Entry<String, JsonElement> entry : keysObj.entrySet()) {
                if (entry.getKey()
                    .length() > 0) {
                    char ch = entry.getKey()
                        .charAt(0);
                    ICondition cond = ConditionParserRegistry.parse(
                        entry.getValue()
                            .getAsJsonObject());
                    keys.put(ch, cond);
                }
            }
        }

        return new BiomePatternCondition(pattern, keys);
    }
}
