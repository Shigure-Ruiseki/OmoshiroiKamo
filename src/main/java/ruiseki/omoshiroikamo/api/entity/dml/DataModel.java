package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.item.ItemNBTUtils;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class DataModel {

    protected static final String TIER_TAG = "Tier";
    protected static final String KILL_COUNT_TAG = "KillCount";
    protected static final String SIMULATION_COUNT_TAG = "SimulationCount";
    protected static final String TOTAL_KILL_COUNT_TAG = "TotalKillCount";
    protected static final String TOTAL_SIMULATION_COUNT_TAG = "TotalSimulationCount";

    private DataModel() {}

    public static boolean isModel(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.DATA_MODEL.getItem();
    }

    public static ModelRegistryItem getDataFromStack(ItemStack stack) {
        if (!isModel(stack)) return null;
        return ModelRegistry.INSTANCE.getByType(getId(stack));
    }

    @Nullable
    public static Class<? extends Entity> getEntityClass(ItemStack stack) {
        return getEntityClass(getId(stack));
    }

    @Nullable
    public static Class<? extends Entity> getEntityClass(int id) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(id);
        if (model == null) return null;
        return EntityList.stringToClassMapping.get(model.entityDisplay);
    }

    @Nullable
    public static List<Class<? extends Entity>> getEntityClasses(ItemStack stack) {
        if (stack == null) return null;
        return getEntityClasses(getId(stack));
    }

    @Nullable
    public static List<Class<? extends Entity>> getEntityClasses(int id) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(id);
        if (model == null) return null;
        List<Class<? extends Entity>> classes = model.getAssociatedEntityClasses();
        if (classes == null) return null;
        return classes.isEmpty() ? null : classes;
    }

    public static boolean entityLivingMatchesMob(ItemStack stack, Entity entity) {
        return entityLivingMatchesMob(getId(stack), entity);
    }

    public static boolean entityLivingMatchesMob(int id, Entity entity) {
        if (entity == null) return false;

        List<Class<? extends Entity>> classes = getEntityClasses(id);
        if (classes != null) {
            for (Class<? extends Entity> clazz : classes) {
                if (clazz.isInstance(entity)) return true;
            }
        }

        return false;
    }

    public static void increaseMobKillCount(EntityPlayerMP player, ItemStack stack) {
        int tier = getTier(stack);
        int i = getKillCount(stack);

        // TODO Add GlitchSword and Trial
        i = i + 1;
        setKillCount(i, stack);
        setTotalKillCount(getTotalKillCount(stack) + 1, stack);

        if (DataModelExperience.shouldIncreaseTier(tier, i, getSimulationCount(stack))) {
            String nextTierName = LibMisc.LANG.localize(DataModelExperience.getTierName(tier + 1));
            String entityName = LibMisc.LANG.localize(
                ModelRegistry.INSTANCE.getByType(getId(stack))
                    .getDisplayName());
            String message = LibMisc.LANG.localize("tooltip.data_model.reached_tier", entityName, nextTierName);
            player.addChatMessage(new ChatComponentText(message));
            setKillCount(0, stack);
            setSimulationCount(0, stack);
            setTier(tier + 1, stack);
        }
    }

    public static void increaseSimulationCount(ItemStack stack) {
        int tier = getTier(stack);
        int i = getSimulationCount(stack);
        i = i + 1;
        setSimulationCount(i, stack);

        setTotalSimulationCount(getTotalSimulationCount(stack) + 1, stack);

        if (DataModelExperience.shouldIncreaseTier(tier, getKillCount(stack), i)) {
            setKillCount(0, stack);
            setSimulationCount(0, stack);
            setTier(tier + 1, stack);
        }
    }

    public static NBTTagCompound createTagCompound(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(TIER_TAG, getTier(stack));
        tag.setInteger(KILL_COUNT_TAG, getKillCount(stack));
        tag.setInteger(SIMULATION_COUNT_TAG, getSimulationCount(stack));
        tag.setInteger(TOTAL_KILL_COUNT_TAG, getTotalKillCount(stack));
        tag.setInteger(TOTAL_SIMULATION_COUNT_TAG, getTotalSimulationCount(stack));
        return tag;
    }

    public static int getId(ItemStack stack) {
        return stack.getItemDamage();
    }

    public static int getTier(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, TIER_TAG, 0);
    }

    public static int getKillCount(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, KILL_COUNT_TAG, 0);
    }

    public static int getSimulationCount(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, SIMULATION_COUNT_TAG, 0);
    }

    public static int getTotalKillCount(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, TOTAL_KILL_COUNT_TAG, 0);
    }

    public static int getTotalSimulationCount(ItemStack stack) {
        return ItemNBTUtils.getInt(stack, TOTAL_SIMULATION_COUNT_TAG, 0);
    }

    public static void setTier(int tier, ItemStack stack) {
        ItemNBTUtils.setInt(stack, TIER_TAG, tier);
    }

    public static void setKillCount(int count, ItemStack stack) {
        ItemNBTUtils.setInt(stack, KILL_COUNT_TAG, count);
    }

    public static void setSimulationCount(int count, ItemStack stack) {
        ItemNBTUtils.setInt(stack, SIMULATION_COUNT_TAG, count);
    }

    public static void setTotalKillCount(int count, ItemStack stack) {
        ItemNBTUtils.setInt(stack, TOTAL_KILL_COUNT_TAG, count);
    }

    public static void setTotalSimulationCount(int count, ItemStack stack) {
        ItemNBTUtils.setInt(stack, TOTAL_SIMULATION_COUNT_TAG, count);
    }

    public static boolean hasExtraTooltip(ItemStack stack) {
        ModelRegistryItem model = getDataFromStack(stack);
        if (model == null) {
            return false;
        }

        return model.getExtraTooltip() != null;
    }

    public static String getExtraTooltip(ItemStack stack) {
        ModelRegistryItem model = getDataFromStack(stack);
        if (model == null) {
            return null;
        }
        return model.getExtraTooltip();
    }

    public static int getSimulationTickCost(ItemStack stack) {
        ModelRegistryItem model = getDataFromStack(stack);
        if (model == null) {
            return 0;
        }
        return model.getSimulationRFCost();
    }

    public static boolean canSimulate(ItemStack stack) {
        ModelTierRegistryItem tier = ModelTierRegistry.INSTANCE.getByType(getTier(stack));
        if (tier == null) {
            return false;
        }

        return tier.canSimulate;
    }

    public static int getPristineChance(ItemStack stack) {
        ModelTierRegistryItem tier = ModelTierRegistry.INSTANCE.getByType(getTier(stack));
        if (tier == null) {
            return 0;
        }

        return tier.getPristineChance();
    }

    public static int getSimulationEnergy(ItemStack stack) {
        ModelRegistryItem model = getDataFromStack(stack);
        if (model == null) {
            return 0;
        }

        return model.getSimulationRFCost();
    }

    public static String getTierName(ItemStack stack) {
        return LibMisc.LANG.localize(DataModelExperience.getTierName(getTier(stack)));
    }

    public static String getMatterTypeName(ItemStack stack) {
        ModelRegistryItem model = getDataFromStack(stack);
        if (model == null) {
            return "";
        }
        LivingRegistryItem item = LivingRegistry.INSTANCE.getByType(
            model.getLivingMatter()
                .getItemDamage());
        return LibMisc.LANG.localize(item.getItemName());
    }

    public static boolean isMaxTier(ItemStack stack) {
        return DataModelExperience.isMaxTier(getTier(stack));
    }

    public static boolean isDataModelMatchesLivingMatter(ItemStack modelStack, ItemStack livingMatterStack) {
        ModelRegistryItem model = getDataFromStack(modelStack);
        if (model == null) {
            return false;
        }

        return ItemUtils.areStacksEqual(model.getLivingMatter(), livingMatterStack);
    }

    public static boolean isDataModelMatchesPristineMatter(ItemStack modelStack, ItemStack pristineMatterStack) {
        return modelStack.getItemDamage() == pristineMatterStack.getItemDamage();
    }

    public static int getCurrentTierSimulationCountWithKills(ItemStack stack) {
        return DataModelExperience
            .getCurrentTierSimulationCountWithKills(getTier(stack), getKillCount(stack), getSimulationCount(stack));
    }

    public static int getSimulationsToNextTier(ItemStack stack) {
        return DataModelExperience
            .getSimulationsToNextTier(getTier(stack), getKillCount(stack), getSimulationCount(stack));
    }

    public static int getTierRoof(ItemStack stack) {
        return DataModelExperience.getTierRoof(getTier(stack), false);
    }

    public static int getKillMultiplier(ItemStack stack) {
        return DataModelExperience.getKillMultiplier(getTier(stack));
    }
}
