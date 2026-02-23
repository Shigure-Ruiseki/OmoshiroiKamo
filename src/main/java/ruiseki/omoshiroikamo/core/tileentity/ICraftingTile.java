package ruiseki.omoshiroikamo.core.tileentity;

import ruiseki.omoshiroikamo.api.enums.CraftingState;

public interface ICraftingTile extends IProgressTile {

    CraftingState getCraftingState();

    void setCraftingState(CraftingState state);
}
