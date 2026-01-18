package ruiseki.omoshiroikamo.api.cable;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.SidedPosGuiData;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

public interface ICableEndpoint extends ICableNode {

    @NotNull
    ModularPanel endpointPanel(SidedPosGuiData data, PanelSyncManager syncManager, UISettings settings);

    default GuiContainerWrapper createGuiContainer(ModularContainer container, ModularScreen screen) {
        return new GuiContainerWrapper(container, screen);
    }
}
