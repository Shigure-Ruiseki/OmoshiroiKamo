package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierHaste extends BlockModifier {

    protected BlockModifierHaste() {
        super(ModObject.blockModifierHaste.unlocalisedName, "haste");
        setTextureName("modifier_haste");
    }

    public static BlockModifierHaste create() {
        return new BlockModifierHaste();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_HASTE.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(StatCollector.translateToLocal("tooltip.modifier.haste.effect"));
        list.add(StatCollector.translateToLocal("tooltip.modifier.beacon.energy"));
        return list;
    }
}
