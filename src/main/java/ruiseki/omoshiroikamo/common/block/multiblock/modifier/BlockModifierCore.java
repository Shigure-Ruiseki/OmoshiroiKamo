package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;

public class BlockModifierCore extends BlockModifier {

    protected BlockModifierCore() {
        super(ModObject.blockModifierNull, "");
        setTextureName("modifier_core");
    }

    public static BlockModifierCore create() {
        return new BlockModifierCore();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {}
}
