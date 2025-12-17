package ruiseki.omoshiroikamo.api.entity.model;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.common.init.ModItems;

public class DataModel {

    private final ModelRegistryItem model;

    private int tier;
    private int killCount;
    private int simulationCount;
    private int totalKillCount;
    private int totalSimulationCount;

    protected final String TIER_TAG = "tier";
    protected final String KILL_COUNT_TAG = "killCount";
    protected final String SIMULATION_COUNT_TAG = "simulationCount";
    protected final String TOTAL_KILL_COUNT_TAG = "totalKillCount";
    protected final String TOTAL_SIMULATION_COUNT_TAG = "totalSimulationCount";

    private DataModel(ModelRegistryItem model, NBTTagCompound compound) {
        this.model = model;
        if (compound != null) {
            this.tier = compound.getInteger(TIER_TAG);
            this.killCount = compound.getInteger(KILL_COUNT_TAG);
            this.simulationCount = compound.getInteger(SIMULATION_COUNT_TAG);
            this.totalKillCount = compound.getInteger(TOTAL_KILL_COUNT_TAG);
            this.totalSimulationCount = compound.getInteger(TOTAL_SIMULATION_COUNT_TAG);
        }
    }

    public static boolean isModel(ItemStack stack) {
        return stack != null && stack.getItem() == ModItems.DATA_MODEL.getItem();
    }

    public static DataModel getDataFromStack(ItemStack stack) {
        if (!isModel(stack)) return null;
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        return model != null ? new DataModel(model, stack.getTagCompound()) : null;
    }

    public static DataModel getDataFromKey(String key, ItemStack stack) {
        if (!isModel(stack)) {
            return null;
        }
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByName(key);
        return model != null ? new DataModel(model, stack.getTagCompound()) : null;
    }

    public static List<DataModel> getAllModels() {
        List<DataModel> models = new LinkedList<>();
        for (ModelRegistryItem item : ModelRegistry.INSTANCE.getItems()) {
            models.add(new DataModel(item, null));
        }
        return models;
    }

    public int getId() {
        return model.getId();
    }

    public ModelRegistryItem getItem() {
        return model;
    }

    public int getTier() {
        return tier;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getTotalKillCount() {
        return totalKillCount;
    }

    public int getSimulationCount() {
        return simulationCount;
    }

    public int getTotalSimulationCount() {
        return totalSimulationCount;
    }

    @Nullable
    public Class<? extends Entity> getEntityClass() {
        return (Class<? extends Entity>) EntityList.stringToClassMapping.get(model.getEntityName());
    }

    public void increaseMobKillCount(EntityPlayerMP player) {
        int tier = getTier();
        int i = getKillCount();

        // TODO Add GlitchSword and Trial
        i = i + 1;
        setKillCount(i);
        setTotalKillCount(getTotalKillCount() + 1);

        // TODO Add LevelUp Tier
        // if(DataModelExperience.shouldIncreaseTier(tier, i, getCurrentTierSimulationCount(stack))) {
        // PlayerHelper.sendMessage(player, new TextComponentString(stack.getDisplayName().getFormattedText() + "
        // reached the " + getTierName(stack, true) + " tier"));
        //
        // setCurrentTierKillCount(stack, 0);
        // setCurrentTierSimulationCount(stack, 0);
        // setTier(stack, tier + 1);
        // }
    }

    public void increaseSimulationCount() {
        int tier = getTier();
        int i = getSimulationCount();
        i = i + 1;
        setSimulationCount(i);

        // Update the totals
        setTotalSimulationCount(getTotalSimulationCount() + 1);

        // TODO Add LevelUp Tier
        // if(DataModelExperience.shouldIncreaseTier(tier, getCurrentTierKillCount(stack), i)) {
        // setCurrentTierKillCount(stack, 0);
        // setCurrentTierSimulationCount(stack, 0);
        // setTier(stack, tier + 1);
        // }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(TIER_TAG, tier);
        tag.setInteger(KILL_COUNT_TAG, killCount);
        tag.setInteger(SIMULATION_COUNT_TAG, simulationCount);
        tag.setInteger(TOTAL_KILL_COUNT_TAG, totalKillCount);
        tag.setInteger(TOTAL_SIMULATION_COUNT_TAG, totalSimulationCount);
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public void setKillCount(int count) {
        this.killCount = count;
    }

    public void setSimulationCount(int count) {
        this.simulationCount = count;
    }

    public void setTotalKillCount(int count) {
        this.totalKillCount = count;
    }

    public void setTotalSimulationCount(int count) {
        this.totalSimulationCount = count;
    }

}
