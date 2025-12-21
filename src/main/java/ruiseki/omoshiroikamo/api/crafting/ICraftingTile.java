package ruiseki.omoshiroikamo.api.crafting;

import ruiseki.omoshiroikamo.api.client.IProgressTile;

public interface ICraftingTile extends IProgressTile {

    CraftingState getCraftingState();

    void setCraftingState(CraftingState state);
}
