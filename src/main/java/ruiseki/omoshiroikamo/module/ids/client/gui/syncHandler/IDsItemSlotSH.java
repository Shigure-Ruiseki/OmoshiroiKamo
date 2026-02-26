package ruiseki.omoshiroikamo.module.ids.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.module.ids.common.item.part.terminal.storage.StorageTerminal;
import ruiseki.omoshiroikamo.module.ids.common.item.part.tunnel.item.ItemNetwork;

public class IDsItemSlotSH extends SyncHandler {

    public static final int REQ_TAKE = 0;
    public static final int RESP_CLONE = 1;

    public static final int RESP_MOUSE = 100;

    public final ItemNetwork itemNetwork;
    public final StorageTerminal terminal;

    public IDsItemSlotSH(StorageTerminal terminal) {
        this.terminal = terminal;
        this.itemNetwork = terminal.getItemNetwork();
    }

    public void requestClick(ItemStack slotStack, ItemStack cursorStack, int slotAmount, boolean toInventory) {
        syncToServer(REQ_TAKE, buf -> {
            boolean slotNotEmpty = slotStack != null && slotStack.stackSize > 0;
            buf.writeBoolean(slotNotEmpty);
            if (slotNotEmpty) {
                buf.writeItemStackToBuffer(slotStack);
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

    public void requestClone(ItemStack slotStack) {
        syncToServer(RESP_CLONE, buf -> { buf.writeItemStackToBuffer(slotStack); });
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        switch (id) {
            case REQ_TAKE -> handleClick(buf);
            case RESP_CLONE -> handleClone(buf);
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

    private void handleClone(PacketBuffer buf) throws IOException {
        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null || !player.capabilities.isCreativeMode) return;
        ItemStack stack = buf.readItemStackFromBuffer();
        stack.stackSize = stack.getMaxStackSize();
        syncToClient(RESP_MOUSE, out -> out.writeItemStackToBuffer(stack));
    }

    protected void handleClick(PacketBuffer buf) throws IOException {
        boolean slotNotEmpty = buf.readBoolean();
        ItemStack stack = slotNotEmpty ? buf.readItemStackFromBuffer() : null;

        int amount = buf.readVarIntFromBuffer();

        boolean cursorPresent = buf.readBoolean();
        ItemStack cursorStack = cursorPresent ? buf.readItemStackFromBuffer() : null;

        boolean toInventory = buf.readBoolean();

        if (itemNetwork == null) return;
        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null) return;

        if (cursorStack != null && cursorStack.stackSize > 0) {
            ItemStack leftover = itemNetwork.insert(cursorStack, terminal.getChannel());
            player.inventory.setItemStack(leftover);
            syncToClient(RESP_MOUSE, bufOut -> bufOut.writeItemStackToBuffer(leftover));
        } else if (stack != null) {
            ItemStack extracted = itemNetwork.extract(stack, amount, terminal.getChannel());
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
