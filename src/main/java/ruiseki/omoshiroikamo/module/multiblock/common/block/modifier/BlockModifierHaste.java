package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierHaste extends BlockModifier {

    protected BlockModifierHaste() {
        super(ModObject.blockModifierHaste.unlocalisedName, "haste");
        setTextureName("multiblock/modifier_haste");
    }

    public static BlockModifierHaste create() {
        return new BlockModifierHaste();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.haste;
        list.add(ModifierAttribute.P_HASTE.getAttribute());
        list.add(new AttributeEnergyCostFixed(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.haste;
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.haste.effect"));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"), energyCost));
        return list;
    }
}
