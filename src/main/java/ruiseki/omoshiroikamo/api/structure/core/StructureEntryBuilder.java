package ruiseki.omoshiroikamo.api.structure.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
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
    private final Set<Character> externalPorts = new LinkedHashSet<>();
    private final Map<Character, EnumIO> fixedExternalPorts = new LinkedHashMap<>();
    private int[] controllerOffset;
    private String tintColor;
    private IExpression speedMultiplierExpr = new ConstantExpression(1.0);
    private IExpression energyMultiplierExpr = new ConstantExpression(1.0);
    private IExpression batchMinExpr = new ConstantExpression(1.0);
    private IExpression batchMaxExpr = new ConstantExpression(1.0);
    private int tier = 0;
    private String defaultFacing;
    private final List<TierStructureRef> tierStructures = new ArrayList<>();

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

    public List<IStructureLayer> getLayers() {
        return layers;
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

    public StructureEntryBuilder addExternalPort(char symbol) {
        this.externalPorts.add(symbol);
        return this;
    }

    public StructureEntryBuilder addFixedExternalPort(char symbol, EnumIO io) {
        this.fixedExternalPorts.put(symbol, io);
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

    public StructureEntryBuilder setSpeedMultiplier(double value) {
        this.speedMultiplierExpr = new ConstantExpression(value);
        return this;
    }

    public StructureEntryBuilder setSpeedMultiplier(IExpression expr) {
        this.speedMultiplierExpr = expr;
        return this;
    }

    public StructureEntryBuilder setEnergyMultiplier(double value) {
        this.energyMultiplierExpr = new ConstantExpression(value);
        return this;
    }

    public StructureEntryBuilder setEnergyMultiplier(IExpression expr) {
        this.energyMultiplierExpr = expr;
        return this;
    }

    public StructureEntryBuilder setBatchMin(int value) {
        this.batchMinExpr = new ConstantExpression(value);
        return this;
    }

    public StructureEntryBuilder setBatchMin(IExpression expr) {
        this.batchMinExpr = expr;
        return this;
    }

    public StructureEntryBuilder setBatchMax(int value) {
        this.batchMaxExpr = new ConstantExpression(value);
        return this;
    }

    public StructureEntryBuilder setBatchMax(IExpression expr) {
        this.batchMaxExpr = expr;
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

    public StructureEntryBuilder addTierStructure(TierStructureRef ref) {
        this.tierStructures.add(ref);
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
            speedMultiplierExpr,
            energyMultiplierExpr,
            batchMinExpr,
            batchMaxExpr,
            tier,
            defaultFacing,
            externalPorts,
            fixedExternalPorts,
            tierStructures);
    }
}
