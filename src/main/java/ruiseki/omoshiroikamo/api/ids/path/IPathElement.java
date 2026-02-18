package ruiseki.omoshiroikamo.api.ids.path;

import java.util.Set;

import ruiseki.omoshiroikamo.api.datastructure.DimPos;

/**
 * An element that can be used to construct paths using the
 * {@link ruiseki.omoshiroikamo.module.ids.common.path.PathFinder}.
 * Multiple instances for the same 'element' can be created, so the comparator implementation must
 * make sure that these instances are considered equal.
 * These instances are used as a simple way of referring to these elements.
 *
 * @author rubensworks
 */
public interface IPathElement<E extends IPathElement<E>> extends Comparable<E> {

    /**
     * @return The position of this element.
     */
    public DimPos getPosition();

    /**
     * @return The set of all path elements that can be reached from here.
     */
    public Set<E> getReachableElements();

}
