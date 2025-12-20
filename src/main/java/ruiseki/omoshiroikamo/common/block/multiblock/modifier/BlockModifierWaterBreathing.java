package ruiseki.omoshiroikamo.common.block.multiblock.modifier;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.common.init.ModifierAttribute;

public class BlockModifierWaterBreathing extends BlockModifier {

    protected BlockModifierWaterBreathing() {
        super(ModObject.blockModifierWaterBreathing.unlocalisedName, "water_breathing");
        setTextureName("modifier_water_breathing");
    }

    public static BlockModifierWaterBreathing create() {
        return new BlockModifierWaterBreathing();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        list.add(ModifierAttribute.P_WATER_BREATHING.getAttribute());
        list.add(new AttributeEnergyCostFixed(128));
    }

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(StatCollector.translateToLocal("tooltip.modifier.water_breathing.effect"));
        list.add(StatCollector.translateToLocal("tooltip.modifier.beacon.energy"));
        return list;
    }
}
