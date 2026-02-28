package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;

/**
 * Builder for IStructureEntry (StructureEntry).
 */
public class StructureEntryBuilder {

    private String name;
    private String displayName;
    private final List<IStructureLayer> layers = new ArrayList<>();
    private final Map<Character, ISymbolMapping> mappings = new LinkedHashMap<>();
    private final List<IStructureRequirement> requirements = new ArrayList<>();
    private final List<String> recipeGroups = new ArrayList<>();
    private int[] controllerOffset;
    private String tintColor;
    private int tier;
    private String defaultFacing;

    public StructureEntryBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StructureEntryBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public StructureEntryBuilder addLayer(IStructureLayer layer) {
        this.layers.add(layer);
        return this;
    }

    public StructureEntryBuilder addMapping(char symbol, ISymbolMapping mapping) {
        this.mappings.put(symbol, mapping);
        return this;
    }

    public StructureEntryBuilder addRequirement(IStructureRequirement requirement) {
        this.requirements.add(requirement);
        return this;
    }

    public StructureEntryBuilder addRecipeGroup(String group) {
        this.recipeGroups.add(group);
        return this;
    }

    public StructureEntryBuilder setControllerOffset(int[] offset) {
        this.controllerOffset = offset != null ? offset.clone() : null;
        return this;
    }

    public StructureEntryBuilder setTintColor(String tintColor) {
        this.tintColor = tintColor;
        return this;
    }

    public StructureEntryBuilder setTier(int tier) {
        this.tier = tier;
        return this;
    }

    public StructureEntryBuilder setDefaultFacing(String defaultFacing) {
        this.defaultFacing = defaultFacing;
        return this;
    }

    public IStructureEntry build() {
        if (name == null) {
            throw new IllegalStateException("Structure name must be set");
        }
        return new StructureEntry(
            name,
            displayName,
            layers,
            mappings,
            requirements,
            recipeGroups,
            controllerOffset,
            tintColor,
            tier,
            defaultFacing);
    }
}
