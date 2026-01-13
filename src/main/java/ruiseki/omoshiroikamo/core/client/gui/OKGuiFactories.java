package ruiseki.omoshiroikamo.core.client.gui;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.factory.GuiManager;

import ruiseki.omoshiroikamo.core.client.gui.factory.ItemFactory;
import ruiseki.omoshiroikamo.core.client.gui.factory.TESideFactory;

public class OKGuiFactories {

    public static TESideFactory tileEntity() {
        return TESideFactory.INSTANCE;
    }

    public static ItemFactory item() {
        return ItemFactory.INSTANCE;
    }

    @ApiStatus.Internal
    public static void preInit() {
        GuiManager.registerFactory(tileEntity());
        GuiManager.registerFactory(item());
    }

    private OKGuiFactories() {}
}
