package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierFireResistance extends BlockModifier {

    protected BlockModifierFireResistance() {
        super(ModObject.blockModifierFireResistance.unlocalisedName, "fire_resistance");
        setTextureName("multiblock/modifier_fire_resistance");
    }

    public static BlockModifierFireResistance create() {
        return new BlockModifierFireResistance();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.fireResistance;
        list.add(ModifierAttribute.P_FIRE_RESISTANCE.getAttribute());
        list.add(new AttributeEnergyCostFixed(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.fireResistance;
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.fire_resistance.effect"));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"), energyCost));
        return list;
    }
}
