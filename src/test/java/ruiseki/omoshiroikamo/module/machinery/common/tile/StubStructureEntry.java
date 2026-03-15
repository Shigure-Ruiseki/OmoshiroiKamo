package ruiseki.omoshiroikamo.module.machinery.common.tile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.io.IStructureRequirement;
import ruiseki.omoshiroikamo.api.structure.visitor.IStructureVisitor;

public class StubStructureEntry implements IStructureEntry {

    private final String name;
    private final Map<Character, ISymbolMapping> mappings;

    public StubStructureEntry(String name, Map<Character, ISymbolMapping> mappings) {
        this.name = name;
        this.mappings = mappings;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Character, ISymbolMapping> getMappings() {
        return mappings;
    }

    @Override
    public List<IStructureLayer> getLayers() {
        return Collections.emptyList();
    }

    @Override
    public int[] getControllerOffset() {
        return new int[] { 0, 0, 0 };
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public List<String> getRecipeGroup() {
        return Collections.singletonList("default");
    }

    @Override
    public String getTintColor() {
        return null;
    }

    @Override
    public Set<Character> getExternalPorts() {
        return Collections.emptySet();
    }

    @Override
    public Map<Character, EnumIO> getFixedExternalPorts() {
        return Collections.emptyMap();
    }

    @Override
    public List<IStructureRequirement> getRequirements() {
        return Collections.emptyList();
    }

    @Override
    public float getSpeedMultiplier() {
        return 1.0f;
    }

    @Override
    public float getEnergyMultiplier() {
        return 1.0f;
    }

    @Override
    public int getBatchMin() {
        return 1;
    }

    @Override
    public int getBatchMax() {
        return 1;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public String getDefaultFacing() {
        return "SOUTH";
    }

    @Override
    public List<String> getComponentNames() {
        return Collections.emptyList();
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        return json;
    }

    @Override
    public void accept(IStructureVisitor visitor) {}
}
