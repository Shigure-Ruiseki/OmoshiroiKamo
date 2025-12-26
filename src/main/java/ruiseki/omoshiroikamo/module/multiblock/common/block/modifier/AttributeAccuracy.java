package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;

public class AttributeAccuracy implements IModifierAttribute {

    private float factor;

    public AttributeAccuracy() {
        this(1.0F);
    }

    public AttributeAccuracy(float factor) {
        this.factor = factor;
    }

    public String getAttributeName() {
        return "accuracy";
    }

    public float getModificationFactor() {
        return this.factor;
    }

    public float getMultiplier(float totalModificationFactor) {
        float base = QuantumExtractorConfig.modifiers.accuracyMultiplier;
        float maxMultiplier = QuantumExtractorConfig.modifiers.accuracyMaxMultiplier;
        float result = (float) Math.pow(base, (double) totalModificationFactor);
        return Math.min(maxMultiplier, result);
    }
}
