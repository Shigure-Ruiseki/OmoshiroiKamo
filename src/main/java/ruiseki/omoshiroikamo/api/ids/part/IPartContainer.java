package ruiseki.omoshiroikamo.api.ids.part;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.core.datastructure.DimPos;

/**
 * An interface for containers that can hold {@link IPartType}s.
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
    public Map<ForgeDirection, IPartType<?, ?>> getParts();

    /**
     * @return If this container has at least one part.
     */
    public boolean hasParts();

    /**
     * Set the part for a side.
     *
     * @param side      The side to place the part on.
     * @param part      The part.
     * @param partState The state for this part.
     * @param <P>       The type of part.
     * @param <S>       The type of part state.
     */
    public <P extends IPartType<P, S>, S extends IPartState<P>> void setPart(ForgeDirection side, IPartType<P, S> part,
        IPartState<P> partState);

    /**
     * Check if the given part can be added at the given side.
     *
     * @param side The side to place the part on.
     * @param part The part.
     * @param <P>  The type of part.
     * @param <S>  The type of part state.
     * @return If the part can be added.
     */
    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean canAddPart(ForgeDirection side,
        IPartType<P, S> part);

    /**
     * Get the part of a side, can be null.
     *
     * @param side The side.
     * @return The part or null.
     */
    public IPartType getPart(ForgeDirection side);

    /**
     * @param side The side.
     * @return If the given side has a part.
     */
    public boolean hasPart(ForgeDirection side);

    /**
     * Remove the part from a side, can return null if there was no part on that side.
     *
     * @param side   The side.
     * @param player The player removing the part.
     * @return The removed part or null.
     */
    public IPartType removePart(ForgeDirection side, @Nullable EntityPlayer player);

    /**
     * dz
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
