package ruiseki.omoshiroikamo.module.ids.common.part.write;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.module.ids.common.part.IPartContainer;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;

/**
 * A part that can write redstone levels.
 * 
 * @author rubensworks
 */
public interface IPartRedstoneWriter<P extends IPartWriter<P, S>, S extends IPartState<P>> extends IPartWriter<P, S> {

    /**
     * Set the redstone level for given container.
     * 
     * @param partContainer The container to apply to.
     * @param side          The side this part is on.
     * @param level         The level to set the redstone output.
     */
    public void setRedstoneLevel(IPartContainer partContainer, ForgeDirection side, int level);

}
