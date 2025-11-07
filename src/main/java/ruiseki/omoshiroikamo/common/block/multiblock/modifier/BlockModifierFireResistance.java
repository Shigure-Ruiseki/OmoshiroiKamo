package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierFireResistance extends BlockModifier {

    protected BlockModifierFireResistance() {
        super(ModObject.blockModifierFireResistance, "fire_resistance");
        setTextureName("modifier_fire_resistance");
    }

    public static BlockModifierFireResistance create() {
        return new BlockModifierFireResistance();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_FIRE_RESISTANCE.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
