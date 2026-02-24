package ruiseki.omoshiroikamo.module.chickens.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.chickens.client.gui.container.ChickenContainer;

public class TEBreeder extends TERoostBase implements IGuiHolder<PosGuiData> {
    // TODO: Add auto breeder

    public TEBreeder() {}

    private boolean wasActive = false;

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (!worldObj.isRemote) {
            boolean active = isActive();
            if (active != wasActive) {
                wasActive = active;
                worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, active ? 1 : 0, 3);
            }
        }
        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y,
        int z) {
        return oldBlock != newBlock;
    }

    @Override
    protected void playSpawnSound() {
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, "mob.chicken.plop", 0.5f, 0.8f);
    }

    @Override
    protected void spawnChickenDrop() {
        DataChicken left = getChickenData(0);
        DataChicken right = getChickenData(1);
        ItemStack foodStack = getStackInSlot(2);

        if (left != null && right != null && foodStack != null) {
            ItemStack childStack = null;
            Random random = worldObj != null ? worldObj.rand : new Random();

            // Find potential children (including parents)
            List<ChickensRegistryItem> possibleChildren = ChickensRegistry.INSTANCE
                .getChildren(left.getItem(), right.getItem());

            ChickensRegistryItem selectedSpecies = null;

            for (ChickensRegistryItem candidate : possibleChildren) {
                // Mutation is a species different from both parents
                boolean isPotentialMutation = candidate != left.getItem() && candidate != right.getItem();
                if (isPotentialMutation) {
                    // Check if candidate requires no food OR if food matches
                    if (candidate.isFood(foodStack) || candidate.isFallbackFood(foodStack)) {
                        // Use individual mutation chance
                        if (random.nextFloat() < candidate.getMutationChance()) {
                            selectedSpecies = candidate;
                            break;
                        }
                    }
                }
            }

            // Fallback to parents if mutation failed or no mutation food matched
            if (selectedSpecies == null) {
                selectedSpecies = random.nextBoolean() ? left.getItem() : right.getItem();
            }

            if (selectedSpecies != null) {
                DataChicken childData = new DataChicken(selectedSpecies, null);
                childStack = childData.buildStack();
                // Check if it's a mutation (different from both parents)
                boolean isMutation = selectedSpecies != left.getItem() && selectedSpecies != right.getItem();
                applyBreederStatScaling(childStack, left, right, isMutation);
                putStackInOutput(childStack);
                playSpawnSound();
            }
        }
    }

    private void applyBreederStatScaling(ItemStack child, DataChicken left, DataChicken right, boolean isMutation) {
        NBTTagCompound tag = child.getTagCompound();
        if (tag == null) {
            return;
        }

        Random random = worldObj != null ? worldObj.rand : new Random();
        int p1Strength = left.getStrengthStat();
        int p2Strength = right.getStrengthStat();

        // Calculate inherit base stats
        int growth = calculateNewStat(
            p1Strength,
            p2Strength,
            left.getGrowthStat(),
            right.getGrowthStat(),
            random,
            ChickenConfig.getMaxGrowthStat());
        int gain = calculateNewStat(
            p1Strength,
            p2Strength,
            left.getGainStat(),
            right.getGainStat(),
            random,
            ChickenConfig.getMaxGainStat());
        int strength = calculateNewStat(
            p1Strength,
            p2Strength,
            p1Strength,
            p2Strength,
            random,
            ChickenConfig.getMaxStrengthStat());

        // Apply mutation penalty if it's a new species
        if (isMutation) {
            DataChicken childData = DataChicken.getDataFromStack(child);
            if (childData != null) {
                int tier = childData.getItem()
                    .getTier();
                float rawmultiplier = 0.1f * tier;
                float multiplierGrowth = Math.max(0.1f, 1.0f - rawmultiplier * (1.5f + random.nextFloat()));
                float multiplierGain = Math.max(0.1f, 1.0f - rawmultiplier * (1.5f + random.nextFloat()));
                float multiplierStrength = Math.max(0.1f, 1.0f - rawmultiplier * (1.5f + random.nextFloat()));
                growth = Math.max(1, Math.round(growth * multiplierGrowth));
                gain = Math.max(1, Math.round(gain * multiplierGain));
                strength = Math.max(1, Math.round(strength * multiplierStrength));
            }
        }

        tag.setInteger(IMobStats.GROWTH_NBT, growth);
        tag.setInteger(IMobStats.GAIN_NBT, gain);
        tag.setInteger(IMobStats.STRENGTH_NBT, strength);
    }

    @Override
    protected boolean isMutationFood(ItemStack stack) {
        DataChicken left = getChickenData(0);
        DataChicken right = getChickenData(1);
        if (left == null || right == null) {
            return false;
        }

        List<ChickensRegistryItem> possibleChildren = ChickensRegistry.INSTANCE
            .getChildren(left.getItem(), right.getItem());
        for (ChickensRegistryItem candidate : possibleChildren) {
            boolean isPotentialMutation = candidate != left.getItem() && candidate != right.getItem();
            if (isPotentialMutation) {
                if (candidate.isFood(stack) || candidate.isFallbackFood(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getSizeChickenInventory() {
        return 2;
    }

    @Override
    protected int requiredSeedsForDrop() {
        return 2;
    }

    @Override
    protected double speedMultiplier() {
        return ChickenConfig.roostSpeed;
    }

    @Override
    protected boolean hasFreeOutputSlot() {
        for (int i = slotDefinition.getMinItemOutput(); i <= slotDefinition.getMaxItemOutput(); i++) {
            if (getStackInSlot(i) == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.registerSlotGroup("input", 3);
        settings.customContainer(ChickenContainer::new);
        ModularPanel panel = new ModularPanel("breeder_gui");
        panel.child(
            Flow.column()
                .child(
                    IKey.str(getLocalizedName())
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .matrix("S+II  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(OKGuiTextures.ROOST_SLOT)
                                .hoverBackground(OKGuiTextures.ROOST_SLOT)
                                .slot(
                                    new ModularSlot(inv, index).slotGroup("input")
                                        .filter(stack -> isItemValidForSlot(index, stack))))
                        .key(
                            'S',
                            new ItemSlot().background(OKGuiTextures.FOOD_SLOT)
                                .hoverBackground(OKGuiTextures.FOOD_SLOT)
                                .slot(
                                    new ModularSlot(inv, 2).slotGroup("input")
                                        .filter(stack -> isItemValidForSlot(2, stack))))
                        .key('+', new Widget<>().background(GuiTextures.ADD))
                        .key(
                            'O',
                            index -> new ItemSlot().slot(new ModularSlot(inv, index + 3).accessibility(false, true)))
                        .build()
                        .topRel(0.25f)
                        .alignX(Alignment.CENTER))
                .child(
                    new ProgressWidget().value(new DoubleSyncValue(this::getProgress))
                        .tooltipDynamic(richTooltip -> {
                            richTooltip.add(WailaUtils.getProgress(this));
                            richTooltip.markDirty();
                        })
                        .topRel(0.25f)
                        .leftRel(0.57f)
                        .size(36, 18)
                        .texture(OKGuiTextures.BREEDER_PROGRESS, 36)));
        panel.bindPlayerInventory();
        return panel;
    }

    private int calculateNewStat(int p1Strength, int p2Strength, int stat1, int stat2, Random rand, int maxStatValue) {
        int denominator = Math.max(1, p1Strength + p2Strength);
        int weightedAverage = (stat1 * p1Strength + stat2 * p2Strength) / denominator;
        int mutation = rand.nextInt(2) + 1;

        int targetValue = weightedAverage + mutation;
        int currentStat = Math.max(stat1, stat2);
        int maxValue = Math.max(1, maxStatValue);

        if (targetValue > currentStat) {
            int desiredIncrease = targetValue - currentStat;
            float modifier = getBreederDiminishingModifier(currentStat, desiredIncrease, maxValue);

            int adjustedIncrease = Math.max(1, Math.round(desiredIncrease * modifier));
            if (rand.nextFloat() <= modifier) {
                targetValue = currentStat + adjustedIncrease;
            } else {
                targetValue = currentStat;
            }
        }

        if (targetValue <= 1) {
            return 1;
        }

        return Math.min(targetValue, maxValue);
    }

    private float getBreederDiminishingModifier(int currentStat, int desiredIncrease, int maxStatValue) {
        int current = Math.max(1, currentStat);
        int cappedIncrease = Math.max(0, desiredIncrease);
        int effectiveStat = Math.max(1, Math.min(current + cappedIncrease, Math.max(1, maxStatValue)));
        float modifier = 1.0f;
        long threshold = 10;

        while (effectiveStat > threshold) {
            modifier *= 0.8f;
            if (threshold >= Integer.MAX_VALUE / 2) {
                break;
            }
            threshold *= 2;
        }

        return Math.max(0.0f, modifier);
    }
}
