package ruiseki.omoshiroikamo.core.item.weighted;

import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.EnumDye;

public interface IFocusableRegistry {

    List<WeightedStackBase> getFocusedList(EnumDye var1, float var2);

    List<WeightedStackBase> getUnFocusedList();

    boolean hasResource(ItemStack var1);

    boolean addResource(WeightedStackBase var1, EnumDye var2);

    EnumDye getPrioritizedLens(ItemStack var1);

    WeightedStackBase getWeightedStack(ItemStack var1);
}
