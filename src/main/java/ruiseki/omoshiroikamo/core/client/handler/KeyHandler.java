package ruiseki.omoshiroikamo.core.client.handler;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;

public class KeyHandler {

    public static final KeyHandler instance = new KeyHandler();

    public final KeyBinding keyOpenBackpack;
    public final KeyBinding keyBackpackPickBlock;

    private KeyHandler() {
        keyOpenBackpack = new KeyBinding(
            LangHelpers.localize("keybind.backpackOpenToggle"),
            Keyboard.KEY_B,
            LangHelpers.localize("category.omoshiroikamo"));
        ClientRegistry.registerKeyBinding(keyOpenBackpack);

        keyBackpackPickBlock = new KeyBinding(
            LangHelpers.localize("keybind.keyBackpackPickBlock"),
            Keyboard.KEY_NONE,
            LangHelpers.localize("category.omoshiroikamo"));
        ClientRegistry.registerKeyBinding(keyBackpackPickBlock);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        handleInput();
    }

    @SubscribeEvent
    public void onMouseInput(MouseInputEvent event) {
        if (Mouse.getEventButton() >= 0) {
            handleInput();
        }
    }

    private void handleInput() {}
}
