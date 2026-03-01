package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Standard implementation of IStructureLayer.
 */
public class StructureLayer implements IStructureLayer {

    private final String name;
    private final List<String> rows;

    public StructureLayer(String name, List<String> rows) {
        this.name = name;
        this.rows = new ArrayList<>(rows);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getRows() {
        return rows;
    }

    @Override
    public String[][] toStructureLibRows() {
        if (rows.isEmpty()) return new String[0][0];

        int height = rows.size();
        // int width = rows.get(0).length(); // unused

        String[][] array = new String[height][];
        for (int i = 0; i < height; i++) {
            array[i] = new String[] { rows.get(i) };
        }
        return array;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        if (name != null && !name.isEmpty()) {
            json.addProperty("name", name);
        }
        JsonArray rowsArray = new JsonArray();
        for (String row : rows) {
            rowsArray.add(new JsonPrimitive(row));
        }
        json.add("rows", rowsArray);
        return json;
    }
}
