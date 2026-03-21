package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Standard implementation of ISymbolMapping for blocks.
 * Supports single block ID or multiple block IDs (chain).
 */
public class BlockMapping implements ISymbolMapping {

    private final char symbol;
    private final String blockId;
    private final List<String> blockIds;
    private int portIndex = -1;

    public BlockMapping(char symbol, String blockId) {
        this.symbol = symbol;
        this.blockId = blockId;
        this.blockIds = null;
    }

    public BlockMapping(char symbol, List<String> blockIds) {
        this.symbol = symbol;
        this.blockId = null;
        this.blockIds = Collections.unmodifiableList(new ArrayList<>(blockIds));
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public int getPortIndex() {
        return portIndex;
    }

    public void setPortIndex(int index) {
        this.portIndex = index;
    }

    public String getBlockId() {
        return blockId;
    }

    public List<String> getBlockIds() {
        return blockIds;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        if (blockId != null) {
            json.addProperty("block", blockId);
        } else if (blockIds != null) {
            JsonArray array = new JsonArray();
            for (String id : blockIds) {
                array.add(new com.google.gson.JsonPrimitive(id));
            }
            json.add("blocks", array);
        }
        if (portIndex >= 0) {
            json.addProperty("index", portIndex);
        }
        return json;
    }
}
