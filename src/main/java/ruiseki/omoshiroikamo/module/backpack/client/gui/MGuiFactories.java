package ruiseki.omoshiroikamo.module.backpack.client.gui;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.factory.GuiManager;

import ruiseki.omoshiroikamo.module.backpack.client.gui.factory.ItemBackpackUIFactory;
import ruiseki.omoshiroikamo.module.backpack.client.gui.factory.TEBackpackUIFactory;

public class MGuiFactories {

    public static TEBackpackUIFactory tileEntity() {
        return TEBackpackUIFactory.INSTANCE;
    }

    public static ItemBackpackUIFactory playerInventory() {
        return ItemBackpackUIFactory.INSTANCE;
    }

    @ApiStatus.Internal
    public static void init() {
        GuiManager.registerFactory(tileEntity());
        GuiManager.registerFactory(playerInventory());
    }

    private MGuiFactories() {}
}
