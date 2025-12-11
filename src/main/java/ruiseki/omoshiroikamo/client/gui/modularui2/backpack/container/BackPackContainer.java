package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.container;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.api.inventory.ClickType;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.ModularBackpackSlot;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.ICraftingUpgrade;

public class BackPackContainer extends ModularContainer {

    private final BackpackHandler handler;
    private final Integer backpackSlotIndex;

    public BackPackContainer(BackpackHandler handler, Integer backpackSlotIndex) {
        this.handler = handler;
        this.backpackSlotIndex = backpackSlotIndex;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
        ClickType clickTypeIn = ClickType.fromNumber(mode);
        InventoryPlayer inventoryplayer = player.inventory;
        ItemStack heldStack = inventoryplayer.getItemStack();

        if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode
            && (heldStack == null || heldStack.stackSize <= 0)
            && slotId >= 0) {

            ModularSlot slot = (ModularSlot) getSlot(slotId);
            if (slot != null && slot.getStack() != null) {
                player.inventory.setItemStack(
                    slot.getStack()
                        .copy());
            }
            return null;

        } else if (clickTypeIn == ClickType.SWAP && mouseButton >= 0
            && mouseButton < 9
            && backpackSlotIndex != null
            && backpackSlotIndex == mouseButton) {
                return null;
            }

        return super.slotClick(slotId, mouseButton, mode, player);
    }

    @Override
    public ItemStack transferItem(ModularSlot fromSlot, ItemStack fromStack) {
        if (fromStack == null || fromStack.stackSize <= 0) return fromStack;

        int craftingSlotIndex = parseCraftingSlotIndex(fromSlot.getSlotGroupName());

        if (craftingSlotIndex >= 0) {
            Map<Integer, ICraftingUpgrade> map = handler.gatherCapabilityUpgrades(ICraftingUpgrade.class);
            ICraftingUpgrade wrapper = map.get(craftingSlotIndex);

            if (wrapper != null) {
                String targetGroup = switch (wrapper.getCraftingDes()) {
                    case BACKPACK -> "backpack_inventory";
                    case INVENTORY -> "player_inventory";
                };

                for (ModularSlot toSlot : getShiftClickSlots()) {
                    if (!targetGroup.equals(toSlot.getSlotGroupName())) continue;
                    fromStack = transferToSlot(fromStack, toSlot);
                    if (fromStack == null || fromStack.stackSize <= 0) return null;
                }
                return fromStack;
            }
        }

        if ("player_inventory".equals(fromSlot.getSlotGroupName())) {
            List<ModularSlot> memorizedSlots = getShiftClickSlots().stream()
                .filter(s -> s instanceof ModularBackpackSlot sb && handler.isSlotMemorized(sb.getSlotIndex()))
                .collect(Collectors.toList());

            for (ModularSlot toSlot : memorizedSlots) {
                fromStack = transferToSlot(fromStack, toSlot);
                if (fromStack == null || fromStack.stackSize <= 0) return null;
            }
        }

        return super.transferItem(fromSlot, fromStack);
    }

    protected @Nullable ItemStack transferToSlot(ItemStack stack, ModularSlot toSlot) {
        if (stack == null || stack.stackSize <= 0) return null;
        if (toSlot == null || !toSlot.func_111238_b() || !toSlot.isItemValid(stack)) return stack;

        ItemStack existing = toSlot.getStack();
        int limit = toSlot.getItemStackLimit(stack);

        if (existing == null || existing.stackSize <= 0) {
            if (stack.stackSize > limit) {
                toSlot.putStack(stack.splitStack(limit));
            } else {
                toSlot.putStack(stack.splitStack(stack.stackSize));
            }
            return stack.stackSize > 0 ? stack : null;
        }

        if (ItemHandlerHelper.canItemStacksStack(stack, existing)) {
            int total = existing.stackSize + stack.stackSize;
            if (total <= limit) {
                existing.stackSize = total;
                toSlot.onSlotChanged();
                return null;
            } else {
                existing.stackSize = limit;
                toSlot.onSlotChanged();
                stack.stackSize = total - limit;
                return stack;
            }
        }

        return stack;
    }

    protected Integer parseCraftingSlotIndex(String groupName) {
        if (groupName == null) return -1;

        int slotIndexPos = groupName.lastIndexOf("_slot_");
        if (slotIndexPos < 0) return -1;

        try {
            return Integer.parseInt(groupName.substring(slotIndexPos + "_slot_".length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
