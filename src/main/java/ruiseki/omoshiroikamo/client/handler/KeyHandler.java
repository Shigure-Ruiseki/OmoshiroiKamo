package ruiseki.omoshiroikamo.client.handler;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.utils.Platform;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiFactories;
import ruiseki.omoshiroikamo.common.block.backpack.BlockBackpack;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class KeyHandler {

    public static final KeyHandler instance = new KeyHandler();

    public final KeyBinding keyOpenBackpack;

    private KeyHandler() {
        keyOpenBackpack = new KeyBinding(
            LibMisc.LANG.localize("keybind.backpackOpenToggle"),
            Keyboard.KEY_B,
            LibMisc.LANG.localize("category.omoshiroikamo"));
        ClientRegistry.registerKeyBinding(keyOpenBackpack);
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

    private void handleInput() {
        handleOpenBackpack();
    }

    private void handleOpenBackpack() {
        if (keyOpenBackpack.isPressed()) {
            EntityPlayer player = Platform.getClientPlayer();

            for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
                int slot = player.inventory.getSizeInventory() - 1 - armorIndex;
                ItemStack stack = player.inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() instanceof BlockBackpack.ItemBackpack) {

                    MGuiFactories.playerInventory()
                        .openFromPlayerInventoryClient(slot);
                    return;
                }
            }

            if (LibMods.Baubles.isLoaded()) {
                InventoryTypes.BAUBLES.visitAll(player, (type, index, stack) -> {
                    if (stack != null && stack.getItem() instanceof BlockBackpack.ItemBackpack) {
                        MGuiFactories.playerInventory()
                            .openFromBaublesClient(index);
                        return true;
                    }
                    return false;
                });
            }
        }
    }

}
