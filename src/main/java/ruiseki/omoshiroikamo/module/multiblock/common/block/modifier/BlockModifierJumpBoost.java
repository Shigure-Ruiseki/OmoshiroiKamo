package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierJumpBoost extends BlockModifier {

    protected BlockModifierJumpBoost() {
        super(ModObject.blockModifierJumpBoost.unlocalisedName, "jump_boost");
        setTextureName("multiblock/modifier_jump_boost");
    }

    public static BlockModifierJumpBoost create() {
        return new BlockModifierJumpBoost();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.jumpBoost;
        list.add(ModifierAttribute.P_JUMP_BOOST.getAttribute());
        list.add(new AttributeEnergyCostFixed(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        int energyCost = QuantumBeaconConfig.modifierEnergyCost.jumpBoost;
        List<String> list = new ArrayList<>();
        list.add(LibMisc.LANG.localize("tooltip.modifier.jump_boost.effect"));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.beacon.energy"), energyCost));
        return list;
    }
}
