package ruiseki.omoshiroikamo.core.capabilities.light;

import ruiseki.omoshiroikamo.core.block.IDynamicLight;

/**
 * Default implementation of {@link IDynamicLight}.
 *
 * @author rubensworks
 */
public class DynamicLightDefault implements IDynamicLight {

    private int light = 0;

    @Override
    public void setLightLevel(int level) {
        this.light = level;
    }

    @Override
    public int getLightLevel() {
        return light;
    }
}
