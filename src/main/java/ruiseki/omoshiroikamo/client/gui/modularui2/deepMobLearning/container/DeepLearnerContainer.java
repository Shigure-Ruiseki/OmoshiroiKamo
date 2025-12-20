package ruiseki.omoshiroikamo.client.gui.modularui2.deepMobLearning.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.inventory.ClickType;
import com.cleanroommc.modularui.screen.ModularContainer;
import com.cleanroommc.modularui.utils.Platform;

public class DeepLearnerContainer extends ModularContainer {

    private final Integer backpackSlotIndex;

    public DeepLearnerContainer(Integer backpackSlotIndex) {
        this.backpackSlotIndex = backpackSlotIndex;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int mode, EntityPlayer player) {
        ClickType clickTypeIn = ClickType.fromNumber(mode);

        if (clickTypeIn == ClickType.SWAP && mouseButton >= 0
            && mouseButton < 9
            && backpackSlotIndex != null
            && backpackSlotIndex == mouseButton) {
            return Platform.EMPTY_STACK;
        }

        return super.slotClick(slotId, mouseButton, mode, player);
    }
}
