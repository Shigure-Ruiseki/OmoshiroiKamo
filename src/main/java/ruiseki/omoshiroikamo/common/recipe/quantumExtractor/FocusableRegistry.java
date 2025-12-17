package ruiseki.omoshiroikamo.common.recipe.quantumExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedRandomUtil;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;
import ruiseki.omoshiroikamo.common.util.item.ItemUtils;

public class FocusableRegistry implements IFocusableRegistry {

    private final ArrayList<WeightedStackBase> oreStacks = new ArrayList<WeightedStackBase>();
    private final HashMap<EnumDye, List<ItemStack>> priorityOres = new HashMap<EnumDye, List<ItemStack>>();

    public FocusableRegistry() {
        for (EnumDye color : EnumDye.values()) {
            this.priorityOres.put(color, new ArrayList<ItemStack>());
        }
    }

    @Override
    public boolean hasResource(ItemStack other) {
        if (other == null) {
            return false;
        }

        for (WeightedStackBase stack : this.oreStacks) {
            if (stack != null && stack.isStackEqual(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addResource(WeightedStackBase stack, EnumDye prioritized) {
        if (stack == null || stack.getMainStack() == null) {
            return false;
        }

        if (!this.hasResource(stack.getMainStack())) {
            this.oreStacks.add(stack);
            List<ItemStack> stacks = this.priorityOres.get(prioritized);
            if (stacks != null) {
                stacks.add(stack.getMainStack());
            }
            return true;
        }
        return false;
    }

    private List<ItemStack> valuesOfFocus(EnumDye focus) {
        List<ItemStack> result = this.priorityOres.get(focus);
        return result == null ? new ArrayList<ItemStack>() : result;
    }

    @Override
    public List<WeightedStackBase> getFocusedList(EnumDye focusColor, float boostMultiplier) {
        // Create copies
        List<WeightedStackBase> weightedCopy = new ArrayList<>(this.oreStacks.size());
        WeightedRandomUtil.copyWSList(weightedCopy, this.oreStacks);

        List<ItemStack> focusList = this.valuesOfFocus(focusColor);
        HashSet<ItemStack> focusSet = new HashSet<>(focusList);

        double totalWeight = 0;

        // First pass: Calculate Total Effective Weight
        for (WeightedStackBase stack : weightedCopy) {
            if (stack == null) continue;
            boolean isFocus = !focusSet.isEmpty() && focusSet.stream()
                .anyMatch(stack::isStackEqual);

            // Use focusedWeight if matched, otherwise use normal weight
            // Temporarily store effective weight in realFocusedWeight to avoid re-checking
            stack.realFocusedWeight = isFocus ? stack.realFocusedWeight : stack.realWeight;
            totalWeight += stack.realFocusedWeight;
        }

        if (totalWeight <= 0) return weightedCopy;

        // Second pass: Calculate Percentage
        for (WeightedStackBase stack : weightedCopy) {
            if (stack == null) continue;
            // Calculate percentage (0-100)
            stack.realWeight = (stack.realFocusedWeight / totalWeight) * 100.0;
            // Update int weight for compatibility (although likely unused by new Handler)
            stack.itemWeight = (int) stack.realWeight;
        }

        return weightedCopy;
    }

    @Override
    public List<WeightedStackBase> getUnFocusedList() {
        ArrayList<WeightedStackBase> weightedCopy = new ArrayList<>(this.oreStacks.size());
        WeightedRandomUtil.copyWSList(weightedCopy, this.oreStacks);

        double totalWeight = 0;
        for (WeightedStackBase ws : weightedCopy) {
            if (ws != null) totalWeight += ws.realWeight;
        }
        if (totalWeight <= 0) return weightedCopy;

        for (WeightedStackBase ws : weightedCopy) {
            if (ws == null) continue;
            // Calculate percentage
            ws.realWeight = (ws.realWeight / totalWeight) * 100.0;
            ws.itemWeight = (int) ws.realWeight;
        }

        return weightedCopy;
    }

    @Override
    public EnumDye getPrioritizedLens(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        for (EnumDye dye : this.priorityOres.keySet()) {
            List<ItemStack> list = this.priorityOres.get(dye);
            if (list != null) {
                for (ItemStack item : list) {
                    if (item != null && ItemUtils.areStacksEqual(item, stack)) {
                        return dye;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public WeightedStackBase getWeightedStack(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        for (WeightedStackBase weighted : this.oreStacks) {
            if (weighted != null && weighted.isStackEqual(stack)) {
                return weighted;
            }
        }
        return null;
    }
}
