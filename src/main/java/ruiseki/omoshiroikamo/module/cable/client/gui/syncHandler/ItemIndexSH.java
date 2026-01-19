package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexClient;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemIndexUtils;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.CableTerminal;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.TerminalPanel;

public class ItemIndexSH extends SyncHandler {

    public static final int SYNC_FULL = 0;
    public static final int SYNC_DELTA = 1;

    public static final int EXTRACT = 100;
    public static final int INSERT = 101;
    public static final int SYNC_SERVER_STACK = 102;

    public static final int SET_CHANNEL = 200;

    private final CableTerminal terminal;
    private final TerminalPanel panel;
    private final ItemNetwork network;
    private final ItemIndexClient clientIndex;

    public ItemIndexSH(CableTerminal terminal, TerminalPanel panel, ItemIndexClient clientIndex) {
        this.terminal = terminal;
        this.network = terminal.getItemNetwork();
        this.panel = panel;
        this.clientIndex = clientIndex;
    }

    public void requestSync(int clientVersion) {
        if (network == null) return;

        int serverVer = network.getIndexVersion();

        if (clientVersion < 0) {
            syncToClient(SYNC_FULL, b -> ItemIndexUtils.writeFull(b, network));
            return;
        }

        if (clientVersion != serverVer) {
            syncToClient(
                SYNC_DELTA,
                b -> ItemIndexUtils.writeDelta(b, network.getLastSnapshot(), network.getIndex(), serverVer));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

        if (id == SYNC_FULL) {
            int version = buf.readInt();
            clientIndex.replace(ItemIndexUtils.readMap(buf), version);
            return;
        }

        if (id == SYNC_DELTA) {
            int version = buf.readInt();
            clientIndex.applyDelta(ItemIndexUtils.readMap(buf), ItemIndexUtils.readKeys(buf), version);
            return;
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
    }

    public void syncChannel(int channel) {
        syncToServer(SET_CHANNEL, buf -> buf.writeVarIntToBuffer(channel));
    }

    public void extractToMouse(ItemStack stack, int amount) {
        syncToServer(EXTRACT, buf -> {
            ItemStackKeyPool.get(stack)
                .write(buf);
            buf.writeVarIntToBuffer(amount);
            buf.writeBoolean(false);
        });
    }

    public void extractToInventory(ItemStack stack, int amount) {
        syncToServer(EXTRACT, buf -> {
            ItemStackKeyPool.get(stack)
                .write(buf);
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

    private void handleExtract(PacketBuffer buf) throws IOException {
        ItemStackKey key = ItemStackKey.read(buf);
        int amount = buf.readVarIntFromBuffer();
        boolean toInventory = buf.readBoolean();

        EntityPlayer player = getSyncManager().getPlayer();
        if (player == null || network == null) return;

        amount = Math.max(1, amount);

        ItemStack extracted = network.extract(key, amount);
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
        if (player == null || network == null) return;

        ItemStack mouse = player.inventory.getItemStack();
        if (mouse == null) return;
        ItemStack toInsert = mouse.copy();
        ItemStack remainder = network.insert(toInsert);

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
        if (network != null) {
            network.setIndexChannel(channel);
            network.markDirty(channel);
        }
    }
}
