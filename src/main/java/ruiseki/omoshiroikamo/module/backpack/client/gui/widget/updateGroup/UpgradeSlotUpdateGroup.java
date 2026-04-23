package ruiseki.omoshiroikamo.module.backpack.client.gui.widget.updateGroup;

import java.util.HashMap;
import java.util.Map;

import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import ruiseki.omoshiroikamo.api.storage.widget.IUpgradeSlotGroupFactory;
import ruiseki.omoshiroikamo.api.storage.widget.UpgradeSlotGroupRegistry;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class UpgradeSlotUpdateGroup {

    public final BackpackPanel panel;
    public final BackpackWrapper wrapper;
    public final int slotIndex;
    public final PanelSyncManager syncManager;

    final Map<String, Object> components = new HashMap<>();

    public UpgradeSlotUpdateGroup(BackpackPanel panel, BackpackWrapper wrapper, int slotIndex) {
        this.panel = panel;
        this.wrapper = wrapper;
        this.slotIndex = slotIndex;
        this.syncManager = panel.syncManager;

        for (IUpgradeSlotGroupFactory factory : UpgradeSlotGroupRegistry.getFactories()) {
            factory.build(this);
        }
    }

    public <T> void put(String key, T value) {
        components.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {

        Object obj = components.get(key);

        if (obj == null) {
            return null;
        }

        return (T) obj;
    }
}
