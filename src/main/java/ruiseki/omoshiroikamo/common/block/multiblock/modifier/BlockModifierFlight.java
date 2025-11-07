package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierFlight extends BlockModifier {

    protected BlockModifierFlight() {
        super(ModObject.blockModifierFlight, "flight");
        setTextureName("modifier_flight");
    }

    public static BlockModifierFlight create() {
        return new BlockModifierFlight();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.E_FLIGHT_CREATIVE.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
