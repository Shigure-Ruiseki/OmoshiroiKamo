package ruiseki.omoshiroikamo.api.block;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;

public interface ISidedIO extends IOKTile {

    default EnumIO getIOLimit() {
        return EnumIO.BOTH;
    }

    EnumIO getSideIO(ForgeDirection side);

    void setSideIO(ForgeDirection side, EnumIO state);

    default void toggleSide(ForgeDirection side) {
        EnumIO limit = getIOLimit();
        EnumIO current = getSideIO(side);

        EnumIO next;
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

        setSideIO(side, next);
    }

    default boolean canInput(ForgeDirection side) {
        return getSideIO(side).canInput();
    }

    default boolean canOutput(ForgeDirection side) {
        return getSideIO(side).canOutput();
    }
}
