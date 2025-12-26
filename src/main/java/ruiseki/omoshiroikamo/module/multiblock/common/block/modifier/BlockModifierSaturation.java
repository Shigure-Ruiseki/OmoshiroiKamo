package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierSaturation extends BlockModifier {

    protected BlockModifierSaturation() {
        super(ModObject.blockModifierSaturation.unlocalisedName, "saturation");
        setTextureName("modifier_saturation");
    }

    public static BlockModifierSaturation create() {
        return new BlockModifierSaturation();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.saturation;
        list.add(ModifierAttribute.P_SATURATION.getAttribute());
        list.add(new AttributeEnergyCostFixed(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.saturation;
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.saturation.effect"));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"), energyCost));
        return list;
    }
}
