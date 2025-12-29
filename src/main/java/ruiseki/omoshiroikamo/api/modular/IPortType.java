package ruiseki.omoshiroikamo.api.modular;

public interface IPortType {

    enum Type {
        ITEM,
        FLUID,
        ENERGY,
        MANA,
        NONE
    }

    enum Direction {
        INPUT,
        OUTPUT,
        BOTH,
        NONE
    }

    Type getPortType();

    Direction getPortDirection();
}
