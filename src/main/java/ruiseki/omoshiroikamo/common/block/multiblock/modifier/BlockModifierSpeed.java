package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierSpeed extends BlockModifier {

    protected BlockModifierSpeed() {
        super(ModObject.blockModifierSpeed, "speed");
        setTextureName("modifier_speed");
    }

    public static BlockModifierSpeed create() {
        return new BlockModifierSpeed();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(new AttributeEnergyCost(1.0F));
        list.add(ModifierAttribute.SPEED.getAttribute());
        list.add(ModifierAttribute.P_SPEED.getAttribute());
        list.add(new AttributeEnergyCostFixed(16));
    }
}
