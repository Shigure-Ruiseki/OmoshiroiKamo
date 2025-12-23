package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.handler;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.client.gui.modularui2.base.handler.ItemStackHandlerBase;
import ruiseki.omoshiroikamo.common.item.deepMobLearning.ItemTrialKey;

public class ItemHandlerTrialKey extends ItemStackHandlerBase {

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemTrialKey && super.isItemValid(slot, stack);
    }
}
