package ruiseki.omoshiroikamo.module.backpack.common.block;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import org.joml.Vector3d;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.omoshiroikamo.api.item.BaublesUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.backport.BackpackConfig;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.backpack.client.gui.container.BackPackContainer;

@EventBusSubscriber
public class BackpackEventHandler {

    private static int feedTickCounter = 0;
    private static int magnetTickCounter = 0;

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.useEnvironmentalTech;
    }

    @SubscribeEvent
    public static void onPlayerPickup(EntityItemPickupEvent event) {
        EntityPlayer player = event.entityPlayer;
        IInventory inventory = player.inventory;
        ItemStack stack = event.item.getEntityItem()
            .copy();

        if (player.openContainer instanceof BackPackContainer) {
            return;
        }

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

            BackpackHandler handler = new BackpackHandler(backpackStack.copy(), null, backpack);

            if (!handler.canPickupItem(stack)) {
                continue;
            }

            stack = handler.insertItem(stack, false);

            backpackStack.setTagCompound(
                handler.getBackpack()
                    .getTagCompound());
            if (stack == null) {
                break;
            }
        }

        return stack;
    }

    @SubscribeEvent
    public static void onPlayerTickFeed(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (player.openContainer instanceof BackPackContainer) {
            return;
        }

        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof BlockBackpack.ItemBackpack) {
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

            BackpackHandler handler = new BackpackHandler(stack.copy(), null, backpack);

            ItemStack feedingStack = handler.getFeedingStack(
                player.getFoodStats()
                    .getFoodLevel(),
                player.getHealth(),
                player.getMaxHealth());

            if (feedingStack == null || feedingStack.stackSize <= 0) {
                continue;
            }

            feedingStack.onFoodEaten(player.worldObj, player);

            stack.setTagCompound(
                handler.getBackpack()
                    .getTagCompound());
            return true;
        }

        return false;
    }

    @SubscribeEvent
    public static void onPlayerTickMagnet(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        magnetTickCounter++;
        if (magnetTickCounter % 2 == 0) {
            magnetTickCounter = 0;
            attemptMagnet(player);
        }
    }

    public static void attemptMagnet(EntityPlayer player) {
        boolean result = false;

        if (LibMods.Baubles.isLoaded()) {
            IInventory baublesInventory = BaublesUtils.instance()
                .getBaubles(player);
            result = attemptMagnet(player, baublesInventory);
        }

        if (!result) {
            attemptMagnet(player, player.inventory);
        }
    }

    public static boolean attemptMagnet(EntityPlayer player, IInventory searchInventory) {
        int size = searchInventory.getSizeInventory();

        for (int i = 0; i < size; i++) {
            ItemStack stack = searchInventory.getStackInSlot(i);
            if (stack == null || stack.stackSize <= 0) {
                continue;
            }

            if (!(stack.getItem() instanceof BlockBackpack.ItemBackpack backpack)) {
                continue;
            }

            BackpackHandler handler = new BackpackHandler(stack.copy(), null, backpack);

            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                player.posX - BackpackConfig.magnetConfig.magnetRange,
                player.posY - BackpackConfig.magnetConfig.magnetRange,
                player.posZ - BackpackConfig.magnetConfig.magnetRange,
                player.posX + BackpackConfig.magnetConfig.magnetRange,
                player.posY + BackpackConfig.magnetConfig.magnetRange,
                player.posZ + BackpackConfig.magnetConfig.magnetRange);

            List<Entity> entities = handler.getMagnetEntities(player.worldObj, aabb);
            if (entities.isEmpty()) {
                return false;
            }
            int pulled = 0;
            for (Entity entity : entities) {
                if (pulled++ > 200) {
                    break;
                }
                Vector3d target = new Vector3d(
                    player.posX,
                    player.posY - (player.worldObj.isRemote ? 1.62 : 0) + 0.75,
                    player.posZ);
                setEntityMotionFromVector(entity, target, 0.45F);
            }

            return true;
        }

        return false;
    }

    private static void setEntityMotionFromVector(Entity entity, Vector3d target, float modifier) {
        Vector3d current = fromEntityCenter(entity);

        Vector3d motion = new Vector3d(target.x - current.x, target.y - current.y, target.z - current.z);

        if (motion.length() > 1.0) {
            motion.normalize();
        }

        entity.motionX = motion.x * modifier;
        entity.motionY = motion.y * modifier;
        entity.motionZ = motion.z * modifier;
    }

    public static Vector3d fromEntityCenter(Entity e) {
        return new Vector3d(e.posX, e.posY - e.yOffset + e.height / 2.0, e.posZ);
    }
}
