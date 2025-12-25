package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.init.ModifierAttribute;

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
        float energyCost = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierEnergyCost;
        float speedPenalty = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierSpeedPenalty;
        // Convert 0.9 (desired speedPenalty) to AttributeSpeed factor: ln(0.9) /
        // ln(0.7) ≈ -0.295
        float speedFactor = (float) (Math.log(speedPenalty) / Math.log(0.7));

        list.add(ModifierAttribute.ACCURACY.getAttribute());
        list.add(new AttributeEnergyCost(energyCost));
        list.add(new AttributeSpeed(speedFactor));
    }

    @Override
    public List<String> getTooltipLines() {
        float accuracyMultiplier = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierMultiplier;
        float energyCost = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierEnergyCost;
        float speedPenalty = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierSpeedPenalty;

        List<String> list = new ArrayList<>();
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.accuracy.effect"), accuracyMultiplier));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.accuracy.penalty"), speedPenalty, energyCost));
        return list;
    }
}
