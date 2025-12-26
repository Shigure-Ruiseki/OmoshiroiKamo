package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.muliblock.QuantumExtractorConfig;

/**
 * Luck attribute for Miner modifiers.
 * Provides a chance for bonus output (additive: configurable % per modifier).
 */
public class AttributeLuck implements IModifierAttribute {

    private float modificationFactor;

    public AttributeLuck() {
        this(QuantumExtractorConfig.luckModifierBonusChance);
    }

    public AttributeLuck(float modificationFactor) {
        this.modificationFactor = modificationFactor;
    }

    @Override
    public String getAttributeName() {
        return "luck";
    }

    @Override
    public float getModificationFactor() {
        return this.modificationFactor;
    }

    @Override
    public float getMultiplier(float totalModificationFactor) {
        // Additive: total factor is directly returned, clamped to max
        float maxBonus = QuantumExtractorConfig.luckModifierMaxBonus;
        return Math.min(maxBonus, totalModificationFactor);
    }
}
