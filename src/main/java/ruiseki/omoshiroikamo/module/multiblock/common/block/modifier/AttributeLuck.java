package ruiseki.omoshiroikamo.module.multiblock.common.block.modifier;

import ruiseki.omoshiroikamo.api.multiblock.IModifierAttribute;

/**
 * Luck attribute for Miner modifiers.
 * Provides a chance for double output (additive: +10% per modifier).
 */
public class AttributeLuck implements IModifierAttribute {

    private float modificationFactor;

    public AttributeLuck() {
        this(0.1F); // 10% per modifier
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
        // Additive: 1 modifier = 0.1 (10%), 2 modifiers = 0.2 (20%), etc.
        return totalModificationFactor;
    }
}
