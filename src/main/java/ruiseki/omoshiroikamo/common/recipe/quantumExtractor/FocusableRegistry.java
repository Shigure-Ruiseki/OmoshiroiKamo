package ruiseki.omoshiroikamo.common.recipe.quantumExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.ItemUtil;

import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.WeightedRandomUtil;
import ruiseki.omoshiroikamo.api.item.WeightedStackBase;

public class FocusableRegistry implements IFocusableRegistry {

    private final ArrayList<WeightedStackBase> oreStacks = new ArrayList<WeightedStackBase>();
    private final HashMap<DyeColor, List<ItemStack>> priorityOres = new HashMap<DyeColor, List<ItemStack>>();

    public FocusableRegistry() {
        for (DyeColor color : DyeColor.values()) {
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
    public boolean addResource(WeightedStackBase stack, DyeColor prioritized) {
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

    private List<ItemStack> valuesOfFocus(DyeColor focus) {
        List<ItemStack> result = this.priorityOres.get(focus);
        return result == null ? new ArrayList<ItemStack>() : result;
    }

    @Override
    public List<WeightedStackBase> getFocusedList(DyeColor focusColor, float boostMultiplier) {
        ArrayList<WeightedStackBase> weightedCopy = new ArrayList<WeightedStackBase>(this.oreStacks.size());
        WeightedRandomUtil.copyWSList(weightedCopy, this.oreStacks);

        List<ItemStack> focusList = this.valuesOfFocus(focusColor);

        int totalWeight = WeightedRandom.getTotalWeight(this.oreStacks);
        int boost = this.oreStacks.isEmpty() ? 0 : totalWeight / Math.max(1, (this.oreStacks.size() / 2));

        for (ItemStack focusedItem : focusList) {
            for (WeightedStackBase stack : weightedCopy) {
                if (stack.isStackEqual(focusedItem)) {
                    int weight = stack.getWeight();
                    weight += (int) (boost * MathHelper.abs(boostMultiplier));
                    stack.itemWeight = weight;
                }
            }
        }

        return weightedCopy;
    }

    @Override
    public List<WeightedStackBase> getUnFocusedList() {
        return this.oreStacks;
    }

    @Override
    public DyeColor getPrioritizedLens(ItemStack stack) {
        if (stack == null) {
            return null;
        }

        for (DyeColor dye : this.priorityOres.keySet()) {
            List<ItemStack> list = this.priorityOres.get(dye);
            if (list != null) {
                for (ItemStack item : list) {
                    if (item != null && ItemUtil.areStacksEqual(item, stack)) {
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
