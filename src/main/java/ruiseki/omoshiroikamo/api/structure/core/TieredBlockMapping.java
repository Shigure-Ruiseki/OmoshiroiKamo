package ruiseki.omoshiroikamo.api.structure.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Implementation of ISymbolMapping that associates a component name with
 * different block IDs for each Tier.
 */
public class TieredBlockMapping implements ISymbolMapping {

    private final char symbol;
    private final String componentName;
    private final Map<String, Integer> blockIdToTier;
    private final Map<Integer, String> tierToBlockId;
    private int portIndex = -1;

    public TieredBlockMapping(char symbol, String componentName, Map<String, Integer> tiers) {
        this.symbol = symbol;
        this.componentName = componentName;
        this.blockIdToTier = Collections.unmodifiableMap(new HashMap<>(tiers));

        Map<Integer, String> reverse = new HashMap<>();
        for (Map.Entry<String, Integer> entry : tiers.entrySet()) {
            reverse.put(entry.getValue(), entry.getKey());
        }
        this.tierToBlockId = Collections.unmodifiableMap(reverse);
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

    public String getComponentName() {
        return componentName;
    }

    /**
     * Get the Tier for a specific block ID.
     * Returns 0 if the block is not part of this tiered mapping.
     */
    public int getTier(String blockId) {
        // Handle metadata-less lookup if needed, but exact match is safer
        return blockIdToTier.getOrDefault(blockId, 0);
    }

    /**
     * Get the block ID for a specific Tier.
     */
    public String getBlockId(int tier) {
        return tierToBlockId.get(tier);
    }

    public Map<String, Integer> getTiers() {
        return blockIdToTier;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("component", componentName);
        JsonObject tiersObj = new JsonObject();
        for (Map.Entry<Integer, String> entry : tierToBlockId.entrySet()) {
            tiersObj.addProperty(entry.getValue(), entry.getKey());
        }
        json.add("tiers", tiersObj);
        if (portIndex >= 0) {
            json.addProperty("index", portIndex);
        }
        return json;
    }
}
