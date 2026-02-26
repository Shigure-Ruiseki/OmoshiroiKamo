package ruiseki.omoshiroikamo.module.ids.common.capabilities;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.block.IDynamicRedstone;
import ruiseki.omoshiroikamo.core.datastructure.EnumFacingMap;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.TECable;

public class SidedDynamicRedstone implements IDynamicRedstone {

    public final TECable tile;
    public final ForgeDirection side;

    public SidedDynamicRedstone(TECable tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    protected EnumFacingMap<Integer> getRedstoneLevels() {
        return tile.getRedstoneLevels();
    }

    protected EnumFacingMap<Boolean> getRedstoneInputs() {
        return tile.getRedstoneInputs();
    }

    protected EnumFacingMap<Boolean> getRedstoneStrong() {
        return tile.getRedstoneStrong();
    }

    @Override
    public void setRedstoneLevel(int level, boolean strongPower) {
        if (!tile.getWorld().isRemote) {
            EnumFacingMap<Integer> redstoneLevels = getRedstoneLevels();
            EnumFacingMap<Boolean> redstoneStrongs = getRedstoneStrong();
            boolean sendUpdate = false;
            if (redstoneLevels.containsKey(side)) {
                if (redstoneLevels.get(side) != level) {
                    sendUpdate = true;
                    redstoneLevels.put(side, level);
                }
            } else {
                sendUpdate = true;
                redstoneLevels.put(side, level);
            }
            if (redstoneStrongs.containsKey(side)) {
                if (redstoneStrongs.get(side) != strongPower) {
                    sendUpdate = true;
                    redstoneStrongs.put(side, strongPower);
                }
            } else {
                sendUpdate = true;
                redstoneStrongs.put(side, strongPower);
            }
            if (sendUpdate) {
                tile.updateRedstoneInfo(side, strongPower);
            }
        }
    }

    @Override
    public int getRedstoneLevel() {
        EnumFacingMap<Integer> redstoneLevels = getRedstoneLevels();
        if (redstoneLevels.containsKey(side)) {
            return redstoneLevels.get(side);
        }
        return -1;
    }

    @Override
    public boolean isStrong() {
        EnumFacingMap<Boolean> redstoneStrongs = getRedstoneStrong();
        if (redstoneStrongs.containsKey(side)) {
            return redstoneStrongs.get(side);
        }
        return false;
    }

    @Override
    public void setAllowRedstoneInput(boolean allow) {
        EnumFacingMap<Boolean> redstoneInputs = getRedstoneInputs();
        redstoneInputs.put(side, allow);
    }

    @Override
    public boolean isAllowRedstoneInput() {
        EnumFacingMap<Boolean> redstoneInputs = getRedstoneInputs();
        if (redstoneInputs.containsKey(side)) {
            return redstoneInputs.get(side);
        }
        return false;
    }

    @Override
    public void setLastPulseValue(int value) {
        EnumFacingMap<Integer> pulses = tile.getLastRedstonePulses();
        pulses.put(side, value);
    }

    @Override
    public int getLastPulseValue() {
        EnumFacingMap<Integer> pulses = tile.getLastRedstonePulses();
        if (pulses.containsKey(side)) {
            return pulses.get(side);
        }
        return 0;
    }
}
