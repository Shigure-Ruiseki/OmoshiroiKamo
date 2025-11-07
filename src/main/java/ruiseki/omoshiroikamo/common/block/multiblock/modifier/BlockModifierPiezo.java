package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierPiezo extends BlockModifier {

    protected BlockModifierPiezo() {
        super(ModObject.blockModifierPiezo, "piezo");
        setTextureName("modifier_piezo");
    }

    public static BlockModifierPiezo create() {
        return new BlockModifierPiezo();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.PIEZO.getAttribute());
    }

}
