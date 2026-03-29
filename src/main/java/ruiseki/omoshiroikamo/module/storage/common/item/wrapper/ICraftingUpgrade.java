package ruiseki.omoshiroikamo.module.storage.common.item.wrapper;

public interface ICraftingUpgrade extends IStorageUpgrade {

    String CRAFTING_DEST_TAG = "CraftingDest";
    String USE_STORAGE_TAG = "UseStorage";

    CraftingDestination getCraftingDes();

    void setCraftingDes(CraftingDestination type);

    boolean isUseBackpack();

    void setUseBackpack(boolean used);

    enum CraftingDestination {
        STORAGE,
        INVENTORY;
    }
}
