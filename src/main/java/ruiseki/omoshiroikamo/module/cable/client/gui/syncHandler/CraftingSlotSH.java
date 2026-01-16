package ruiseki.omoshiroikamo.module.cable.client.gui.syncHandler;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import com.cleanroommc.modularui.value.sync.ItemSlotSH;
import com.cleanroommc.modularui.widgets.slot.InventoryCraftingWrapper;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.module.cable.client.gui.widget.CableCraftingSlot;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;
import ruiseki.omoshiroikamo.module.cable.common.network.terminal.CableTerminal;

public class CraftingSlotSH extends ItemSlotSH {

    public static final int SYNC_CRAFT_INTENT = 10;

    private final CableTerminal terminal;

    public CraftingSlotSH(ModularSlot slot, CableTerminal terminal) {
        super(slot);
        this.terminal = terminal;
    }

    public void sendCraftIntent() {
        syncToServer(SYNC_CRAFT_INTENT, buf -> {});
    }

    @Override
    public void readOnServer(int id, PacketBuffer buf) throws IOException {
        if (id != SYNC_CRAFT_INTENT) {
            super.readOnServer(id, buf);
            return;
        }

        CableCraftingSlot resultSlot = (CableCraftingSlot) getSlot();
        EntityPlayer player = getSyncManager().getPlayer();

        InventoryCraftingWrapper wrapper = resultSlot.getCraftMatrix();

        for (int i = 0; i < wrapper.getSizeInventory() - 1; i++) {
            ItemStack stack = wrapper.getStackInSlot(i);
            if (stack == null) continue;

            boolean usedNetwork = tryConsumeNetwork(stack);

            if (!usedNetwork) {
                consumeLocal(player, wrapper, i, stack);
            }
        }

        wrapper.notifyContainer();
    }

    private boolean tryConsumeNetwork(ItemStack stack) {
        if (terminal.getItemNetwork() == null) return false;
        ItemStack extracted = terminal.getItemNetwork()
            .extract(ItemStackKey.of(stack), 1);
        return extracted != null && extracted.stackSize == 1;
    }

    private void consumeLocal(EntityPlayer player, InventoryCrafting matrix, int slot, ItemStack stack) {
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            matrix.setInventorySlotContents(slot, null);
        }

        if (stack.getItem()
            .hasContainerItem(stack)) {
            ItemStack cont = stack.getItem()
                .getContainerItem(stack);
            if (cont != null && cont.isItemStackDamageable() && cont.getItemDamage() > cont.getMaxDamage()) {
                MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, cont));
            } else if (cont != null) {
                matrix.setInventorySlotContents(slot, cont);
            }
        }
    }

    public ItemNetwork getNetwork() {
        return terminal.getItemNetwork();
    }
}
