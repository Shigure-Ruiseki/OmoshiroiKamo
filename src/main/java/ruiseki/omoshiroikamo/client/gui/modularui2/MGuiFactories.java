package ruiseki.omoshiroikamo.client.gui.modularui2;

import org.jetbrains.annotations.ApiStatus;

import com.cleanroommc.modularui.factory.GuiManager;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.factory.ItemBackpackUIFactory;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.factory.TEBackpackUIFactory;

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
