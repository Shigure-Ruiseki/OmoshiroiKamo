package ruiseki.omoshiroikamo.module.ids.common.part;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.datastructure.DimPos;

/**
 * A interface for containers that can hold {@link IPart}s.
 * 
 * @author rubensworks
 */
public interface IPartContainer {

    /**
     * @return The position this container is at.
     */
    public DimPos getPosition();

    /**
     * @return The parts inside this container.
     */
    public Map<ForgeDirection, IPart<?, ?>> getParts();

    /**
     * Set the part for a side.
     * 
     * @param side The side to place the part on.
     * @param part The part.
     * @param <P>  The type of part.
     * @param <S>  The type of part state.
     */
    public <P extends IPart<P, S>, S extends IPartState<P>> void setPart(ForgeDirection side, IPart<P, S> part);

    /**
     * Get the part of a side, can be null.
     * 
     * @param side The side.
     * @return The part or null.
     */
    public IPart getPart(ForgeDirection side);

    /**
     * @param side The side.
     * @return If the given side has a part.
     */
    public boolean hasPart(ForgeDirection side);

    /**
     * Remove the part from a side, can return null if there was no part on that side.
     * 
     * @param side The side.
     * @return The removed part or null.
     */
    public IPart removePart(ForgeDirection side);

    /**
     * Set the state of a part.
     * 
     * @param side      The side.
     * @param partState The part state.
     */
    public void setPartState(ForgeDirection side, IPartState partState);

    /**
     * Get the state of a part.
     * 
     * @param side The side.
     * @return The part state.
     */
    public IPartState getPartState(ForgeDirection side);

}
