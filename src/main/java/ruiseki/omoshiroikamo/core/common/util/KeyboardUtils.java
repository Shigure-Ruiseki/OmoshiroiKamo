package ruiseki.omoshiroikamo.core.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

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

    @SideOnly(Side.CLIENT)
    public static String getUseKeyName() {
        return getKeyName(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
    }

    @SideOnly(Side.CLIENT)
    public static String getAttackKeyName() {
        return getKeyName(Minecraft.getMinecraft().gameSettings.keyBindAttack);
    }

    @SideOnly(Side.CLIENT)
    public static String getSneakKeyName() {
        return getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSneak);
    }

    @SideOnly(Side.CLIENT)
    public static String getSprintKeyName() {
        return getKeyName(Minecraft.getMinecraft().gameSettings.keyBindSprint);
    }

    @SideOnly(Side.CLIENT)
    private static String getKeyName(net.minecraft.client.settings.KeyBinding key) {
        return GameSettings.getKeyDisplayString(key.getKeyCode());
    }
}
