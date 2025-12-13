package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.CraftingStackHandler;

public interface ICraftingUpgrade {

    String MATRIX_TAG = "Matrix";
    String CRAFTING_DEST_TAG = "CraftingDest";
    String USE_BACKPACK_TAG = "UseBackpack";

    CraftingStackHandler getMatrix();

    void setMatrix(CraftingStackHandler handler);

    CraftingDestination getCraftingDes();

    void setCraftingDes(CraftingDestination type);

    boolean isUseBackpack();

    void setUseBackpack(boolean used);

    enum CraftingDestination {
        BACKPACK,
        INVENTORY;
    }
}
