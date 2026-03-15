package ruiseki.omoshiroikamo.api.modular;

public interface IPortType {

    enum Type {
        ITEM,
        FLUID,
        ENERGY,
        MANA,
        GAS,
        ESSENTIA,
        VIS,
        BLOCK,
        NONE
    }

    enum Direction {
        INPUT,
        OUTPUT,
        BOTH,
        NONE
    }

    Type[] SUPPORTED_TYPES = { Type.ITEM, Type.FLUID, Type.ENERGY, Type.MANA, Type.GAS, Type.ESSENTIA, Type.VIS };

    Type getPortType();

    Direction getPortDirection();
}
