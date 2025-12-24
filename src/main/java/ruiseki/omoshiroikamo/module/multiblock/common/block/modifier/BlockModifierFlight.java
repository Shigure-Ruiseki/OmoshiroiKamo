package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierFlight extends BlockModifier {

    protected BlockModifierFlight() {
        super(ModObject.blockModifierFlight.unlocalisedName, "flight");
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

    @Override
    public List<String> getTooltipLines() {
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.flight.effect"));
        list.add(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"));
        return list;
    }
}
