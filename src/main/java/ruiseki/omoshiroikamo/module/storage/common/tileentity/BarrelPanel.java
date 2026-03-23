package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;

import static ruiseki.omoshiroikamo.module.storage.common.block.BlockBarrel.OPEN;

public class BarrelPanel extends StoragePanel {

    public BarrelPanel(PosGuiData data, PanelSyncManager syncManager, UISettings settings, TEBarrel tile, StorageWrapper wrapper) {
        super(data, syncManager, settings, tile, wrapper);

        syncManager.addCloseListener(player -> {
            BlockState state = BlockPropertyRegistry.getBlockState(tile.getWorld(), tile.getX(), tile.getY(), tile.getZ());
            state.setPropertyValue(OPEN, false);
            state.place(tile.getWorld(), tile.getX(), tile.getY(), tile.getZ());
            state.close();
        });
    }
}
