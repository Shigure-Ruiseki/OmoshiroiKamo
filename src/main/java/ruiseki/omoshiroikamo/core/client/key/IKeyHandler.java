package ruiseki.omoshiroikamo.core.client.key;

import net.minecraft.client.settings.KeyBinding;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A handler responsible for handling key presses.
 *
 * @author immortaleeb
 *
 */
@SideOnly(Side.CLIENT)
public interface IKeyHandler {

    /**
     * This method is called whenever a key, which is mapped
     * to this KeyHandler is pressed.
     *
     * @param kb {@link KeyBinding} of the key that was pressed.
     */
    public void onKeyPressed(KeyBinding kb);
}
