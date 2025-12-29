package ruiseki.omoshiroikamo.core.client.handler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.factory.inventory.InventoryTypes;
import com.cleanroommc.modularui.utils.Platform;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import ruiseki.omoshiroikamo.api.item.BaublesUtils;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.backpack.client.gui.MGuiFactories;
import ruiseki.omoshiroikamo.module.backpack.common.block.BlockBackpack;
import ruiseki.omoshiroikamo.module.backpack.common.network.PacketQuickDraw;
import ruiseki.omoshiroikamo.module.backpack.common.util.BackpackInventoryUtils;

public class KeyHandler {

    public static final KeyHandler instance = new KeyHandler();

    public final KeyBinding keyOpenBackpack;
    public final KeyBinding keyBackpackPickBlock;

    private KeyHandler() {
        keyOpenBackpack = new KeyBinding(
            LibMisc.LANG.localize("keybind.backpackOpenToggle"),
            Keyboard.KEY_B,
            LibMisc.LANG.localize("category.omoshiroikamo"));
        ClientRegistry.registerKeyBinding(keyOpenBackpack);

        keyBackpackPickBlock = new KeyBinding(
            LibMisc.LANG.localize("keybind.keyBackpackPickBlock"),
            Keyboard.KEY_NONE,
            LibMisc.LANG.localize("category.omoshiroikamo"));
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

    private void handleInput() {
        handleOpenBackpack();
        handleBackpackQuickDraw();
    }

    private void handleOpenBackpack() {
        if (keyOpenBackpack.isPressed() && BackportConfigs.useBackpack) {
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

    private void handleBackpackQuickDraw() {
        if (!keyBackpackPickBlock.getIsKeyPressed() || !BackportConfigs.useBackpack) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null) return;

        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null || player.capabilities.isCreativeMode) return;

        ItemStack held = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (held != null) return;

        MovingObjectPosition mop = mc.objectMouseOver;
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        Block block = mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
        int meta = mc.theWorld.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
        Item item = Item.getItemFromBlock(block);
        if (item == null) return;

        ItemStack wanted = new ItemStack(item, 1, meta);

        boolean haveItem = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null && ItemUtils.areStacksEqual(stack, wanted)) {
                haveItem = true;
            }
        }
        if (haveItem) return;

        ItemStack result = null;
        if (LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            result = BackpackInventoryUtils.getQuickDrawStack(baublesInventory, wanted, InventoryTypes.BAUBLES);
        }

        if (result == null) {
            result = BackpackInventoryUtils.getQuickDrawStack(player.inventory, wanted, InventoryTypes.PLAYER);
        }

        if (result != null && mc.theWorld.isRemote) {
            int slot = player.inventory.currentItem;
            PacketHandler.INSTANCE.sendToServer(new PacketQuickDraw(slot, result));
        }
    }

}
