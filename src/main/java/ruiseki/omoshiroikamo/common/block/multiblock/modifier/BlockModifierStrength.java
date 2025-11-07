package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierStrength extends BlockModifier {

    protected BlockModifierStrength() {
        super(ModObject.blockModifierStrength, "strength");
        setTextureName("modifier_strength");
    }

    public static BlockModifierStrength create() {
        return new BlockModifierStrength();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_STRENGTH.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
