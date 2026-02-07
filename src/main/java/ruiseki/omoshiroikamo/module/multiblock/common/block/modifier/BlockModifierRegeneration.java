package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierRegeneration extends BlockModifier {

    protected BlockModifierRegeneration() {
        super(ModObject.blockModifierRegeneration.unlocalisedName, "regen");
        setTextureName("multiblock/modifier_regen");
    }

    public static BlockModifierRegeneration create() {
        return new BlockModifierRegeneration();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.regeneration;
        list.add(ModifierAttribute.P_REGEN.getAttribute());
        list.add(new AttributeEnergyCostFixed(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.regeneration;
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.regeneration.effect"));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"), energyCost));
        return list;
    }
}
