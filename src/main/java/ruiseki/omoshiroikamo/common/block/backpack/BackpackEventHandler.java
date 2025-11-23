package ruiseki.omoshiroikamo.common.block.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.omoshiroikamo.client.gui.modularui2.container.BackPackContainer;
import ruiseki.omoshiroikamo.common.util.item.BaublesUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

@EventBusSubscriber
public class BackpackEventHandler {

    private static int feedTickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (player.openContainer instanceof BackPackContainer) {
            return;
        }

        feedTickCounter++;
        if (feedTickCounter % 20 == 0) {
            feedTickCounter = 0;
            if (!player.capabilities.isCreativeMode) {
                attemptFeed(player);
            }
        }
    }

    public static void attemptFeed(EntityPlayer player) {
        boolean result = false;

        if (LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            result = attemptFeed(player, baublesInventory);
        }

        if (!result) {
            attemptFeed(player, player.inventory);
        }
    }

    public static boolean attemptFeed(EntityPlayer player, IInventory searchInventory) {
        int size = searchInventory.getSizeInventory();

        for (int i = 0; i < size; i++) {
            ItemStack stack = searchInventory.getStackInSlot(i);
            if (stack == null || stack.stackSize <= 0) {
                continue;
            }

            if (!(stack.getItem() instanceof BlockBackpack.ItemBackpack backpack)) {
                continue;
            }

            BackpackHandler handler = new BackpackHandler(stack, null, backpack);

            ItemStack feedingStack = handler.getFeedingStack(
                player.getFoodStats()
                    .getFoodLevel(),
                player.getHealth(),
                player.getMaxHealth());

            if (feedingStack == null || feedingStack.stackSize <= 0) {
                continue;
            }

            feedingStack.onFoodEaten(player.worldObj, player);
            return true;
        }

        return false;
    }
}
