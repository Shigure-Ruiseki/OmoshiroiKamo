package ruiseki.omoshiroikamo.api.io;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.block.IOKTile;

public interface ISidedIO extends IOKTile {

    enum IO {

        NONE,
        INPUT,
        OUTPUT,
        BOTH;

        public boolean canInput() {
            return this == INPUT || this == BOTH;
        }

        public boolean canOutput() {
            return this == OUTPUT || this == BOTH;
        }
    }

    default IO getIOLimit() {
        return IO.BOTH;
    }

    IO getSideIO(ForgeDirection side);

    void setSideIO(ForgeDirection side, IO state);

    default void toggleSide(ForgeDirection side) {
        IO limit = getIOLimit();
        IO current = getSideIO(side);

        IO next;
        switch (current) {
            case NONE:
                next = limit.canInput() ? IO.INPUT : limit.canOutput() ? IO.OUTPUT : IO.NONE;
                break;

            case INPUT:
                next = limit.canOutput() ? IO.OUTPUT : IO.NONE;
                break;

            case OUTPUT:
            default:
                next = IO.NONE;
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
