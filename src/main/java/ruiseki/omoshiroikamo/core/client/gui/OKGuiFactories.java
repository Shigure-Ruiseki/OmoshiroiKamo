package ruiseki.omoshiroikamo.core.client.gui;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.factory.GuiManager;

import ruiseki.omoshiroikamo.core.client.gui.factory.TESideFactory;

public class OKGuiFactories {

    public static TESideFactory tileEntity() {
        return TESideFactory.INSTANCE;
    }

    @ApiStatus.Internal
    public static void preInit() {
        GuiManager.registerFactory(tileEntity());
    }

    private OKGuiFactories() {}
}
