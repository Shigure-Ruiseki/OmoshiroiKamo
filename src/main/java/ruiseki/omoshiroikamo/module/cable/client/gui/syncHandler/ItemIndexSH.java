package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.item.ItemStackKeyUtils;
import ruiseki.omoshiroikamo.module.cable.common.network.crafting.CraftingNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.StorageTerminal;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.StorageTerminalPanel;

public class ItemIndexSH extends SyncHandler {

    public static final int SYNC_FULL = 0;
    public static final int SYNC_DELTA = 1;

    public static final int EXTRACT = 100;
    public static final int INSERT = 101;
    public static final int SYNC_SERVER_STACK = 102;
    public static final int CLEAR_GRID = 103;

    public static final int SET_CHANNEL = 200;

    private final StorageTerminal terminal;
    private final StorageTerminalPanel panel;
    private final ItemNetwork itemNetwork;
    private final CraftingNetwork craftingNetwork;
    private final ItemIndexClient clientIndex;

    public ItemIndexSH(StorageTerminal terminal, StorageTerminalPanel panel, ItemIndexClient clientIndex) {
        this.terminal = terminal;
        this.itemNetwork = terminal.getItemNetwork();
        this.craftingNetwork = terminal.getCraftingNetwork();
        this.panel = panel;
        this.clientIndex = clientIndex;
    }

    public void requestSync(int clientVersion) {
        if (itemNetwork == null) return;

        int serverVer = itemNetwork.getIndexVersion();

        if (clientVersion < 0) {
            syncToClient(SYNC_FULL, b -> {
                b.writeInt(itemNetwork.getIndexVersion());
                ItemStackKeyUtils.writeMap(
                    b,
                    itemNetwork.getIndex()
                        .view());
                ItemStackKeyUtils.writeKeys(
                    b,
                    craftingNetwork.getIndex()
                        .keys());
            });
            return;
        }

        if (clientVersion != serverVer) {
            syncToClient(SYNC_DELTA, b -> {
                ItemStackKeyUtils.writeDelta(b, itemNetwork.getLastSnapshot(), itemNetwork.getIndex(), serverVer);
                ItemStackKeyUtils.writeKeys(
                    b,
                    craftingNetwork.getIndex()
                        .keys());
            });
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

        if (id == SYNC_FULL) {
            int version = buf.readInt();
            clientIndex.replace(ItemStackKeyUtils.readMap(buf), ItemStackKeyUtils.readKeys(buf), version);
            return;
        }

        if (id == SYNC_DELTA) {
            int version = buf.readInt();

            var added = ItemStackKeyUtils.readMap(buf);
            var removed = ItemStackKeyUtils.readKeys(buf);
            var craftables = ItemStackKeyUtils.readKeys(buf);

            clientIndex.applyDelta(added, removed, craftables, version);
        }
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        if (id == SET_CHANNEL) {
            handleServerChannel(buf);
        }

        if (id == EXTRACT) {
            handleExtract(buf);
        }

        if (id == INSERT) {
            handleInsert();
        }

        if (id == SYNC_SERVER_STACK) {
            handleServerStackMouse(buf);
        }

        if (id == CLEAR_GRID) {
            handleClearGrid();
        }
    }

    public void syncChannel(int channel) {
        syncToServer(SET_CHANNEL, buf -> buf.writeVarIntToBuffer(channel));
    }

    public void extractToMouse(ItemStack stack, int amount) {
        syncToServer(EXTRACT, buf -> {
            buf.writeItemStackToBuffer(stack);
            buf.writeVarIntToBuffer(amount);
            buf.writeBoolean(false);
        });
    }

    public void extractToInventory(ItemStack stack, int amount) {
        syncToServer(EXTRACT, buf -> {
            buf.writeItemStackToBuffer(stack);
            buf.writeVarIntToBuffer(amount);
            buf.writeBoolean(true);
        });
    }

    public void insertFromMouse() {
        syncToServer(INSERT, buf -> {});
    }

    public void syncServerStackMouse(ItemStack stack) {
        syncToServer(SYNC_SERVER_STACK, buf -> buf.writeItemStackToBuffer(stack));
    }

    public void clearCraftingGrid() {
        syncToServer(ItemIndexSH.CLEAR_GRID, buffer -> {});
    }

    private void handleExtract(PacketBuffer buf) throws IOException {
        ItemStack stack = buf.readItemStackFromBuffer();
        int amount = buf.readVarIntFromBuffer();
        boolean toInventory = buf.readBoolean();

        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null || itemNetwork == null) return;

        amount = Math.max(1, amount);

        ItemStack extracted = itemNetwork.extract(stack, amount, terminal.getChannel());
        if (extracted == null) return;

        if (toInventory) {
            if (!player.inventory.addItemStackToInventory(extracted)) {
                player.dropPlayerItemWithRandomChoice(extracted, false);
            }
            player.inventory.markDirty();
        } else {
            player.inventory.setItemStack(extracted);
        }
    }

    private void handleInsert() {
        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null || itemNetwork == null) return;

        ItemStack mouse = player.inventory.getItemStack();
        if (mouse == null) return;
        ItemStack toInsert = mouse.copy();
        ItemStack remainder = itemNetwork.insert(toInsert, terminal.getChannel());

        if (remainder == null || remainder.stackSize == 0) {
            player.inventory.setItemStack(null);
        } else {
            player.inventory.setItemStack(remainder);
        }

        player.inventory.markDirty();
    }

    private void handleServerStackMouse(PacketBuffer buffer) throws IOException {
        ItemStack stack = buffer.readItemStackFromBuffer();
        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null) return;
        if (player.inventory.getCurrentItem() != null) return;
        player.inventory.setItemStack(stack);
    }

    private void handleServerChannel(PacketBuffer buffer) {
        int channel = buffer.readVarIntFromBuffer();
        if (itemNetwork != null) {
            itemNetwork.setIndexChannel(channel);
            itemNetwork.markDirty(channel);
        }
    }

    private void handleClearGrid() {
        if (itemNetwork == null) return;
        for (int i = 0; i < 9; i++) {
            ItemStack s = terminal.craftingStackHandler.extractItem(i, Integer.MAX_VALUE, false);
            if (s == null) continue;
            ItemStack rem = itemNetwork.insert(s, terminal.getChannel());
            terminal.craftingStackHandler.setStackInSlot(i, rem);
        }
    }

}
