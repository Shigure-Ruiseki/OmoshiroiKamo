package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.fluid.SmartTank;
import ruiseki.omoshiroikamo.api.modular.IFluidPort;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

public abstract class AbstractFluidPortTE extends AbstractTE implements IFluidPort, IFluidHandler {

    protected final IO[] sides = new IO[6];

    protected SmartTank tank;
    private boolean tankDirty = false;

    public AbstractFluidPortTE(int fluidCapacity) {
        tank = new SmartTank(fluidCapacity) {

            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                markDirty();
            }
        };
        for (int i = 0; i < 6; i++) {
            sides[i] = IO.NONE;
        }
    }

    public abstract int getTier();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public IO getSideIO(ForgeDirection side) {
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, IO state) {
        sides[side.ordinal()] = state;
        requestRenderUpdate();
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        int[] sideData = new int[6];
        for (int i = 0; i < 6; i++) {
            sideData[i] = sides[i].ordinal();
        }
        root.setIntArray("sideIO", sideData);
        tank.writeCommon(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        if (root.hasKey("sideIO")) {
            int[] sideData = root.getIntArray("sideIO");
            for (int i = 0; i < 6 && i < sideData.length; i++) {
                sides[i] = IO.values()[sideData[i]];
            }
        }
        tank.readCommon(root);
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (tankDirty && shouldDoWorkThisTick(20)) {
            tankDirty = false;
            return true;
        }
        return false;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (!canFill(from)) {
            return 0;
        }
        int res = tank.fill(resource, doFill);
        if (res > 0 && doFill) {
            tankDirty = true;
        }
        return res;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (!canDrain(from)) {
            return null;
        }
        FluidStack res = tank.drain(resource, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (!canDrain(from)) {
            return null;
        }
        FluidStack res = tank.drain(maxDrain, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
    }

    public boolean canFill(ForgeDirection from) {
        return canInput(from) && isRedstoneActive();
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return canFill(from) && fluid != null
            && (tank.getFluidAmount() > 0 && tank.getFluid()
                .getFluidID() == fluid.getID() || tank.getFluidAmount() == 0);
    }

    public boolean canDrain(ForgeDirection from) {
        return canOutput(from) && isRedstoneActive();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return canDrain(from) && tank.canDrainFluidType(fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tank.getInfo() };
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = new ModularPanel("fluid_port");

        EnumSyncValue<RedstoneMode> redstoneSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            this::getRedstoneMode,
            this::setRedstoneMode);
        syncManager.syncValue("redstoneSyncer", redstoneSyncer);

        panel.child(
            new RedstoneModeWidget(redstoneSyncer).pos(-20, 2)
                .size(18)
                .excludeAreaInRecipeViewer());

        panel.child(new TileWidget(this.getLocalizedName()));

        panel.child(
            IKey.lang(data.getPlayer().inventory.getInventoryName())
                .asWidget()
                .pos(8, 72));

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        panel.child(
            new FluidSlot().alignX(0.5f)
                .topRel(0.15f)
                .syncHandler(SyncHandlers.fluidSlot(this.tank)));

        return panel;
    }
}
