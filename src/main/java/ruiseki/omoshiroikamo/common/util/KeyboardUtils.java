package ruiseki.omoshiroikamo.common.util;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KeyboardUtils {

    @SideOnly(Side.CLIENT)
    public static boolean isHoldingShift() {
        return Keyboard.isCreated()
            && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
    }

    @SideOnly(Side.CLIENT)
    public static boolean isHoldingCTRL() {
        return Keyboard.isCreated()
            && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL));
    }
}
