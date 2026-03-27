package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
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
    private final double speedMultiplier;
    private final double energyMultiplier;
    private final int batchMin;
    private final int batchMax;
    private final int tier;
    private final String defaultFacing;
    private final Set<Character> externalPorts;
    private final Map<Character, EnumIO> fixedExternalPorts;
    private final List<TierStructureRef> tierStructures;

    public StructureEntry(String name, String displayName, List<IStructureLayer> layers,
        Map<Character, ISymbolMapping> mappings, List<IStructureRequirement> requirements, List<String> recipeGroup,
        int[] controllerOffset, String tintColor, double speedMultiplier, double energyMultiplier, int batchMin,
        int batchMax, int tier, String defaultFacing, Set<Character> externalPorts,
        Map<Character, EnumIO> fixedExternalPorts, List<TierStructureRef> tierStructures) {
        this.name = name;
        this.displayName = displayName;
        this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
        this.mappings = Collections.unmodifiableMap(new LinkedHashMap<>(mappings));
        this.requirements = Collections.unmodifiableList(new ArrayList<>(requirements));
        this.recipeGroup = recipeGroup != null ? Collections.unmodifiableList(new ArrayList<>(recipeGroup))
            : Collections.emptyList();
        this.controllerOffset = controllerOffset != null ? controllerOffset.clone() : null;
        this.tintColor = tintColor;
        this.speedMultiplier = speedMultiplier;
        this.energyMultiplier = energyMultiplier;
        this.batchMin = batchMin;
        this.batchMax = batchMax;
        this.tier = tier;
        this.defaultFacing = defaultFacing;
        this.externalPorts = externalPorts != null ? Collections.unmodifiableSet(new LinkedHashSet<>(externalPorts))
            : Collections.emptySet();
        this.fixedExternalPorts = fixedExternalPorts != null
            ? Collections.unmodifiableMap(new LinkedHashMap<>(fixedExternalPorts))
            : Collections.emptyMap();
        this.tierStructures = tierStructures != null ? Collections.unmodifiableList(new ArrayList<>(tierStructures))
            : Collections.emptyList();
    }

    @Override
    public Set<Character> getExternalPorts() {
        if (fixedExternalPorts.isEmpty()) return externalPorts;
        Set<Character> combined = new LinkedHashSet<>(externalPorts);
        combined.addAll(fixedExternalPorts.keySet());
        return combined;
    }

    @Override
    public Map<Character, EnumIO> getFixedExternalPorts() {
        return fixedExternalPorts;
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
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    @Override
    public double getEnergyMultiplier() {
        return energyMultiplier;
    }

    @Override
    public int getBatchMin() {
        return batchMin;
    }

    @Override
    public int getBatchMax() {
        return batchMax;
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
    public List<String> getComponentNames() {
        Set<String> components = new LinkedHashSet<>();
        for (ISymbolMapping mapping : mappings.values()) {
            if (mapping instanceof TieredBlockMapping tiered) {
                components.add(tiered.getComponentName());
            }
        }
        return new ArrayList<>(components);
    }

    @Override
    public List<TierStructureRef> getTierStructures() {
        return tierStructures;
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
        // Skip serializing tierStructures for now as it's complex and mostly used for
        // loading
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
        if (speedMultiplier != 1.0) {
            json.addProperty("speedMultiplier", speedMultiplier);
        }
        if (energyMultiplier != 1.0) {
            json.addProperty("energyMultiplier", energyMultiplier);
        }
        if (batchMin != 1) {
            json.addProperty("batchMin", batchMin);
        }
        if (batchMax != 1) {
            json.addProperty("batchMax", batchMax);
        }

        if (tier != 0) {
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

        if (!externalPorts.isEmpty()) {
            JsonArray portsArray = new JsonArray();
            for (Character c : externalPorts) {
                if (!fixedExternalPorts.containsKey(c)) {
                    portsArray.add(new JsonPrimitive(String.valueOf(c)));
                }
            }
            if (portsArray.size() > 0) json.add("externalPorts", portsArray);
        }

        if (!fixedExternalPorts.isEmpty()) {
            JsonArray inputOnly = new JsonArray();
            JsonArray outputOnly = new JsonArray();
            JsonArray bothOnly = new JsonArray();

            for (Map.Entry<Character, EnumIO> entry : fixedExternalPorts.entrySet()) {
                JsonArray target = switch (entry.getValue()) {
                    case INPUT -> inputOnly;
                    case OUTPUT -> outputOnly;
                    case BOTH -> bothOnly;
                    default -> null;
                };
                if (target != null) target.add(new JsonPrimitive(String.valueOf(entry.getKey())));
            }

            if (inputOnly.size() > 0) json.add("inputOnly", inputOnly);
            if (outputOnly.size() > 0) json.add("outputOnly", outputOnly);
            if (bothOnly.size() > 0) json.add("bothOnly", bothOnly);
        }

        return json;
    }
}
