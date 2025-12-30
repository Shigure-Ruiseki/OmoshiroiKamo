package ruiseki.omoshiroikamo.core.integration.waila;

import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.crafting.ICraftingTile;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class WailaUtils {

    public static String getProgress(IProgressTile tile) {
        float progress = tile.getProgress();
        return LibMisc.LANG.localize("gui.progress", progress * 100);
    }

    public static String getCraftingState(ICraftingTile tile) {
        return LibMisc.LANG.localize(
            "gui.craftingState." + tile.getCraftingState()
                .getName());
    }

    public static String getEnergyTransfer(IEnergyTile tile) {
        return LibMisc.LANG.localize("gui.energy_transfer", tile.getEnergyTransfer());
    }
}
