package ruiseki.omoshiroikamo.module.cable.client.gui.container;

import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;

public class TerminalGuiContainer extends GuiContainerWrapper {

    private final TerminalContainer terminalContainer;

    public TerminalGuiContainer(ModularContainer container, ModularScreen screen) {
        super(container, screen);
        this.terminalContainer = (TerminalContainer) container;
    }
}
