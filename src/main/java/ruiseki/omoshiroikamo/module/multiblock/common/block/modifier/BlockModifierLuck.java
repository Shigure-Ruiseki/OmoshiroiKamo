package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.multiblock.AttributeEnergyCost;
import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class BlockModifierLuck extends BlockModifier {

    protected BlockModifierLuck() {
        super("modifierLuck", "luck");
        setTextureName("modifier_luck");
    }

    public static BlockModifierLuck create() {
        return new BlockModifierLuck();
    }

    @Override
    public void addAttributes(List<IModifierAttribute> list) {
        float bonusChance = EnvironmentalConfig.quantumExtractorConfig.luckModifierBonusChance;
        float energyCost = EnvironmentalConfig.quantumExtractorConfig.luckModifierEnergyCost;
        list.add(new AttributeLuck(bonusChance));
        list.add(new AttributeEnergyCost(energyCost));
    }

    @Override
    public List<String> getTooltipLines() {
        float bonusChance = EnvironmentalConfig.quantumExtractorConfig.luckModifierBonusChance;
        float energyCost = EnvironmentalConfig.quantumExtractorConfig.luckModifierEnergyCost;

        List<String> list = new ArrayList<>();
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.luck.effect"), (int) (bonusChance * 100)));
        list.add(String.format(LibMisc.LANG.localize("tooltip.modifier.luck.penalty"), energyCost));
        return list;
    }
}
