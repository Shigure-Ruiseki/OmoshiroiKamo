package ruiseki.omoshiroikamo.core.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Interface for object providing gui-containers.
 *
 * @author rubensworks
 *
 */
public interface IGuiContainerProvider {

    /**
     * Get the unique ID for the GUI this blockState has.
     *
     * @return the GUI ID.
     */
    public int getGuiID();

    /**
     * Get the container for this blockState.
     *
     * @return The container class.
     */
    public Class<? extends Container> getContainer();

    /**
     * Get the GUI for this blockState.
     *
     * @return The GUI class.
     */
    @SideOnly(Side.CLIENT)
    public Class<? extends GuiScreen> getGui();

    /**
     * @return The mod providing this interface.
     */
    public String getModGui();

}
