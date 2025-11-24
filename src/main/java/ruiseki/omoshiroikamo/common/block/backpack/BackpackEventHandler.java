package ruiseki.omoshiroikamo.common.block.backpack;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.container.BackPackContainer;
import ruiseki.omoshiroikamo.common.util.item.BaublesUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

@EventBusSubscriber
public class BackpackEventHandler {

    private static int feedTickCounter = 0;

    @SubscribeEvent
    public static void onPlayerPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.entityPlayer;
        IInventory inventory = player.inventory;
        ItemStack stack = event.item.getEntityItem()
            .copy();

        if (LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            stack = attemptPickup(baublesInventory, stack);
        }

        stack = attemptPickup(inventory, stack);
        if (stack == null || stack.stackSize <= 0) {
            event.item.setDead();
            event.setCanceled(true);

            World world = event.item.worldObj;

            world.playSoundEffect(
                event.item.posX,
                event.item.posY,
                event.item.posZ,
                "random.pop",
                0.2F,
                ((player.getRNG()
                    .nextFloat()
                    - player.getRNG()
                        .nextFloat())
                    * 0.7F + 1.0F) * 2.0F);
            return;
        } else if (stack.stackSize != event.item.getEntityItem().stackSize) {
            event.item.setDead();
            event.setCanceled(true);

            World world = event.item.worldObj;

            EntityItem newItem = new EntityItem(world, event.item.posX, event.item.posY, event.item.posZ, stack);

            newItem.delayBeforeCanPickup = 0;
            world.spawnEntityInWorld(newItem);
        }

    }

    private static ItemStack attemptPickup(IInventory targetInventory, ItemStack stack) {

        for (int i = 0; i < targetInventory.getSizeInventory(); i++) {
            ItemStack backpackStack = targetInventory.getStackInSlot(i);
            if (backpackStack == null || backpackStack.stackSize <= 0) {
                continue;
            }

            if (!(backpackStack.getItem() instanceof BlockBackpack.ItemBackpack backpack)) {
                continue;
            }

            BackpackHandler handler = new BackpackHandler(backpackStack, null, backpack);

            if (!handler.canPickupItem(stack)) {
                continue;
            }

            int slotIndex = 0;
            while (stack != null && slotIndex < handler.getSlots()) {
                stack = handler.getBackpackHandler()
                    .prioritizedInsertion(slotIndex, stack, false);
                slotIndex++;
            }

            if (stack == null) {
                break;
            }
        }

        return stack;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof BlockBackpack.ItemBackpack) {
            feedTickCounter = -100;
            return;
        }

        if (player.openContainer instanceof BackPackContainer) {
            feedTickCounter = -100;
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
