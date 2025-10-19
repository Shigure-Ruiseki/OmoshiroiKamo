package ruiseki.omoshiroikamo.common.block.electrolyzer;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.fluid.IFluidHandlerAdv;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.api.io.SlotDefinition;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuis;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractPoweredTaskTE;
import ruiseki.omoshiroikamo.common.recipe.chance.ChanceFluidStack;
import ruiseki.omoshiroikamo.common.recipe.chance.ChanceItemStack;

public class TEElectrolyzer extends AbstractPoweredTaskTE implements IFluidHandlerAdv {

    public TEElectrolyzer() {
        super(new SlotDefinition(0, 2, 3, 5, 0, 2, 3, 5, -1, -1), MaterialRegistry.get("Iron"));
    }

    @Override
    public int getPowerUsePerTick() {
        return 20;
    }

    @Override
    public String getMachineName() {
        return ModObject.blockElectrolyzer.unlocalisedName;
    }

    @Override
    protected boolean isMachineItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return slot >= slotDefinition.minItemInputSlot && slot <= slotDefinition.maxItemInputSlot;
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        syncManager.registerSlotGroup("item_inv", 1);

        return MGuis.mteTemplatePanelBuilder(this, data, syncManager, settings)
            .doesAddConfigR(true)
            .doesAddConfigIOHeat(true)
            .doesAddConfigIOFluid(true)
            .doesAddConfigIOItem(true)
            .build()
            .child(
                new Column().child(
                    SlotGroupWidget.builder()
                        .matrix("IIISSIII", "FFFSSFFF")
                        .key('I', index -> {
                            return new ItemSlot().slot(
                                new ModularSlot(this.inv, index).slotGroup("item_inv")
                                    .filter(stack -> isMachineItemValidForSlot(index, stack)))
                                .name("Slot " + index);
                        })
                        .key('F', index -> {
                            return new FluidSlot()
                                .syncHandler(
                                    new FluidSlotSyncHandler(fluidTanks[index])
                                        .canFillSlot(index <= slotDefinition.maxFluidInputSlot))
                                .name("Slot " + index);
                        })
                        .build()
                        .topRel(0.1f)
                        .alignX(Alignment.CENTER))
                    .child(
                        new ProgressWidget().progress(this::getProgress)
                            .topRel(0.1f)
                            .leftRel(0.5f)
                            .texture(GuiTextures.PROGRESS_ARROW, 20))

                    .child(
                        new ToggleButton().size(18, 18)
                            .overlay(true, GuiTextures.LOCKED)
                            .overlay(false, GuiTextures.UNLOCKED)
                            .value(new BooleanSyncValue(this::isRecipeLocked, this::setRecipeLocked))
                            .tooltipDynamic(richTooltip -> {
                                richTooltip.add(IKey.str("Predicted Outputs:\n"));

                                List<ChanceItemStack> items = getItemOutput();
                                List<ChanceFluidStack> fluids = getFluidOutput();

                                if (items.isEmpty() && fluids.isEmpty()) {
                                    richTooltip.add(IKey.str(" - No valid recipe\n"));
                                } else {
                                    for (ChanceItemStack item : items) {
                                        if (item != null && item.stack != null) {
                                            String chanceStr = item.chance >= 1f ? ""
                                                : String.format(" (%.0f%%)", item.chance * 100f);
                                            richTooltip.add(
                                                IKey.str(
                                                    " - " + item.stack.stackSize
                                                        + "x "
                                                        + item.stack.getDisplayName()
                                                        + chanceStr
                                                        + "\n"));
                                        }
                                    }

                                    for (ChanceFluidStack fluid : fluids) {
                                        if (fluid != null && fluid.stack != null && fluid.stack.getFluid() != null) {
                                            String chanceStr = fluid.chance >= 1f ? ""
                                                : String.format(" (%.0f%%)", fluid.chance * 100f);
                                            richTooltip.add(
                                                IKey.str(
                                                    " - " + fluid.stack.amount
                                                        + "L of "
                                                        + fluid.stack.getLocalizedName()
                                                        + chanceStr
                                                        + "\n"));
                                        }
                                    }
                                }

                                richTooltip.markDirty();
                            })
                            .selectedBackground(GuiTextures.MC_BUTTON)
                            .selectedHoverBackground(GuiTextures.MC_BUTTON_HOVERED)
                            .topRel(0.21f)
                            .leftRel(0.5f)));
    }

    @Override
    public SmartTank[] getTanks() {
        return fluidTanks;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        if (fluidTanks == null || fluidTanks.length == 0) {
            return new FluidTankInfo[0];
        }

        FluidTankInfo[] info = new FluidTankInfo[fluidTanks.length];
        for (int i = 0; i < fluidTanks.length; i++) {
            SmartTank tank = fluidTanks[i];
            if (tank != null) {
                info[i] = new FluidTankInfo(tank.getFluid(), tank.getCapacity());
            } else {
                info[i] = new FluidTankInfo(null, 0);
            }
        }
        return info;
    }

}
