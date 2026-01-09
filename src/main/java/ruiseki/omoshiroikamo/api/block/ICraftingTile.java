package ruiseki.omoshiroikamo.api.block;

import ruiseki.omoshiroikamo.api.client.IProgressTile;

public interface ICraftingTile extends IProgressTile {

    CraftingState getCraftingState();

    void setCraftingState(CraftingState state);
}
