package ruiseki.omoshiroikamo.module.storage.client.gui.slot;

import net.minecraft.entity.player.EntityPlayer;

import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

public class LockedPlayerSlot extends ModularSlot {

    public LockedPlayerSlot(IItemHandler itemHandler, int index) {
        super(itemHandler, index);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}
