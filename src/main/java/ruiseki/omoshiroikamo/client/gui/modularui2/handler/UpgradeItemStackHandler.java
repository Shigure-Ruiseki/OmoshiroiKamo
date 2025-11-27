package ruiseki.omoshiroikamo.client.gui.modularui2.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;

public class UpgradeItemStackHandler extends ItemStackHandler {

    public UpgradeItemStackHandler(int size) {
        super(size);
    }

    public void resize(int newSize) {
        List<ItemStack> newStacks = new ArrayList<>(newSize);

        for (int i = 0; i < newSize; i++) {
            if (i < stacks.size()) {
                newStacks.add(stacks.get(i));
            } else {
                newStacks.add(null);
            }
        }

        this.stacks = newStacks;
    }
}
