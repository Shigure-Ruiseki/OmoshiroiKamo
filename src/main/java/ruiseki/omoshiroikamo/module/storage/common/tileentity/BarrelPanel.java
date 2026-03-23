package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;

public class BarrelPanel extends StoragePanel {

    public BarrelPanel(PosGuiData data, PanelSyncManager syncManager, UISettings settings, TEBarrel tile,
        StorageWrapper wrapper, int width) {
        super(data, syncManager, settings, tile, wrapper, width);

        syncManager.addCloseListener(player -> {
            tile.setOpen(false);
        });
    }
}
