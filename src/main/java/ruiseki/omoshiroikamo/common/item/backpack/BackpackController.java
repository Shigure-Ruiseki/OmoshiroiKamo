package ruiseki.omoshiroikamo.common.item.backpack;

import static ruiseki.omoshiroikamo.common.item.backpack.BackpackGui.EVERLASTING_MODE;
import static ruiseki.omoshiroikamo.common.item.backpack.BackpackGui.FEEDING_MODE;
import static ruiseki.omoshiroikamo.common.item.backpack.BackpackGui.FEEDING_TYPE;
import static ruiseki.omoshiroikamo.common.item.backpack.BackpackGui.MAGNET_MODE;
import static ruiseki.omoshiroikamo.plugin.botania.BotaniaUtil.hasSolegnoliaAround;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import codechicken.lib.vec.Vector3;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.client.gui.modularui2.handler.UpgradeItemStackHandler;
import ruiseki.omoshiroikamo.common.entity.EntityImmortalItem;
import ruiseki.omoshiroikamo.common.item.upgrade.EnergyUpgrade;
import ruiseki.omoshiroikamo.common.network.PacketBackPackState;
import ruiseki.omoshiroikamo.common.util.ItemNBTHelper;
import ruiseki.omoshiroikamo.config.item.FeedingConfig;
import ruiseki.omoshiroikamo.config.item.MagnetConfig;
import ruiseki.omoshiroikamo.plugin.baubles.BaublesUtil;

@EventBusSubscriber
public class BackpackController {

    private static final String TAG_COOLDOWN_FEED = "feedingCooldown";
    private static final String TAG_COOLDOWN_MAGNET = "magnetCooldown";
    private static final double COLLISION_DISTANCE_SQ = 1.25 * 1.25;

    private static List<String> feedingFilter = Collections.emptyList();
    private static List<String> magnetFilter = Collections.emptyList();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        EntityPlayer player = event.player;
        if (player.getHealth() <= 0f) {
            return;
        }

        List<ActiveBackPack> backpacks = getAllActiveBackpacks(player);
        if (backpacks.isEmpty()) {
            return;
        }

        for (ActiveBackPack mag : backpacks) {
            ItemStack stack = mag.item;
            initFilter(mag);
            if (hasUpgrade(stack, ItemFeedingUpgrade.class)) {
                handleFeeding(player, mag);
            }
            if (hasUpgrade(stack, ItemMagnetUpgrade.class)) {
                handleMagnet(player, mag);
            }
            if (hasUpgrade(stack, ItemBatteryUpgrade.class)) {
                handleBattery(player, mag);
            }
        }
    }

    @SubscribeEvent
    public static void onTossItemWrapper(ItemTossEvent event) {
        onTossItem(event);
    }

    @SubscribeEvent
    public static void onEnitySpawnWrapper(EntityJoinWorldEvent event) {
        onBackpackSpawn(event);
    }

    private static List<ActiveBackPack> getAllActiveBackpacks(EntityPlayer player) {
        List<ActiveBackPack> result = new ArrayList<>();
        UpgradeItemStackHandler upgradeHandler = new UpgradeItemStackHandler(BackpackGui.upgradeSlot);

        ItemStack[] inv = player.inventory.mainInventory;
        int maxSlot = Math.max(
            FeedingConfig.feedingAllowInMainInventory ? 4 * 9 : 9,
            MagnetConfig.magnetAllowInMainInventory ? 4 * 9 : 9);

        for (int i = 0; i < maxSlot; i++) {
            ItemStack stack = inv[i];
            if (stack == null || !(stack.getItem() instanceof ItemBackpack)) {
                continue;
            }

            NBTTagCompound tag = ItemNBTHelper.getNBT(stack);
            if (!tag.hasKey(BackpackGui.BACKPACK_UPGRADE)) {
                continue;
            }
            upgradeHandler.deserializeNBT(tag.getCompoundTag(BackpackGui.BACKPACK_UPGRADE));

            for (int slot = 0; slot < upgradeHandler.getSlots(); slot++) {
                ItemStack upgrade = upgradeHandler.getStackInSlot(slot);
                if (upgrade != null && (upgrade.getItem() instanceof ItemFeedingUpgrade
                    || upgrade.getItem() instanceof ItemMagnetUpgrade)) {
                    result.add(new ActiveBackPack(stack, slot));
                    break;
                }
            }
        }

        if (FeedingConfig.feedingAllowInBaublesSlot || MagnetConfig.magnetAllowInBaublesSlot) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ItemBackpack)) {
                    continue;
                }

                NBTTagCompound tag = ItemNBTHelper.getNBT(stack);
                if (!tag.hasKey(BackpackGui.BACKPACK_UPGRADE)) {
                    continue;
                }
                upgradeHandler.deserializeNBT(tag.getCompoundTag(BackpackGui.BACKPACK_UPGRADE));

                for (int slot = 0; slot < upgradeHandler.getSlots(); slot++) {
                    ItemStack upgrade = upgradeHandler.getStackInSlot(slot);
                    if (upgrade != null && (upgrade.getItem() instanceof ItemFeedingUpgrade
                        || upgrade.getItem() instanceof ItemMagnetUpgrade)) {
                        result.add(new ActiveBackPack(stack, slot));
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static boolean hasUpgrade(ItemStack stack, Class<?> clazz) {
        NBTTagCompound tag = ItemNBTHelper.getNBT(stack);
        if (tag == null || !tag.hasKey(BackpackGui.BACKPACK_UPGRADE)) {
            return false;
        }
        UpgradeItemStackHandler upgradeHandler = new UpgradeItemStackHandler(BackpackGui.upgradeSlot);
        upgradeHandler.deserializeNBT(tag.getCompoundTag(BackpackGui.BACKPACK_UPGRADE));
        for (int i = 0; i < upgradeHandler.getSlots(); i++) {
            ItemStack upgrade = upgradeHandler.getStackInSlot(i);
            if (upgrade != null && clazz.isInstance(upgrade.getItem())) {
                return true;
            }
        }
        return false;
    }

    private static void handleFeeding(EntityPlayer player, ActiveBackPack mag) {
        int cooldown = ItemNBTHelper.getInt(mag.item, TAG_COOLDOWN_FEED, 2);

        if (cooldown > 0) {
            ItemNBTHelper.setInt(mag.item, TAG_COOLDOWN_FEED, cooldown - 1);
            return;
        }

        NBTTagCompound tag = ItemNBTHelper.getNBT(mag.item);
        if (tag == null || !tag.hasKey(BackpackGui.BACKPACK_INV)) {
            return;
        }

        ItemStackHandler handler = new ItemStackHandler(BackpackGui.slot);
        handler.deserializeNBT(tag.getCompoundTag(BackpackGui.BACKPACK_INV));

        FoodStats foodStats = player.getFoodStats();
        boolean consumedAny = false;
        int missing = 20 - foodStats.getFoodLevel();
        if (missing <= 0) {
            return;
        }

        boolean filterMode = tag.getBoolean(FEEDING_MODE);
        boolean feedingType = tag.getBoolean(FEEDING_TYPE);

        List<Integer> foodSlots = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack s = handler.getStackInSlot(i);
            if (s == null || !(s.getItem() instanceof ItemFood)) {
                continue;
            }

            if (!feedingFilter.isEmpty()) {
                GameRegistry.UniqueIdentifier uid = GameRegistry.findUniqueIdentifierFor(s.getItem());
                if (uid == null) {
                    continue;
                }

                String id = uid.modId + ":" + uid.name + ":" + s.getItemDamage();
                boolean contained = feedingFilter.contains(id);

                if ((filterMode && contained) || (!filterMode && !contained)) {
                    continue;
                }
            }

            foodSlots.add(i);
        }

        foodSlots.sort(
            Comparator.comparingInt(
                i -> ((ItemFood) handler.getStackInSlot(i)
                    .getItem()).func_150905_g(handler.getStackInSlot(i))));

        for (int slotIndex : foodSlots) {
            ItemStack stack = handler.getStackInSlot(slotIndex);
            if (stack == null) {
                continue;
            }

            ItemFood food = (ItemFood) stack.getItem();

            while (stack.stackSize > 0 && foodStats.getFoodLevel() < 20) {
                int heal = food.func_150905_g(stack);
                int before = foodStats.getFoodLevel();
                int missingNow = 20 - before;

                if (feedingType) {
                    if (heal > missingNow + 1) {
                        break;
                    }
                }

                food.onEaten(stack, player.worldObj, player);
                consumedAny = true;

                if (stack.stackSize <= 0) {
                    handler.setStackInSlot(slotIndex, null);
                    break;
                } else {
                    handler.setStackInSlot(slotIndex, stack);
                }

                if (!feedingType) {
                    continue;
                }
            }

            if (foodStats.getFoodLevel() >= 20) {
                break;
            }
        }

        if (consumedAny) {
            tag.setTag(BackpackGui.BACKPACK_INV, handler.serializeNBT());
            mag.item.setTagCompound(tag);
            ItemNBTHelper.setInt(mag.item, TAG_COOLDOWN_FEED, 100);
        }
    }

    public static void handleBattery(EntityPlayer player, ActiveBackPack mag) {
        if (player.worldObj.isRemote) {
            return;
        }

        EnergyUpgrade energyUpgrade = EnergyUpgrade.loadFromItem(mag.item);
        if (energyUpgrade == null) {
            return;
        }

        int energyStored = EnergyUpgrade.getEnergyStored(mag.item);
        if (energyStored <= 0) {
            return;
        }

        int energyPerTick = energyUpgrade.getCapacity() / 100;
        int remainingToDistribute = Math.min(energyStored, energyPerTick);

        List<ItemStack> stacks = new ArrayList<>();

        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack == null || stack == mag.item || !(stack.getItem() instanceof IEnergyContainerItem)) {
                continue;
            }
            stacks.add(stack);
        }

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || stack == mag.item || !(stack.getItem() instanceof IEnergyContainerItem)) {
                continue;
            }
            stacks.add(stack);
        }

        IInventory baubles = PlayerHandler.getPlayerBaubles(player);
        if (baubles != null) {
            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack == null || stack == mag.item || !(stack.getItem() instanceof IEnergyContainerItem)) {
                    continue;
                }
                stacks.add(stack);
            }
        }
        for (ItemStack stack : stacks) {
            if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
                int received = energyItem.receiveEnergy(stack, remainingToDistribute, false);
                if (received > 0) {
                    energyUpgrade.extractEnergy(received, false);
                    remainingToDistribute -= received;
                    if (remainingToDistribute <= 0) {
                        break;
                    }
                }
            }
        }

        energyUpgrade.writeToItem(mag.item);
    }

    private static void handleMagnet(EntityPlayer player, ActiveBackPack mag) {
        int cooldown = ItemNBTHelper.getInt(mag.item, TAG_COOLDOWN_MAGNET, 2);

        if (cooldown > 0) {
            ItemNBTHelper.setInt(mag.item, TAG_COOLDOWN_MAGNET, cooldown - 1);
            return;
        }

        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
            player.posX - MagnetConfig.magnetRange,
            player.posY - MagnetConfig.magnetRange,
            player.posZ - MagnetConfig.magnetRange,
            player.posX + MagnetConfig.magnetRange,
            player.posY + MagnetConfig.magnetRange,
            player.posZ + MagnetConfig.magnetRange);

        List<Entity> entities = selectEntitiesWithinAABB(player.worldObj, aabb, mag);
        if (entities.isEmpty()) {
            return;
        }

        int pulled = 0;
        for (Entity entity : entities) {
            double dx = player.posX + 0.5D - entity.posX;
            double dy = player.posY + 1D - entity.posY;
            double dz = player.posZ + 0.5D - entity.posZ;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < COLLISION_DISTANCE_SQ) {
                entity.onCollideWithPlayer(player);
            } else {
                if (pulled++ > 200) {
                    break;
                }
                Vector3 target = new Vector3(
                    player.posX,
                    player.posY - (player.worldObj.isRemote ? 1.62 : 0) + 0.75,
                    player.posZ);
                setEntityMotionFromVector(entity, target, 0.45F);
            }
        }

        ItemNBTHelper.setInt(mag.item, TAG_COOLDOWN_MAGNET, 2);
    }

    private static void initFilter(ActiveBackPack mag) {
        if (mag == null || mag.item == null) {
            feedingFilter = Collections.emptyList();
            magnetFilter = Collections.emptyList();
            return;
        }

        List<String> feedList = getFilter(mag.item, BackpackGui.FEEDING_FILTER);
        if (feedList.isEmpty()) {
            feedingFilter = Collections.emptyList();
        } else {
            Set<String> feedSet = new HashSet<>(feedList);
            feedingFilter = new ArrayList<>(feedSet);
        }

        Set<String> magSet = new HashSet<>();
        List<String> magFromItem = getFilter(mag.item, BackpackGui.MAGNET_FILTER);
        if (!magFromItem.isEmpty()) {
            magSet.addAll(magFromItem);
        }

        if (magSet.isEmpty()) {
            magnetFilter = Collections.emptyList();
        } else {
            magnetFilter = new ArrayList<>(magSet);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Entity> selectEntitiesWithinAABB(World world, AxisAlignedBB bb, ActiveBackPack mag) {
        List<Entity> result = new ArrayList<>();
        int remaining = MagnetConfig.magnetMaxItems <= 0 ? Integer.MAX_VALUE : MagnetConfig.magnetMaxItems;

        NBTTagCompound root = mag.item.getTagCompound();
        boolean filterMode = root.getBoolean(MAGNET_MODE);

        int minChunkX = MathHelper.floor_double(bb.minX / 16.0D);
        int maxChunkX = MathHelper.floor_double(bb.maxX / 16.0D);
        int minChunkZ = MathHelper.floor_double(bb.minZ / 16.0D);
        int maxChunkZ = MathHelper.floor_double(bb.maxZ / 16.0D);
        int minChunkY = MathHelper.floor_double(bb.minY / 16.0D);
        int maxChunkY = MathHelper.floor_double(bb.maxY / 16.0D);

        for (int cx = minChunkX; cx <= maxChunkX; ++cx) {
            for (int cz = minChunkZ; cz <= maxChunkZ; ++cz) {
                Chunk chunk = world.getChunkFromChunkCoords(cx, cz);
                int minY = MathHelper.clamp_int(minChunkY, 0, chunk.entityLists.length - 1);
                int maxY = MathHelper.clamp_int(maxChunkY, 0, chunk.entityLists.length - 1);

                for (int cy = minY; cy <= maxY; ++cy) {
                    for (Entity entity : (List<Entity>) chunk.entityLists[cy]) {
                        if (entity.isDead) {
                            continue;
                        }

                        boolean shouldPull = false;

                        if (entity instanceof EntityItem) {
                            ItemStack stack = ((EntityItem) entity).getEntityItem();
                            boolean intersects = entity.boundingBox.intersectsWith(bb);
                            boolean hasSolegnolia = hasSolegnoliaAround(entity);
                            boolean contained = false;

                            if (!magnetFilter.isEmpty()) {
                                GameRegistry.UniqueIdentifier uid = GameRegistry
                                    .findUniqueIdentifierFor(stack.getItem());
                                if (uid != null) {
                                    String id = uid.modId + ":" + uid.name + ":" + stack.getItemDamage();
                                    contained = magnetFilter.contains(id);
                                }
                            }

                            shouldPull = intersects && !hasSolegnolia
                                && (magnetFilter.isEmpty() || (filterMode ? contained : !contained));
                        } else if (entity instanceof EntityXPOrb) {
                            shouldPull = entity.boundingBox.intersectsWith(bb);
                        }

                        if (shouldPull) {
                            result.add(entity);

                            if (--remaining <= 0) {
                                return result;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private static void setEntityMotionFromVector(Entity entity, Vector3 target, float modifier) {
        Vector3 current = Vector3.fromEntityCenter(entity);
        Vector3 motion = target.copy()
            .subtract(current);

        if (motion.mag() > 1) {
            motion.normalize();
        }

        entity.motionX = motion.x * modifier;
        entity.motionY = motion.y * modifier;
        entity.motionZ = motion.z * modifier;
    }

    private static void onTossItem(ItemTossEvent event) {
        ItemStack[] inv = event.player.inventory.mainInventory;
        for (ItemStack stack : inv) {
            if (stack == null || !(stack.getItem() instanceof ItemBackpack)) {
                continue;
            }
            ItemNBTHelper.setInt(stack, TAG_COOLDOWN_MAGNET, 100);
        }

        InventoryBaubles baubleInv = PlayerHandler.getPlayerBaubles(event.player);
        for (int i = 0; i < baubleInv.getSizeInventory(); i++) {
            ItemStack stack = baubleInv.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBackpack) {
                ItemNBTHelper.setInt(stack, TAG_COOLDOWN_MAGNET, 100);
                baubleInv.markDirty();
            }
        }
    }

    private static void onBackpackSpawn(EntityJoinWorldEvent event) {
        if (event.entity.worldObj.isRemote) {
            return;
        }
        if (!(event.entity instanceof EntityItem entityItem)) {
            return;
        }

        ItemStack stack = entityItem.getEntityItem();
        if (stack == null) {
            return;
        }

        if (!(stack.getItem() instanceof ItemBackpack)) {
            return;
        }

        boolean mode = ItemNBTHelper.getBoolean(stack, EVERLASTING_MODE, false);

        if (entityItem instanceof EntityImmortalItem immortalItem) {
            immortalItem.setImmortal(mode);
        }
    }

    public static class ActiveBackPack {

        public ItemStack item;
        public int slot;

        public ActiveBackPack(ItemStack item, int slot) {
            this.item = item;
            this.slot = slot;
        }
    }

    public static void setBackpackActive(EntityPlayerMP player, PacketBackPackState.SlotType type, int slot,
        boolean isActive) {
        ItemStack stack = null;
        IInventory baubles = null;
        int dropOff = -1;
        switch (type) {
            case INVENTORY:
                stack = player.inventory.getStackInSlot(slot);
                break;
            case ARMOR:
                return;
            case BAUBLES:
                baubles = BaublesUtil.instance()
                    .getBaubles(player);
                if (baubles != null) {
                    stack = baubles.getStackInSlot(slot);
                }
                break;
        }
        if (stack == null || stack.getItem() == null) {
            return;
        }
        if (type == PacketBackPackState.SlotType.BAUBLES && !isActive) {
            ItemStack[] inv = player.inventory.mainInventory;
            for (int i = 0; i < inv.length && dropOff < 0; i++) {
                if (inv[i] == null) {
                    dropOff = i;
                }
            }
            if (dropOff < 0) {
                return;
            }
        }
        switch (type) {
            case INVENTORY:
                player.inventory.setInventorySlotContents(slot, stack);
                player.inventory.markDirty();
                break;
            case ARMOR:
                return;
            case BAUBLES:
                if (dropOff < 0) {
                    baubles.setInventorySlotContents(slot, stack);
                } else {
                    baubles.setInventorySlotContents(slot, null);
                    player.inventory.setInventorySlotContents(dropOff, stack);
                }
                player.inventory.markDirty();
                break;
        }
    }

    private static List<String> getFilter(ItemStack magnet, String tag) {
        List<String> filterList = new ArrayList<>();
        if (magnet != null && magnet.hasTagCompound()
            && magnet.getTagCompound()
                .hasKey(tag)) {
            NBTTagList list = magnet.getTagCompound()
                .getTagList(tag, 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound entry = list.getCompoundTagAt(i);
                if (entry.hasKey("id")) {
                    String id = entry.getString("id");
                    int meta = entry.hasKey("meta") ? entry.getInteger("meta") : 0;
                    filterList.add(id + ":" + meta);
                }
            }
        }
        return filterList;
    }

}
