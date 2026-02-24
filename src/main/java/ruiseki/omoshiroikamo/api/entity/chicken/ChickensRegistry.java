package ruiseki.omoshiroikamo.api.entity.chicken;

import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.entity.BaseRegistry;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

/**
 * Central registry for all {@link ChickensRegistryItem} instances.
 *
 * <p>
 * This registry handles:
 * <ul>
 * <li>Registering and validating chicken types</li>
 * <li>Lookup by dye metadata</li>
 * <li>Identifying and retrieving the special "smart chicken"</li>
 * <li>Handling right-click conversion from vanilla chicken → smart chicken</li>
 * </ul>
 *
 * <p>
 * Singleton instance available via {@link #INSTANCE}.
 */
@EventBusSubscriber
public class ChickensRegistry extends BaseRegistry<ChickensRegistryItem> {

    /**
     * Global registry instance.
     */
    public static final ChickensRegistry INSTANCE = new ChickensRegistry();

    private ChickensRegistry() {}

    /**
     * Finds a chicken type whose lay item is a dye of the given metadata.
     *
     * @param dyeMetadata the dye damage value (0–15)
     * @return the matching chicken, or {@code null} if none exist
     */
    @Nullable
    public ChickensRegistryItem findDyeChicken(int dyeMetadata) {
        for (ChickensRegistryItem chicken : items.values()) {
            if (chicken.isDye(dyeMetadata)) {
                return chicken;
            }
        }
        return null;
    }

    /**
     * @return the registered "smart chicken" item, or {@code null} if not enabled
     */
    @Nullable
    public ChickensRegistryItem getSmartChicken() {
        for (ChickensRegistryItem chicken : items.values()) {
            if ("SmartChicken".equalsIgnoreCase(chicken.getEntityName())) {
                return chicken;
            }
        }
        return null;
    }

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.enableChickens;
    }

    /**
     * Event: Converts a vanilla chicken into a "smart chicken" when right-clicked
     * with a book.
     *
     * @param event the interaction event
     */
    @SubscribeEvent
    public static void handleInteraction(EntityInteractEvent event) {
        if (event.entityPlayer.worldObj.isRemote) {
            return;
        }

        ItemStack item = event.entityPlayer.getHeldItem();
        if (item == null || item.getItem() != Items.book) {
            return;
        }

        if (!(event.target instanceof EntityChicken chicken)) {
            return;
        }

        ChickensRegistryItem smart = INSTANCE.getSmartChicken();
        if (smart == null || !smart.isEnabled()) {
            return;
        }

        EntityChickensChicken smartChicken = convertToSmart(chicken, event.entityPlayer.worldObj, smart);

        chicken.setDead();
        event.entityPlayer.worldObj.spawnEntityInWorld(smartChicken);

        event.setCanceled(true);
    }

    public static boolean isFallbackFood(ItemStack stack) {
        return stack != null && stack.getItem() == Items.wheat_seeds;
    }

    /**
     * Converts a vanilla chicken entity into a {@link EntityChickensChicken} while copying
     * key attributes like position, rotation, type, and growing age.
     *
     * @param chicken                 the original vanilla chicken
     * @param worldObj                world where conversion happens
     * @param smartChickenDescription registry item describing the smart chicken
     * @return a fully initialized EntityChickensChicken
     */
    private static EntityChickensChicken convertToSmart(EntityChicken chicken, World worldObj,
        ChickensRegistryItem smartChickenDescription) {
        EntityChickensChicken smartChicken = new EntityChickensChicken(worldObj);

        smartChicken.setPositionAndRotation(
            chicken.posX,
            chicken.posY,
            chicken.posZ,
            chicken.rotationYaw,
            chicken.rotationPitch);

        smartChicken.onSpawnWithEgg(null);
        smartChicken.setType(smartChickenDescription.getId());
        smartChicken.setCustomNameTag(smartChickenDescription.getEntityName());
        smartChicken.setGrowingAge(chicken.getGrowingAge());

        return smartChicken;
    }
}
