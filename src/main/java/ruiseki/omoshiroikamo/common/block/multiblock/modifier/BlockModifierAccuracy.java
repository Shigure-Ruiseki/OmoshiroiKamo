package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierAccuracy extends BlockModifier {

    protected BlockModifierAccuracy() {
        super(ModObject.blockModifierAccuracy.unlocalisedName, "accuracy");
        setTextureName("modifier_accuracy");
    }

    public static BlockModifierAccuracy create() {
        return new BlockModifierAccuracy();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.ACCURACY.getAttribute());
        list.add(new AttributeEnergyCost(0.5F));
        list.add(new AttributeSpeed(-0.295F)); // 10% slower per modifier (0.9x speed)
    }

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(StatCollector.translateToLocal("tooltip.modifier.accuracy.effect"));
        list.add(StatCollector.translateToLocal("tooltip.modifier.accuracy.penalty"));
        return list;
    }
}
