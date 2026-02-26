package ruiseki.omoshiroikamo.core.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * This is a temporary storage for NBT data when {@link TileEntityOK}s are destroyed.
 * In the dropped blocks method this tag should then be used to add to the dropped blockState.
 * 
 * @author rubensworks
 *
 */
public final class TileEntityNBTStorage {

    private TileEntityNBTStorage() {}

    /**
     * The temporary tag storage for dropped NBT data from {@link TileEntityOK}.
     */
    public static NBTTagCompound TAG = null;
    /**
     * The temporary tag storage for dropped custom name from {@link TileEntityOK}.
     */
    public static String NAME = null;
    /**
     * The temporary tile storage for dropped custom tile from {@link TileEntityOK}.
     */
    public static TileEntityOK TILE = null;

}
