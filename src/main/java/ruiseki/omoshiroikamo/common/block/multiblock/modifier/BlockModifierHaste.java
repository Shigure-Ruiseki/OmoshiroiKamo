package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierHaste extends BlockModifier {

    protected BlockModifierHaste() {
        super(ModObject.blockModifierHaste, "haste");
        setTextureName("modifier_haste");
    }

    public static BlockModifierHaste create() {
        return new BlockModifierHaste();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_HASTE.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
