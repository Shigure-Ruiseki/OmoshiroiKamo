package ruiseki.omoshiroikamo.module.storage.common.tileentity;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;

public class StoragePanel extends ModularPanel {

    public StoragePanel(PosGuiData data, PanelSyncManager syncManager, UISettings settings, TEStorage tileEntity, StorageWrapper wrapper) {
        super("gui");

    }

}
