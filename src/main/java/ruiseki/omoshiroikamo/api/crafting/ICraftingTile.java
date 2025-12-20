package ruiseki.omoshiroikamo.api.crafting;

import ruiseki.omoshiroikamo.api.block.IOKTile;

public interface ICraftingTile extends IOKTile {

    CraftingState getCraftingState();

    void setCraftingState(CraftingState state);
}
