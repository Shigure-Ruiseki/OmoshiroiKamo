package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.api.multiblock.IModifierBlock;
import ruiseki.omoshiroikamo.api.multiblock.ModifierRegistry;
import ruiseki.omoshiroikamo.common.block.BlockOK;

public abstract class BlockModifier extends BlockOK implements IModifierBlock, IMBBlock {

    private final String modifierName;
    private final List<IModifierAttribute> attributes;

    protected BlockModifier(ModObject modObject, String modifierName) {
        super(modObject);
        this.modifierName = modifierName;
        ModifierRegistry.getInstance()
            .registerModifier(this);
        this.attributes = new ArrayList<>();
        this.addAttributes(this.attributes);
    }

    public abstract void addAttributes(List<IModifierAttribute> var1);

    @Override
    public String getModifierName() {
        return this.modifierName;
    }

    @Override
    public List<IModifierAttribute> getAttributes() {
        return this.attributes;
    }
}
