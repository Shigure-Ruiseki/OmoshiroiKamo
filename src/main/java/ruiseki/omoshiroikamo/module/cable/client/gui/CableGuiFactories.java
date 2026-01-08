package ruiseki.omoshiroikamo.module.cable.client.gui;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.factory.GuiManager;

import ruiseki.omoshiroikamo.module.cable.client.gui.factory.TEPartUIFactory;

public class CableGuiFactories {

    public static TEPartUIFactory tileEntity() {
        return TEPartUIFactory.INSTANCE;
    }

    @ApiStatus.Internal
    public static void preInit() {
        GuiManager.registerFactory(tileEntity());
    }

    private CableGuiFactories() {}
}
