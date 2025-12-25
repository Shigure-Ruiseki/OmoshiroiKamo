package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class BlockModifierLuck extends BlockModifier {

    protected BlockModifierLuck() {
        super("modifierLuck", "luck");
        setTextureName("modifier_luck");
    }

    public static BlockModifierLuck create() {
        return new BlockModifierLuck();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(new AttributeLuck(0.1F)); // 10% double output chance
        list.add(new AttributeEnergyCost(1.25F)); // 1.25x energy cost
    }

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.luck.effect"));
        list.add(LibMisc.LANG.localize("tooltip.modifier.luck.penalty"));
        return list;
    }
}
