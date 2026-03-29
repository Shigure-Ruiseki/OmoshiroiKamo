package ruiseki.omoshiroikamo.module.storage.common.helper;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.utils.item.PlayerMainInvWrapper;

import ruiseki.omoshiroikamo.module.storage.common.block.BlockBarrel;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.ICraftingUpgrade;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class StorageInventoryHelpers {

    public static void sortInventory(StorageWrapper wrapper, boolean reverse) {

        // Phase 1: memory slot
        for (int i = 0; i < wrapper.storageSlots; i++) {
            if (!wrapper.isSlotMemorized(i) || wrapper.isSlotLocked(i)) continue;

            ItemStack mem = wrapper.getMemorizedStack(i);
            if (mem == null) continue;

            ItemStack inSlot = wrapper.getStackInSlot(i);

            int limit = mem.getMaxStackSize() * wrapper.getTotalStackMultiplier();
            int current = inSlot != null ? inSlot.stackSize : 0;

            if (current >= limit) continue;

            int need = limit - current;

            for (int j = 0; j < wrapper.storageSlots; j++) {
                if (i == j || wrapper.isSlotLocked(j)) continue;

                ItemStack other = wrapper.getStackInSlot(j);
                if (other == null || other.stackSize <= 0) continue;

                boolean match = wrapper.isMemoryStackRespectNBT(i) ? ItemStack.areItemStacksEqual(mem, other)
                    : other.isItemEqual(mem);

                if (!match) continue;

                int move = Math.min(other.stackSize, need);

                if (inSlot == null) {
                    inSlot = other.copy();
                    inSlot.stackSize = move;
                    wrapper.storageItemStackHandler.setStackInSlot(i, inSlot);
                } else {
                    inSlot.stackSize += move;
                }

                other.stackSize -= move;
                if (other.stackSize <= 0) {
                    wrapper.storageItemStackHandler.setStackInSlot(j, null);
                }

                need -= move;
                if (need <= 0) break;
            }
        }

        // Phase 2: merge stack
        for (int i = 0; i < wrapper.storageSlots - 1; i++) {
            if (wrapper.isSlotLocked(i)) continue;

            boolean isMem = wrapper.isSlotMemorized(i);
            ItemStack baseStack = wrapper.getStackInSlot(i);
            if (baseStack == null) continue;

            int slotMaxSize = baseStack.getMaxStackSize() * wrapper.getTotalStackMultiplier();

            for (int j = i + 1; j < wrapper.storageSlots; j++) {
                if (isMem != wrapper.isSlotMemorized(j) || wrapper.isSlotLocked(j)) continue;

                ItemStack stack = wrapper.getStackInSlot(j);
                if (!ItemHandlerHelper.canItemStacksStack(baseStack, stack)) continue;
                if (stack.stackSize <= 0) continue;

                int diff = Math.min(stack.stackSize, slotMaxSize - baseStack.stackSize);

                if (diff > 0) {
                    baseStack.stackSize += diff;
                    stack.stackSize -= diff;

                    if (stack.stackSize <= 0) {
                        wrapper.storageItemStackHandler.setStackInSlot(j, null);
                    }
                } else if (diff == 0) break;
            }
        }

        // Phase 3: collect items
        List<ItemStack> sorted = new ArrayList<>();
        List<Map.Entry<ItemStack, Integer>> inPlace = new ArrayList<>();

        for (int i = 0; i < wrapper.storageSlots; i++) {
            ItemStack stack = wrapper.getStackInSlot(i);

            if (wrapper.isSlotMemorized(i) || wrapper.isSlotLocked(i)) {
                inPlace.add(new AbstractMap.SimpleEntry<>(stack, i));
            } else {
                sorted.add(stack);
            }
        }

        // Phase 4: sort
        sorted.sort((a, b) -> {
            if (a == null || a.stackSize <= 0) {
                if (b == null || b.stackSize <= 0) return 0;
                return 1;
            }
            if (b == null || b.stackSize <= 0) return -1;

            switch (wrapper.sortType) {
                case BY_NAME:
                    return reverse ? a.getDisplayName()
                        .compareTo(b.getDisplayName())
                        : b.getDisplayName()
                            .compareTo(a.getDisplayName());

                case BY_MOD_ID:
                    String aItem = Item.itemRegistry.getNameForObject(a.getItem());
                    String bItem = Item.itemRegistry.getNameForObject(b.getItem());

                    String aDomain = aItem.substring(0, aItem.indexOf(":"));
                    String bDomain = bItem.substring(0, bItem.indexOf(":"));

                    return reverse ? aDomain.compareTo(bDomain) : bDomain.compareTo(aDomain);

                case BY_COUNT:
                    return reverse ? Integer.compare(a.stackSize, b.stackSize)
                        : Integer.compare(b.stackSize, a.stackSize);

                case BY_ORE_DICT:
                    List<String> ore1 = oreNames(a);
                    List<String> ore2 = oreNames(b);

                    return reverse ? compareLists(ore1, ore2) : compareLists(ore2, ore1);
            }
            return 0;
        });

        // Phase 5: rebuild inventory
        while (sorted.size() < wrapper.storageSlots) {
            sorted.add(null);
        }

        for (Map.Entry<ItemStack, Integer> entry : inPlace) {
            sorted.set(entry.getValue(), entry.getKey());
        }

        wrapper.storageItemStackHandler.setSize(wrapper.storageSlots);

        for (int i = 0; i < sorted.size(); i++) {
            wrapper.storageItemStackHandler.insertItem(i, sorted.get(i), false);
        }
    }

    private static List<String> oreNames(ItemStack stack) {
        int[] ids = OreDictionary.getOreIDs(stack);
        List<String> names = new ArrayList<String>();
        for (int id : ids) names.add(OreDictionary.getOreName(id));
        return names;
    }

    private static int compareLists(List<String> a, List<String> b) {
        int size = Math.min(a.size(), b.size());
        for (int i = 0; i < size; i++) {
            int cmp = a.get(i)
                .compareTo(b.get(i));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(a.size(), b.size());
    }

    private static boolean hasMatchingSlot(StorageWrapper wrapper, ItemStack stack) {
        for (int i = 0; i < wrapper.storageSlots; i++) {
            ItemStack inSlot = wrapper.getStackInSlot(i);
            if (ItemHandlerHelper.canItemStacksStack(inSlot, stack)) {
                return true;
            }
        }
        return false;
    }

    public static void transferPlayerInventoryToBackpack(StorageWrapper wrapper, PlayerMainInvWrapper playerInv,
        boolean transferMatched) {
        for (int i = 9; i < playerInv.getSlots(); i++) {
            ItemStack stack = playerInv.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.getItem() instanceof BlockBarrel.ItemBarrel barrel) {

                StorageWrapper other = new StorageWrapper(barrel);
                if (other.equals(wrapper)) continue;
            }

            if (transferMatched && !hasMatchingSlot(wrapper, stack)) {
                continue;
            }

            ItemStack remaining = wrapper.insertItem(stack, false);
            playerInv.setStackInSlot(i, remaining);

        }
    }

    public static void transferBackpackToPlayerInventory(StorageWrapper wrapper, PlayerMainInvWrapper playerInv,
        boolean transferMatched) {
        for (int i = 0; i < wrapper.storageSlots; i++) {
            ItemStack stack = wrapper.getStackInSlot(i);

            for (int j = 9; j < playerInv.getSlots(); j++) {

                if (transferMatched && playerInv.getStackInSlot(j) == null) continue;

                stack = playerInv.insertItem(j, stack, false);
            }

            wrapper.setStackInSlot(i, stack);
        }
    }

    public static void rotated(ItemStackHandler stackHandler, boolean clockwise) {
        ItemStack[] old = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            old[i] = stackHandler.getStackInSlot(i);
        }

        if (clockwise) {
            stackHandler.setStackInSlot(0, old[3]);
            stackHandler.setStackInSlot(1, old[0]);
            stackHandler.setStackInSlot(2, old[1]);

            stackHandler.setStackInSlot(3, old[6]);
            stackHandler.setStackInSlot(4, old[4]);
            stackHandler.setStackInSlot(5, old[2]);

            stackHandler.setStackInSlot(6, old[7]);
            stackHandler.setStackInSlot(7, old[8]);
            stackHandler.setStackInSlot(8, old[5]);
        } else {
            stackHandler.setStackInSlot(0, old[2]);
            stackHandler.setStackInSlot(0, old[1]);
            stackHandler.setStackInSlot(1, old[2]);
            stackHandler.setStackInSlot(2, old[5]);

            stackHandler.setStackInSlot(3, old[0]);
            stackHandler.setStackInSlot(4, old[4]);
            stackHandler.setStackInSlot(5, old[8]);

            stackHandler.setStackInSlot(6, old[3]);
            stackHandler.setStackInSlot(7, old[6]);
            stackHandler.setStackInSlot(8, old[7]);
        }
    }

    public static void balance(ItemStackHandler handler) {

        Map<ItemStack, List<Integer>> groups = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (s == null) continue;

            boolean found = false;
            for (ItemStack key : groups.keySet()) {
                if (ItemHandlerHelper.canItemStacksStack(s, key)) {
                    groups.get(key)
                        .add(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                ItemStack key = s.copy();
                key.stackSize = 1;
                List<Integer> list = new ArrayList<>();
                list.add(i);
                groups.put(key, list);
            }
        }

        for (Map.Entry<ItemStack, List<Integer>> entry : groups.entrySet()) {

            ItemStack target = entry.getKey();
            List<Integer> slots = entry.getValue();
            int slotCount = slots.size();

            if (slotCount <= 1) continue;

            int total = 0;
            for (int slot : slots) {
                total += handler.getStackInSlot(slot).stackSize;
            }

            int base = total / slotCount;
            int remain = total % slotCount;

            for (int i = 0; i < slotCount; i++) {
                int slot = slots.get(i);
                ItemStack old = handler.getStackInSlot(slot);

                int want = base + (i < remain ? 1 : 0);
                int give = Math.min(want, old.getMaxStackSize());

                ItemStack out = target.copy();
                out.stackSize = give;
                handler.setStackInSlot(slot, out);
            }
        }
    }

    public static void spread(ItemStackHandler handler) {

        ItemStack source = null;
        for (int i = 0; i < 9; i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (s != null && s.stackSize > 0) {
                source = s;
                break;
            }
        }
        if (source == null) return;

        List<Integer> slots = new ArrayList<>();
        int totalCount = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (s == null) {
                slots.add(i);
            } else if (ItemHandlerHelper.canItemStacksStack(s, source)) {
                slots.add(i);
                totalCount += s.stackSize;
            }
        }
        if (slots.isEmpty()) return;

        int slotCount = slots.size();
        int base = totalCount / slotCount;
        int remain = totalCount % slotCount;

        for (int i = 0; i < slotCount; i++) {
            int slot = slots.get(i);
            int give = base + (i < remain ? 1 : 0);
            give = Math.min(give, source.getMaxStackSize());

            ItemStack out = source.copy();
            out.stackSize = give;
            handler.setStackInSlot(slot, out);
        }
    }

    public static void clear(StoragePanel panel, ItemStackHandler stackHandler, int ordinal) {
        ICraftingUpgrade.CraftingDestination type = ICraftingUpgrade.CraftingDestination.values()[ordinal];
        PlayerMainInvWrapper playerInv = new PlayerMainInvWrapper(panel.player.inventory);
        switch (type) {
            case INVENTORY:
                for (int i = 0; i < stackHandler.getSlots() - 1; i++) {
                    ItemStack original = stackHandler.getStackInSlot(i);
                    if (original == null || original.stackSize <= 0) continue;

                    ItemStack moving = original.copy();

                    for (int j = 9; j < playerInv.getSlots(); j++) {
                        if (moving == null || moving.stackSize <= 0) break;
                        moving = playerInv.insertItem(j, moving, false);
                    }

                    stackHandler.setStackInSlot(i, moving);
                }
                break;

            case STORAGE:
                for (int i = 0; i < stackHandler.getSlots() - 1; i++) {
                    ItemStack original = stackHandler.getStackInSlot(i);
                    if (original == null || original.stackSize <= 0) continue;

                    ItemStack remaining = panel.wrapper.insertItem(original.copy(), false);

                    stackHandler.setStackInSlot(i, remaining);
                }
                break;
        }
    }
}
