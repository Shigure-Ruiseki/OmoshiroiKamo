package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.syncHandler;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.common.block.dml.lootFabricator.TELootFabricator;

public class LootFabSH extends SyncHandler {

    public static final int UPDATE_OUTPUT_ITEM = 0;

    private final TELootFabricator tileEntity;

    public LootFabSH(TELootFabricator tileEntity) {
        this.tileEntity = tileEntity;
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) throws IOException {

    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        switch (id) {
            case UPDATE_OUTPUT_ITEM:
                updateOutputItem(buf);
                break;
        }
    }

    public void updateOutputItem(PacketBuffer buf) throws IOException {
        ItemStack outputItem = buf.readItemStackFromBuffer();
        tileEntity.setOutputItem(outputItem);
    }
}
