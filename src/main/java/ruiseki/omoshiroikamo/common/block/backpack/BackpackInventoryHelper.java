package ruiseki.omoshiroikamo.common.block.backpack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.cleanroommc.modularui.utils.item.ItemHandlerHelper;
import com.cleanroommc.modularui.utils.item.PlayerMainInvWrapper;

public class BackpackInventoryHelper {

    public static void sortInventory(BackpackHandler handler) {

        for (int i = 0; i < handler.getBackpackSlots() - 1; i++) {

            if (handler.isSlotLocked(i)) continue;

            boolean isMem = handler.isSlotMemorized(i);
            ItemStack baseStack = handler.getStackInSlot(i);
            if (baseStack == null) continue;

            int slotMaxSize = baseStack.getMaxStackSize() * handler.getTotalStackMultiplier();

            for (int j = i + 1; j < handler.getBackpackSlots(); j++) {
                if (isMem != handler.isSlotMemorized(j) || handler.isSlotLocked(j)) continue;

                ItemStack stack = handler.getStackInSlot(j);

                if (!ItemHandlerHelper.canItemStacksStack(baseStack, stack)) continue;

                if (stack == null || stack.stackSize <= 0) continue;

                int diff = Math.min(stack.stackSize, slotMaxSize - baseStack.stackSize);

                if (diff > 0) {
                    baseStack.stackSize += diff;
                    stack.stackSize -= diff;

                    if (stack.stackSize <= 0) {
                        handler.getBackpackHandler()
                            .setStackInSlot(j, null);
                    }
                } else if (diff == 0) break;
            }
        }

        List<ItemStack> sorted = new ArrayList<>();
        List<Map.Entry<ItemStack, Integer>> inPlace = new ArrayList<>();

        for (int i = 0; i < handler.getBackpackSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);

            if (handler.isSlotMemorized(i) || handler.isSlotLocked(i)) {
                inPlace.add(new AbstractMap.SimpleEntry<>(stack, i));
            } else {
                sorted.add(stack);
            }
        }

        sorted.sort((a, b) -> {
            if (a == null || a.stackSize <= 0) {
                if (b == null || b.stackSize <= 0) return 0;
                return 1;
            }
            if (b == null || b.stackSize <= 0) return -1;

            switch (handler.getSortType()) {
                case BY_NAME:
                    return a.getDisplayName()
                        .compareTo(b.getDisplayName());
                case BY_MOD_ID:
                    String aItem = Item.itemRegistry.getNameForObject(a.getItem());
                    String bItem = Item.itemRegistry.getNameForObject(b.getItem());
                    String aDomain = aItem.substring(0, aItem.indexOf(":"));
                    String bDomain = bItem.substring(0, bItem.indexOf(":"));
                    return aDomain.compareTo(bDomain);
                case BY_COUNT:
                    return Integer.compare(a.stackSize, b.stackSize);
                case BY_ORE_DICT:
                    List<String> ore1 = oreNames(a);
                    List<String> ore2 = oreNames(b);
                    return compareLists(ore1, ore2);
            }
            return 0;
        });

        for (Map.Entry<ItemStack, Integer> entry : inPlace) {
            sorted.add(entry.getValue(), entry.getKey());
        }

        handler.getBackpackHandler()
            .setSize(handler.getBackpackSlots());

        for (int i = 0; i < sorted.size(); i++) {
            handler.getBackpackHandler()
                .insertItem(i, sorted.get(i), false);
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

    public static void transferPlayerInventoryToBackpack(BackpackHandler handler, PlayerMainInvWrapper playerInv,
        boolean transferMatched) {
        for (int i = 9; i < playerInv.getSlots(); i++) {
            ItemStack stack = playerInv.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.getItem() instanceof BlockBackpack.ItemBackpack backpack) {

                BackpackHandler other = new BackpackHandler(stack.copy(), null, backpack);
                if (other == handler) continue;
                if (!handler.canNestBackpack()) continue;
            }

            ItemStack remaining = handler.insertItem(stack, false);
            playerInv.setStackInSlot(i, remaining);

        }
    }

    public static void transferBackpackToPlayerInventory(BackpackHandler handler, PlayerMainInvWrapper playerInv,
        boolean transferMatched) {
        for (int i = 0; i < handler.getBackpackSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);

            for (int j = 9; j < playerInv.getSlots(); j++) {

                if (transferMatched && playerInv.getStackInSlot(j) == null) continue;

                stack = playerInv.insertItem(j, stack, false);
            }

            handler.setStackInSlot(i, stack);
        }
    }

}
