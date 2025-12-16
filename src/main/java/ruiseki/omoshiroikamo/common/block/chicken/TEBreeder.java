package ruiseki.omoshiroikamo.common.block.chicken;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.entity.IMobStats;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.chicken.ChickenContainer;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;

public class TEBreeder extends TERoostBase {

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
    public String getMachineName() {
        return ModObject.blockBreeder.unlocalisedName;
    }

    @Override
    protected void spawnChickenDrop() {
        DataChicken left = getChickenData(0);
        DataChicken right = getChickenData(1);
        if (left != null && right != null) {
            ItemStack child = left.createChildStack(right, worldObj);
            if (child != null) {
                applyBreederStatScaling(child, left, right);
                putStackInOutput(child);
                playSpawnSound();
            }
        }
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
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.registerSlotGroup("input", 3);
        settings.customContainer(ChickenContainer::new);
        ModularPanel panel = new ModularPanel("breeder_gui");
        panel.child(
            Flow.column()
                .child(
                    IKey.str(StatCollector.translateToLocal("tile." + getMachineName() + ".name"))
                        .asWidget()
                        .margin(6, 0, 5, 0)
                        .align(Alignment.TopLeft))
                .child(
                    SlotGroupWidget.builder()
                        .matrix("S+II  OOO")
                        .key(
                            'I',
                            index -> new ItemSlot().background(MGuiTextures.ROOST_SLOT)
                                .hoverBackground(MGuiTextures.ROOST_SLOT)
                                .slot(
                                    new ModularSlot(inv, index).slotGroup("input")
                                        .filter(stack -> isItemValidForSlot(index, stack))))
                        .key(
                            'S',
                            new ItemSlot().slot(
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
                    new ProgressWidget().progress(this::getProgress)
                        .tooltipDynamic(richTooltip -> {
                            richTooltip.add(getFormattedProgress());
                            richTooltip.markDirty();
                        })
                        .topRel(0.25f)
                        .leftRel(0.57f)
                        .size(36, 18)
                        .texture(MGuiTextures.BREEDER_PROGRESS, 36)));
        panel.bindPlayerInventory();
        return panel;
    }

    private void applyBreederStatScaling(ItemStack child, DataChicken left, DataChicken right) {
        NBTTagCompound tag = child.getTagCompound();
        if (tag == null) {
            return;
        }

        Random random = worldObj != null ? worldObj.rand : new Random();
        adjustChildStat(
            tag,
            IMobStats.GROWTH_NBT,
            left.getGrowthStat(),
            right.getGrowthStat(),
            ChickenConfig.getMaxGrowthStat(),
            random);
        adjustChildStat(
            tag,
            IMobStats.GAIN_NBT,
            left.getGainStat(),
            right.getGainStat(),
            ChickenConfig.getMaxGainStat(),
            random);
        adjustChildStat(
            tag,
            IMobStats.STRENGTH_NBT,
            left.getStrengthStat(),
            right.getStrengthStat(),
            ChickenConfig.getMaxStrengthStat(),
            random);
    }

    private void adjustChildStat(NBTTagCompound tag, String key, int parentStatA, int parentStatB, int maxStat,
        Random random) {
        if (!tag.hasKey(key)) {
            return;
        }

        int parentBaseline = Math.max(parentStatA, parentStatB);
        int currentValue = Math.min(maxStat, Math.max(1, tag.getInteger(key)));
        if (currentValue <= parentBaseline) {
            return;
        }

        int extra = currentValue - parentBaseline;
        float modifier = getBreederDiminishingModifier(parentBaseline, extra, maxStat);

        if (modifier <= 0.0f || random.nextFloat() > modifier) {
            tag.setInteger(key, parentBaseline);
            return;
        }

        int adjustedIncrease = Math.max(1, Math.round(extra * modifier));
        tag.setInteger(key, Math.min(parentBaseline + adjustedIncrease, maxStat));
    }

    private float getBreederDiminishingModifier(int parentBaseline, int desiredIncrease, int maxStat) {
        int current = Math.max(1, parentBaseline);
        int cappedIncrease = Math.max(0, desiredIncrease);
        int effectiveStat = Math.max(1, Math.min(current + cappedIncrease, Math.max(1, maxStat)));
        float modifier = 1.0f;
        int threshold = 10;

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
