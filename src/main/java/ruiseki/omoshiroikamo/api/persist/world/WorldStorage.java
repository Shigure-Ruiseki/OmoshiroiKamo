package ruiseki.omoshiroikamo.api.persist.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSavedData;

import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.api.persist.nbt.INBTProvider;
import ruiseki.omoshiroikamo.api.persist.nbt.NBTProviderComponent;

/**
 * Instances of this can store data inside the world NBT.
 * 
 * @author rubensworks
 */
public abstract class WorldStorage implements INBTProvider {

    protected final String mod;
    @Delegate
    private INBTProvider nbtProviderComponent = new NBTProviderComponent(this);

    public WorldStorage(String mod) {
        this.mod = mod;
    }

    /**
     * Read the counters.
     * 
     * @param tag The tag to read from.
     */
    public void readFromNBT(NBTTagCompound tag) {
        readGeneratedFieldsFromNBT(tag);
    }

    /**
     * Write the counters.
     * 
     * @param tag The tag to write to.
     */
    public void writeToNBT(NBTTagCompound tag) {
        writeGeneratedFieldsToNBT(tag);
    }

    /**
     * Reset the stored data because it will be reloaded from NBT.
     */
    public abstract void reset();

    /**
     * When a server is started.
     * 
     * @param event The received event.
     */
    public void onStartedEvent(FMLServerStartedEvent event) {
        loadData();
        afterLoad();
    }

    /**
     * When a server is stopping.
     * 
     * @param event The received event.
     */
    public void onStoppingEvent(FMLServerStoppingEvent event) {
        beforeSave();
        saveData();
    }

    protected abstract String getDataId();

    private NBTDataHolder getDataHolder() {
        String dataId = mod + "_" + getDataId();
        NBTDataHolder data = (NBTDataHolder) MinecraftServer.getServer().worldServers[0]
            .loadItemData(NBTDataHolder.class, dataId);
        if (data == null) {
            data = new NBTDataHolder(dataId);
            MinecraftServer.getServer().worldServers[0].setItemData(dataId, data);
        }
        return data;
    }

    private synchronized void loadData() {
        reset();
        readFromNBT(getDataHolder().tag);
    }

    private synchronized void saveData() {
        NBTDataHolder data = getDataHolder();
        writeToNBT(data.tag);
        data.setDirty(true);
    }

    /**
     * Called after the data is loaded from the world storage.
     */
    public void afterLoad() {

    }

    /**
     * Called before the data is saved to the world storage.
     */
    public void beforeSave() {

    }

    /**
     * Data holder for the global counter data.
     */
    public static class NBTDataHolder extends WorldSavedData {

        private static String KEY = "WorldStorageData";

        public NBTTagCompound tag;

        /**
         * Make a new instance.
         * 
         * @param key The key for the global counter data.
         */
        public NBTDataHolder(String key) {
            super(key);
            this.tag = new NBTTagCompound();
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.tag = tag.getCompoundTag(KEY);
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setTag(KEY, this.tag);
        }

    }

}
