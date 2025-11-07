package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierNightVision extends BlockModifier {

    protected BlockModifierNightVision() {
        super(ModObject.blockModifierNightVision, "night_vision");
        setTextureName("modifier_night_vision");
    }

    public static BlockModifierNightVision create() {
        return new BlockModifierNightVision();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_NIGHT_VISION.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }
}
