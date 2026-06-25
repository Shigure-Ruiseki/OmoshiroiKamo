package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.okcore.helper.LangHelpers;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierPiezo extends BlockModifier {

    protected BlockModifierPiezo() {
        super(ModObject.MODIFIER_PIEZO.name, "piezo");
        setTextureName(Reference.PREFIX_MOD + "multiblock/modifier_piezo");
    }

    public static BlockModifierPiezo create() {
        return new BlockModifierPiezo();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.PIEZO.getAttribute());
    }

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(LangHelpers.localize("tooltip.modifier.piezo.effect"));
        return list;
    }
}
