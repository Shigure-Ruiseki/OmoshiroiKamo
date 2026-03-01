package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.visitor.IStructureVisitor;

/**
 * Standard implementation of IStructureEntry.
 */
public class StructureEntry implements IStructureEntry {

    private final String name;
    private final String displayName;
    private final List<IStructureLayer> layers;
    private final Map<Character, ISymbolMapping> mappings;
    private final List<IStructureRequirement> requirements;
    private final List<String> recipeGroup;
    private final int[] controllerOffset;
    private final String tintColor;
    private final int tier;
    private final String defaultFacing;

    public StructureEntry(String name, String displayName, List<IStructureLayer> layers,
        Map<Character, ISymbolMapping> mappings, List<IStructureRequirement> requirements, List<String> recipeGroup,
        int[] controllerOffset, String tintColor, int tier, String defaultFacing) {
        this.name = name;
        this.displayName = displayName;
        this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
        this.mappings = Collections.unmodifiableMap(new LinkedHashMap<>(mappings));
        this.requirements = Collections.unmodifiableList(new ArrayList<>(requirements));
        this.recipeGroup = recipeGroup != null ? Collections.unmodifiableList(new ArrayList<>(recipeGroup))
            : Collections.emptyList();
        this.controllerOffset = controllerOffset != null ? controllerOffset.clone() : null;
        this.tintColor = tintColor;
        this.tier = tier;
        this.defaultFacing = defaultFacing;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<IStructureLayer> getLayers() {
        return layers;
    }

    @Override
    public Map<Character, ISymbolMapping> getMappings() {
        return mappings;
    }

    @Override
    public List<IStructureRequirement> getRequirements() {
        return requirements;
    }

    @Override
    public List<String> getRecipeGroup() {
        return recipeGroup;
    }

    @Override
    public int[] getControllerOffset() {
        return controllerOffset != null ? controllerOffset.clone() : null;
    }

    @Override
    public String getTintColor() {
        return tintColor;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getDefaultFacing() {
        return defaultFacing;
    }

    @Override
    public void accept(IStructureVisitor visitor) {
        visitor.visit(this);
        for (IStructureRequirement req : requirements) {
            visitor.visit(req);
        }
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        if (displayName != null) json.addProperty("displayName", displayName);
        if (!recipeGroup.isEmpty()) {
            JsonArray groupArray = new JsonArray();
            for (String g : recipeGroup) {
                groupArray.add(new JsonPrimitive(g));
            }
            json.add("recipeGroup", groupArray);
        }

        if (controllerOffset != null && controllerOffset.length >= 3) {
            JsonArray offsetArray = new JsonArray();
            offsetArray.add(new JsonPrimitive(controllerOffset[0]));
            offsetArray.add(new JsonPrimitive(controllerOffset[1]));
            offsetArray.add(new JsonPrimitive(controllerOffset[2]));
            json.add("controllerOffset", offsetArray);
        }

        if (tintColor != null) {
            json.addProperty("tintColor", tintColor);
        }

        if (tier != 1) {
            json.addProperty("tier", tier);
        }

        JsonArray layersArray = new JsonArray();
        for (IStructureLayer layer : layers) {
            layersArray.add(layer.serialize());
        }
        json.add("layers", layersArray);

        JsonObject mappingsObj = new JsonObject();
        for (Map.Entry<Character, ISymbolMapping> entry : mappings.entrySet()) {
            mappingsObj.add(
                String.valueOf(entry.getKey()),
                entry.getValue()
                    .serialize());
        }
        json.add("mappings", mappingsObj);

        if (!requirements.isEmpty()) {
            JsonArray reqsArray = new JsonArray();
            for (IStructureRequirement req : requirements) {
                reqsArray.add(req.serialize());
            }
            json.add("requirements", reqsArray);
        }

        if (defaultFacing != null) {
            json.addProperty("defaultFacing", defaultFacing);
        }

        return json;
    }
}
