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

public class BlockModifierAccuracy extends BlockModifier {

    protected BlockModifierAccuracy() {
        super(ModObject.blockModifierAccuracy.unlocalisedName, "accuracy");
        setTextureName("multiblock/modifier_accuracy");
    }

    public static BlockModifierAccuracy create() {
        return new BlockModifierAccuracy();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        float energyCost = QuantumExtractorConfig.modifiers.accuracyEnergyCost;
        list.add(ModifierAttribute.ACCURACY.getAttribute());
        list.add(new AttributeEnergyCost(energyCost));
        list.add(new AttributeEnergyCostFixed(16));
        list.add(new AttributeSpeedPenalty());
    }

    @Override
    public List<String> getTooltipLines() {
        float accuracyMultiplier = QuantumExtractorConfig.modifiers.accuracyMultiplier;
        float energyCost = QuantumExtractorConfig.modifiers.accuracyEnergyCost;
        float speedPenalty = QuantumExtractorConfig.modifiers.accuracySpeedPenalty;

        List<String> list = new ArrayList<>();
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.accuracy.effect"), accuracyMultiplier));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.accuracy.penalty"), speedPenalty, energyCost));
        return list;
    }
}
