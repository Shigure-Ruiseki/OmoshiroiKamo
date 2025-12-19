package ruiseki.omoshiroikamo.common.recipe.quantumExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedRandomUtil;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;

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
        List<WeightedStackBase> weightedCopy = getUnFocusedList();

        List<ItemStack> focusList = this.valuesOfFocus(focusColor);
        if (focusList.isEmpty()) return weightedCopy;

        HashSet<ItemStack> focusSet = new HashSet<>(focusList);

        int totalFocusWeight = 0;
        int totalUnfocusWeight = 0;

        for (WeightedStackBase stack : weightedCopy) {
            if (stack == null) continue;
            if (focusSet.stream()
                .anyMatch(stack::isStackEqual)) {
                totalFocusWeight += stack.itemWeight;
            } else {
                totalUnfocusWeight += stack.itemWeight;
            }
        }

        if (totalFocusWeight <= 0) return weightedCopy;

        float groupBoost = Math.min(Math.abs(boostMultiplier), 2f);
        int boostedFocusTotal = Math.min((int) Math.ceil(totalFocusWeight * (1 + groupBoost)), 100);
        int remainingUnfocusTotal = 100 - boostedFocusTotal;

        for (WeightedStackBase stack : weightedCopy) {
            if (stack == null) continue;
            boolean isFocus = focusSet.stream()
                .anyMatch(f -> stack.isStackEqual(f));
            if (isFocus) {
                stack.itemWeight = Math
                    .max(1, (int) Math.ceil(stack.itemWeight / (float) totalFocusWeight * boostedFocusTotal));
            } else {
                stack.itemWeight = Math
                    .max(1, (int) Math.ceil(stack.itemWeight / (float) totalUnfocusWeight * remainingUnfocusTotal));
            }
        }

        return weightedCopy;
    }

    @Override
    public List<WeightedStackBase> getUnFocusedList() {
        ArrayList<WeightedStackBase> weightedCopy = new ArrayList<>(this.oreStacks.size());
        WeightedRandomUtil.copyWSList(weightedCopy, this.oreStacks);

        int totalWeight = 0;
        for (WeightedStackBase ws : weightedCopy) {
            if (ws != null) totalWeight += ws.itemWeight;
        }
        if (totalWeight <= 0) return weightedCopy;

        final float targetTotal = 100f;
        for (WeightedStackBase ws : weightedCopy) {
            if (ws == null) continue;
            ws.itemWeight = Math.max(1, (int) Math.ceil(ws.itemWeight / (float) totalWeight * targetTotal));
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
