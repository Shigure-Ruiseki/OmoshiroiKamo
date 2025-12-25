package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;

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
        float base = EnvironmentalConfig.quantumExtractorConfig.speedModifierMultiplier;
        return (float) Math.pow(base, (double) totalModificationFactor);
    }

    public float getModificationFactor() {
        return this.modificationFactor;
    }
}
