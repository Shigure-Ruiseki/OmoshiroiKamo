package ruiseki.omoshiroikamo.api.persist.nbt;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Classes tagged with this interface can have their fields persisted to NBT when they are annotated with
 * {@link NBTPersist}.
 * 
 * @author rubensworks
 */
public interface INBTProvider {

    /**
     * Write the data in this provider to NBT.
     * 
     * @param tag The tag to write to.
     */
    void writeGeneratedFieldsToNBT(NBTTagCompound tag);

    /**
     * Read data from the given tag to this provider.
     * 
     * @param tag The tag to read from.
     */
    void readGeneratedFieldsFromNBT(NBTTagCompound tag);

}
