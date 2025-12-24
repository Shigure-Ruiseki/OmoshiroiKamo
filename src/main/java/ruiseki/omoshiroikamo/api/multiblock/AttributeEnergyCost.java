package ruiseki.omoshiroikamo.api.multiblock;

public class AttributeEnergyCost implements IModifierAttribute {

    private float factor;

    public AttributeEnergyCost() {
        this(1.0F);
    }

    public AttributeEnergyCost(float multiplier) {
        // Store the natural log of the multiplier
        // This allows totalFactor to be the sum of logs, so exp(total) is the product
        this.factor = (float) Math.log(multiplier);
    }

    public String getAttributeName() {
        return "energycost";
    }

    public float getModificationFactor() {
        return this.factor;
    }

    public float getMultiplier(float totalModificationFactor) {
        // exp(sum of logs) = product of multipliers
        return (float) Math.exp(totalModificationFactor);
    }
}
