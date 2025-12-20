package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.api.multiblock.ModifierRegistry;
import ruiseki.omoshiroikamo.common.block.BlockOK;

public abstract class BlockModifier extends BlockOK implements IModifierBlock {

    private final String modifierName;
    private final List<IModifierAttribute> attributes;

    protected BlockModifier(String name, String modifierName) {
        super(name);
        this.modifierName = modifierName;
        ModifierRegistry.getInstance()
            .registerModifier(this);
        this.attributes = new ArrayList<>();
        this.addAttributes(this.attributes);
    }

    public abstract void addAttributes(List<IModifierAttribute> var1);

    /**
     * Returns tooltip lines for this modifier block.
     * Override in subclasses to provide custom tooltips.
     * 
     * @return List of tooltip strings (may include formatting codes)
     */
    public List<String> getTooltipLines() {
        return new ArrayList<>();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockModifier.class, name);
    }

    @Override
    public String getModifierName() {
        return this.modifierName;
    }

    @Override
    public List<IModifierAttribute> getAttributes() {
        return this.attributes;
    }
}
