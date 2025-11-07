package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierSaturation extends BlockModifier {

    protected BlockModifierSaturation() {
        super(ModObject.blockModifierSaturation, "saturation");
        setTextureName("modifier_saturation");
    }

    public static BlockModifierSaturation create() {
        return new BlockModifierSaturation();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_SATURATION.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
