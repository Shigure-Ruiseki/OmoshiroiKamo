package ruiseki.omoshiroikamo.common.item.dml;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.entity.dml.DataModel;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

@EventBusSubscriber
public class ModelEventHandler {

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.useDML;
    }

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        if (event.source.getEntity() instanceof EntityPlayer) {
            handlePlayerKilledEntity(event);
        }
    }

    private static void handlePlayerKilledEntity(LivingDeathEvent event) {
        if (!(event.source.getEntity() instanceof EntityPlayerMP player)) return;

        List<ItemStack> inventory = new ArrayList<>();
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null) inventory.add(stack);
        }

        // Grab the deep learners and combat trial items from a players inventory
        List<ItemStack> deepLearners = getDeepLearners(inventory);
        // List<ItemStack> trialKeys = getTrialKeys(inventory);
        List<ItemStack> updatedModels = new ArrayList<>();

        // Update every data model in every deeplearner that match the kill event
        for (ItemStack stack : deepLearners) {
            List<ItemStack> models = updateAndReturnModels(stack, event, player);
            updatedModels.addAll(models);
        }

        // Return early if no models were affected
        if (updatedModels.isEmpty()) return;

        // Chance to drop pristine matter from the model that gained data
        // if (ItemGlitchArmor.isSetEquippedByPlayer(player)) {
        // PlayerTrial cap = DeepMobLearning.proxy.getTrialCapability(player);
        // if (!cap.isTrialActive()) {
        // ItemGlitchArmor.dropPristineMatter(event.entity.worldObj, event.entity.getPosition(), updatedModels.get(0),
        // player);
        // }
        // }

        // ItemStack mainHand = player.getCurrentEquippedItem();
        // if (mainHand != null && mainHand.getItem() instanceof ItemGlitchSword) {
        // PlayerTrial cap = DeepMobLearning.proxy.getTrialCapability(player);
        // if (!cap.isTrialActive()) {
        // if (ItemGlitchSword.canIncreaseDamage(mainHand)) {
        // ItemGlitchSword.increaseDamage(mainHand, player);
        // }
        // }
        // }

        // Attune the trial key if possible
        // for (ItemStack stack : trialKeys) {
        // attuneTrialKey(stack, updatedModels.get(0), event, player);
        // }
    }

    private static List<ItemStack> updateAndReturnModels(ItemStack deepLearner, LivingDeathEvent event,
        EntityPlayerMP player) {
        if (deepLearner == null) return new ArrayList<>();

        DeepLearnerHandler handler = new DeepLearnerHandler(deepLearner);
        List<ItemStack> deepLearnerItems = handler.getStacks();
        List<ItemStack> result = new ArrayList<>();

        if (deepLearnerItems == null) return result;

        for (int i = 0; i < deepLearnerItems.size(); i++) {
            ItemStack stack = deepLearnerItems.get(i);
            if (stack == null || !(stack.getItem() instanceof ItemDataModel)) continue;

            if (DataModel.entityLivingMatchesMob(stack, event.entityLiving)) {
                DataModel.increaseMobKillCount(player, stack);
                result.add(stack);
            }

            handler.setStackInSlot(i, stack);
        }

        return result;
    }

    private static List<ItemStack> getDeepLearners(List<ItemStack> inventory) {
        List<ItemStack> result = new ArrayList<>();
        inventory.forEach(stack -> {
            if (stack.getItem() instanceof ItemDeepLearner) {
                result.add(stack);
            }
        });

        return result;
    }

}
