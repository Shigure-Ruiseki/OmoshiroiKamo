package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCostFixed;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

public class BlockModifierSpeed extends BlockModifier {

    protected BlockModifierSpeed() {
        super(ModObject.blockModifierSpeed.unlocalisedName, "speed");
        setTextureName("modifier_speed");
    }

    public static BlockModifierSpeed create() {
        return new BlockModifierSpeed();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        float energyCost = QuantumExtractorConfig.modifiers.speedEnergyCost;
        list.add(new AttributeEnergyCost(energyCost));
        list.add(ModifierAttribute.SPEED.getAttribute());
        list.add(ModifierAttribute.P_SPEED.getAttribute());
        list.add(new AttributeEnergyCostFixed(16));
    }

    @Override
    public List<String> getTooltipLines() {
        float speedMultiplier = QuantumExtractorConfig.modifiers.speedMultiplier;
        float energyCost = QuantumExtractorConfig.modifiers.speedEnergyCost;

        List<String> list = new ArrayList<>();
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.speed.effect"), speedMultiplier));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.speed.penalty"), energyCost));
        return list;
    }
}
