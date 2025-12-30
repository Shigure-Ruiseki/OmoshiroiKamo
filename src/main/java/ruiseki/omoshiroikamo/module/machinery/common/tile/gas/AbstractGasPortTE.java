package ruiseki.omoshiroikamo.module.machinery.common.tile.gas;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import ruiseki.omoshiroikamo.api.enums.RedstoneMode;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.storage.SmartGasTank;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

public abstract class AbstractGasPortTE extends AbstractTE implements IModularPort, IGasHandler {

    protected final IO[] sides = new IO[6];

    private final SmartGasTank tank;
    private boolean tankDirty = false;

    public AbstractGasPortTE(int gasCapacity) {
        tank = new SmartGasTank(gasCapacity) {

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
    public Type getPortType() {
        return Type.GAS;
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
        tank.writeCommon(root);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
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

    public boolean canReceiveGas(ForgeDirection from) {
        return canInput(from) && isRedstoneActive();
    }

    @Override
    public boolean canReceiveGas(ForgeDirection from, Gas gas) {
        return canReceiveGas(from) && tank.canReceive(gas);
    }

    @Override
    public int receiveGas(ForgeDirection forgeDirection, GasStack gasStack, boolean doTransfer) {
        if (!canReceiveGas(forgeDirection)) {
            return 0;
        }
        int res = tank.receive(gasStack, doTransfer);
        if (res > 0 && doTransfer) {
            tankDirty = true;
        }
        return res;
    }

    public boolean canDrawGas(ForgeDirection from) {
        return canOutput(from) && isRedstoneActive();
    }

    @Override
    public boolean canDrawGas(ForgeDirection from, Gas gas) {
        return canDrawGas(from) && tank.canDraw(gas);
    }

    @Override
    public GasStack drawGas(ForgeDirection forgeDirection, int amount, boolean doTransfer) {
        if (!canDrawGas(forgeDirection)) {
            return null;
        }
        GasStack res = tank.draw(amount, doTransfer);
        if (res != null && res.amount > 0 && doTransfer) {
            tankDirty = true;
        }
        return res;
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

        // panel.child(
        // new FluidSlot().alignX(0.5f)
        // .topRel(0.15f)
        // .syncHandler(SyncHandlers.fluidSlot(this.tank)));

        return panel;
    }
}
