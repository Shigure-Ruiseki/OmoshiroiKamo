package ruiseki.omoshiroikamo.api.block;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.GuiContainerWrapper;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.screen.ModularScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IOKGuiHolder<T extends GuiData> extends IGuiHolder<T> {

    @SideOnly(Side.CLIENT)
    default GuiContainerWrapper createGuiContainer(ModularContainer container, ModularScreen screen) {
        return new GuiContainerWrapper(container, screen);
    }
}
