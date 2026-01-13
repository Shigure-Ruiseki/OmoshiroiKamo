package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public class CableItemSlotSH extends SyncHandler {

    public static final int REQ_TAKE = 0;
    public static final int RESP_MOUSE = 1;

    private final ItemNetwork network;

    public CableItemSlotSH(ItemNetwork network) {
        this.network = network;
    }

    public void requestClick(ItemStack slotStack, ItemStack cursorStack, int slotAmount, boolean toInventory) {
        syncToServer(REQ_TAKE, buf -> {
            boolean slotNotEmpty = slotStack != null && slotStack.stackSize > 0;
            buf.writeBoolean(slotNotEmpty);
            if (slotNotEmpty) {
                ItemStackKey key = ItemStackKey.of(slotStack);
                key.write(buf);
            }

            buf.writeVarIntToBuffer(slotAmount);

            boolean cursorPresent = cursorStack != null && cursorStack.stackSize > 0;
            buf.writeBoolean(cursorPresent);
            if (cursorPresent) {
                buf.writeItemStackToBuffer(cursorStack);
            }

            buf.writeBoolean(toInventory);
        });
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        switch (id) {
            case REQ_TAKE -> handleClick(buf);
            default -> {}
        }
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) throws IOException {
        switch (id) {
            case RESP_MOUSE -> handleClientMouse(buf);
            default -> {}
        }
    }

    protected void handleClick(PacketBuffer buf) throws IOException {
        boolean slotNotEmpty = buf.readBoolean();
        ItemStackKey key = slotNotEmpty ? ItemStackKey.read(buf) : null;

        int amount = buf.readVarIntFromBuffer();

        boolean cursorPresent = buf.readBoolean();
        ItemStack cursorStack = cursorPresent ? buf.readItemStackFromBuffer() : null;

        boolean toInventory = buf.readBoolean();

        if (network == null) return;
        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null) return;

        if (cursorStack != null && cursorStack.stackSize > 0) {
            ItemStack leftover = network.insert(cursorStack);
            player.inventory.setItemStack(leftover);
            syncToClient(RESP_MOUSE, bufOut -> bufOut.writeItemStackToBuffer(leftover));
        } else if (key != null) {
            ItemStack extracted = network.extract(key, amount);
            if (extracted != null) {
                if (toInventory) {
                    if (!player.inventory.addItemStackToInventory(extracted)) {
                        player.dropPlayerItemWithRandomChoice(extracted, false);
                    }
                } else {
                    player.inventory.setItemStack(extracted);
                    syncToClient(RESP_MOUSE, bufOut -> bufOut.writeItemStackToBuffer(extracted));
                }
            }
        }

        player.inventory.markDirty();
    }

    protected void handleClientMouse(PacketBuffer buf) throws IOException {
        ItemStack stack = buf.readItemStackFromBuffer();
        Minecraft.getMinecraft().thePlayer.inventory.setItemStack(stack);
    }

}
