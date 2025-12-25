package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

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
        float base = EnvironmentalConfig.quantumExtractorConfig.accuracyModifierMultiplier;
        return (float) Math.pow(base, (double) totalModificationFactor);
    }
}
