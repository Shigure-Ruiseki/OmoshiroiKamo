package ruiseki.omoshiroikamo.core.datastructure;

import java.util.EnumMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * An efficient implementation for mapping facings to any value.
 * 
 * @author rubensworks
 * @param <V> The value type.
 */
public class EnumFacingMap<V> extends EnumMap<ForgeDirection, V> {

    public EnumFacingMap() {
        super(ForgeDirection.class);
    }

    public EnumFacingMap(EnumMap<ForgeDirection, ? extends V> m) {
        super(m);
    }

    public EnumFacingMap(Map<ForgeDirection, ? extends V> m) {
        super(m);
    }

    /**
     * Make a new empty map.
     * 
     * @param <V> Value type.
     * @return The new map.
     */
    public static <V> EnumFacingMap<V> newMap() {
        return new EnumFacingMap<V>();
    }

    /**
     * Copy a map.
     * 
     * @param m   The existing map.
     * @param <V> Value type.
     * @return The new map.
     */
    public static <V> EnumFacingMap<V> newMap(EnumMap<ForgeDirection, ? extends V> m) {
        return new EnumFacingMap<V>(m);
    }

    /**
     * Copy a map.
     * 
     * @param m   The existing map.
     * @param <V> Value type.
     * @return The new map.
     */
    public static <V> EnumFacingMap<V> newMap(Map<ForgeDirection, ? extends V> m) {
        return new EnumFacingMap<V>(m);
    }

    /**
     * Make a new map for all the given facing values.
     * 
     * @param down  Down
     * @param up    Up
     * @param north North
     * @param south South
     * @param west  West
     * @param east  East
     * @param <V>   Value type.
     * @return The new map.
     */
    public static <V> EnumFacingMap<V> forAllValues(V down, V up, V north, V south, V west, V east) {
        EnumFacingMap<V> map = new EnumFacingMap<V>();
        map.put(ForgeDirection.DOWN, down);
        map.put(ForgeDirection.UP, up);
        map.put(ForgeDirection.NORTH, north);
        map.put(ForgeDirection.SOUTH, south);
        map.put(ForgeDirection.WEST, west);
        map.put(ForgeDirection.EAST, east);
        return map;
    }
}
