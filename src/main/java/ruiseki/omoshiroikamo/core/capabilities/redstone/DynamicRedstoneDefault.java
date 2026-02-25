package ruiseki.omoshiroikamo.core.capabilities.redstone;

import ruiseki.omoshiroikamo.core.block.IDynamicRedstone;

/**
 * Default implementation of {@link IDynamicRedstone}.
 *
 * @author rubensworks
 */
public class DynamicRedstoneDefault implements IDynamicRedstone {

    private int redstoneLevel = 0;
    private boolean strongPower = false;
    private boolean allowRedstoneInput = true;
    private int lastPulseValue = 0;

    @Override
    public void setRedstoneLevel(int level, boolean strongPower) {
        this.redstoneLevel = level;
        this.strongPower = strongPower;
    }

    @Override
    public int getRedstoneLevel() {
        return redstoneLevel;
    }

    @Override
    public boolean isStrong() {
        return strongPower;
    }

    @Override
    public void setAllowRedstoneInput(boolean allow) {
        this.allowRedstoneInput = allow;
    }

    @Override
    public boolean isAllowRedstoneInput() {
        return allowRedstoneInput;
    }

    @Override
    public void setLastPulseValue(int value) {
        this.lastPulseValue = value;
    }

    @Override
    public int getLastPulseValue() {
        return lastPulseValue;
    }
}
