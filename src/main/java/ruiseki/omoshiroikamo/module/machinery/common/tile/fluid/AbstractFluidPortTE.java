package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;

import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.fluid.SmartTank;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

public abstract class AbstractFluidPortTE extends AbstractTE
    implements IModularPort, IFluidHandler, IGuiHolder<PosGuiData> {

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];

    @NBTPersist
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
        Arrays.fill(sides, EnumIO.NONE);
        // Default IO is NONE, handled by Block.onBlockPlacedBy
    }

    public abstract int getTier();

    public abstract EnumIO getIOLimit();

    @Override
    public Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public abstract Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN || side.ordinal() >= 6) {
            return EnumIO.NONE;
        }
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        sides[side.ordinal()] = state;
        forceRenderUpdate();
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
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

    // ========== Internal Access for Machine Controller ==========

    public FluidStack internalDrain(FluidStack resource, boolean doDrain) {
        FluidStack res = tank.drain(resource, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
    }

    public FluidStack internalDrain(int maxDrain, boolean doDrain) {
        FluidStack res = tank.drain(maxDrain, doDrain);
        if (res != null && res.amount > 0 && doDrain) {
            tankDirty = true;
        }
        return res;
    }

    public int internalFill(FluidStack resource, boolean doFill) {
        int res = tank.fill(resource, doFill);
        if (res > 0 && doFill) {
            tankDirty = true;
        }
        return res;
    }

    public FluidStack getStoredFluid() {
        return tank.getFluid();
    }

    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        openGui(player);
        return true;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(LibMisc.MOD_ID, mainPanel);
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
