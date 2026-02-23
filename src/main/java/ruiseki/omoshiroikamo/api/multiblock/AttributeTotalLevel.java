package ruiseki.omoshiroikamo.api.multiblock;

public class AttributeTotalLevel implements IModifierAttribute {

    private final int factor;
    private final String attributeName;

    public AttributeTotalLevel(String attributeName) {
        this(attributeName, 1);
    }

    public AttributeTotalLevel(String attributeName, int level) {
        this.attributeName = attributeName;
        this.factor = level;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    @Override
    public float getModificationFactor() {
        return this.factor;
    }

    public float getMultiplier(float totalModificationFactor) {
        return totalModificationFactor;
    }
}
