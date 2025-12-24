package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.utils.item.PlayerMainInvWrapper;
import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.api.enums.SortType;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackHandler;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackInventoryHelper;

public class BackpackSH extends SyncHandler {

    public static final int UPDATE_SET_SORT_TYPE = 0;
    public static final int UPDATE_SORT_INV = 1;
    public static final int UPDATE_TRANSFER_TO_BACKPACK_INV = 2;
    public static final int UPDATE_TRANSFER_TO_PLAYER_INV = 3;
    public static final int UPDATE_SETTING = 4;

    private final PlayerMainInvWrapper playerInv;
    private final BackpackHandler handler;

    public BackpackSH(PlayerMainInvWrapper playerInv, BackpackHandler handler) {
        this.playerInv = playerInv;
        this.handler = handler;
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        switch (id) {
            case UPDATE_SET_SORT_TYPE:
                setSortType(buf);
                break;

            case UPDATE_SORT_INV:
                sortInventory(buf);
                break;

            case UPDATE_TRANSFER_TO_BACKPACK_INV:
                transferToBackpack(buf);
                break;

            case UPDATE_TRANSFER_TO_PLAYER_INV:
                transferToPlayerInventory(buf);
                break;

            case UPDATE_SETTING:
                updateBackpack(buf);
                break;

            default:
                break;
        }
        handler.writeToItem();
    }

    public void setSortType(PacketBuffer buf) {
        SortType sortType = SortType.values()[buf.readInt()];
        setSortType(sortType);
    }

    public void setSortType(SortType sortType) {
        handler.setSortType(sortType);
    }

    public void sortInventory(PacketBuffer buf) throws IOException {
        int size = handler.getBackpackSlots();

        for (int i = 0; i < size; i++) {
            handler.getBackpackHandler()
                .setStackInSlot(i, buf.readItemStackFromBuffer());
        }
    }

    public void transferToBackpack(boolean transferMatched) {
        BackpackInventoryHelper.transferPlayerInventoryToBackpack(handler, playerInv, transferMatched);
    }

    public void transferToBackpack(PacketBuffer buf) {
        boolean transferMatched = buf.readBoolean();
        BackpackInventoryHelper.transferPlayerInventoryToBackpack(handler, playerInv, transferMatched);
    }

    public void transferToPlayerInventory(boolean transferMatched) {
        BackpackInventoryHelper.transferBackpackToPlayerInventory(handler, playerInv, transferMatched);
    }

    public void transferToPlayerInventory(PacketBuffer buf) {
        boolean transferMatched = buf.readBoolean();
        BackpackInventoryHelper.transferBackpackToPlayerInventory(handler, playerInv, transferMatched);
    }

    public void updateBackpack(PacketBuffer buf) throws IOException {
        boolean lock = buf.readBoolean();
        String uuid = buf.readStringFromBuffer(36);
        boolean search = buf.readBoolean();
        boolean tab = buf.readBoolean();
        handler.setLockBackpack(lock);
        handler.setUuid(uuid);
        handler.setSearchBackpack(search);
        handler.setKeepTab(tab);
    }
}
