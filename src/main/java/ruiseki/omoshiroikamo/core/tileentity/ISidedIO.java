package ruiseki.omoshiroikamo.core.tileentity;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;

public interface ISidedIO extends ITile {

    default EnumIO getIOLimit() {
        return EnumIO.BOTH;
    }

    EnumIO getSideIO(ForgeDirection side);

    void setSideIO(ForgeDirection side, EnumIO state);

    default void toggleSide(ForgeDirection side) {
        toggleSide(side, false);
    }

    default void toggleSide(ForgeDirection side, boolean reverse) {
        EnumIO limit = getIOLimit();
        EnumIO current = getSideIO(side);

        EnumIO next;
        if (!reverse) {
            switch (current) {
                case NONE:
                    next = limit.canInput() ? EnumIO.INPUT : limit.canOutput() ? EnumIO.OUTPUT : EnumIO.NONE;
                    break;
                case INPUT:
                    next = limit.canOutput() ? EnumIO.OUTPUT : EnumIO.NONE;
                    break;
                case OUTPUT:
                default:
                    next = EnumIO.NONE;
                    break;
            }
        } else {
            switch (current) {
                case NONE:
                    next = limit.canOutput() ? EnumIO.OUTPUT : limit.canInput() ? EnumIO.INPUT : EnumIO.NONE;
                    break;
                case OUTPUT:
                    next = limit.canInput() ? EnumIO.INPUT : EnumIO.NONE;
                    break;
                case INPUT:
                default:
                    next = EnumIO.NONE;
                    break;
            }
        }

        setSideIO(side, next);
    }

    default boolean canInput(ForgeDirection side) {
        return getSideIO(side).canInput();
    }

    default boolean canOutput(ForgeDirection side) {
        return getSideIO(side).canOutput();
    }
}
