package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;

public class AttributeSpeed implements IModifierAttribute {

    private float modificationFactor;

    public AttributeSpeed() {
        this(1.0F);
    }

    public AttributeSpeed(float modificationFactor) {
        this.modificationFactor = modificationFactor;
    }

    public String getAttributeName() {
        return "speed";
    }

    public float getMultiplier(float totalModificationFactor) {
        float base = QuantumExtractorConfig.modifiers.speedMultiplier;
        float minMultiplier = QuantumExtractorConfig.modifiers.speedMinMultiplier;
        float result = (float) Math.pow(base, (double) totalModificationFactor);
        return Math.max(minMultiplier, result);
    }

    public float getModificationFactor() {
        return this.modificationFactor;
    }
}
