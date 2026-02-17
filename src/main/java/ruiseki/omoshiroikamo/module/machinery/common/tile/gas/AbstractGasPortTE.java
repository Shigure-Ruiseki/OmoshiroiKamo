package ruiseki.omoshiroikamo.module.machinery.common.tile.gas;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Column;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.ITubeConnection;
import ruiseki.omoshiroikamo.api.block.RedstoneMode;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.gas.GasTankInfo;
import ruiseki.omoshiroikamo.api.gas.IGasHandler;
import ruiseki.omoshiroikamo.api.gas.SmartGasTank;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.core.client.gui.widget.TileWidget;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.client.gui.widget.RedstoneModeWidget;

/*
 * Mekanism Handle Push/Pull itself so skip Auto PushPull
 */
public abstract class AbstractGasPortTE extends AbstractTE
    implements IModularPort, IGasHandler, ITubeConnection, IGuiHolder<PosGuiData> {

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];

    @NBTPersist
    protected final SmartGasTank tank;
    protected boolean tankDirty = false;

    public AbstractGasPortTE(int gasCapacity) {
        tank = new SmartGasTank(gasCapacity) {

            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                markDirty();
            }
        };
        // Default IO is NONE, handled by Block.onBlockPlacedBy
    }

    public abstract int getTier();

    public abstract EnumIO getIOLimit();

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
    public EnumIO getSideIO(ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN || side.ordinal() >= 6) {
            return EnumIO.NONE;
        }
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        if (side == ForgeDirection.UNKNOWN || side.ordinal() >= 6) {
            return;
        }
        sides[side.ordinal()] = state;
        forceRenderUpdate();
    }

    @Override
    public void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
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

    public GasStack internalDrawGas(int amount, boolean doTransfer) {
        GasStack res = tank.draw(amount, doTransfer);
        if (res != null && res.amount > 0 && doTransfer) {
            tankDirty = true;
        }
        return res;
    }

    public int internalReceiveGas(GasStack gasStack, boolean doTransfer) {
        int res = tank.receive(gasStack, doTransfer);
        if (res > 0 && doTransfer) {
            tankDirty = true;
        }
        return res;
    }

    @Override
    public int receiveGas(ForgeDirection from, GasStack gasStack) {
        return this.receiveGas(from, gasStack, true);
    }

    @Override
    public GasStack drawGas(ForgeDirection from, int amount) {
        return this.drawGas(from, amount, true);
    }

    @Override
    public GasTankInfo[] getTankInfo(ForgeDirection from) {
        return new GasTankInfo[] { tank.getInfo() };
    }

    // ITubeConnection - allows Mekanism cables to connect
    @Override
    public boolean canTubeConnect(ForgeDirection side) {
        return getSideIO(side) != EnumIO.NONE;
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
        ModularPanel panel = new ModularPanel("gas_port");

        EnumSyncValue<RedstoneMode> redstoneSyncer = new EnumSyncValue<>(
            RedstoneMode.class,
            this::getRedstoneMode,
            this::setRedstoneMode);
        syncManager.syncValue("redstoneSyncer", redstoneSyncer);

        IntSyncValue gasSyncer = new IntSyncValue(tank::getStored, tank::setAmount);
        syncManager.syncValue("gasSyncer", gasSyncer);
        syncManager.syncValue("maxGasSyncer", new IntSyncValue(tank::getMaxGas));

        panel.child(
            new RedstoneModeWidget(redstoneSyncer).pos(-20, 2)
                .size(18)
                .excludeAreaInRecipeViewer());

        panel.child(new TileWidget(this.getLocalizedName()));

        panel.child(
            IKey.lang(data.getPlayer().inventory.getInventoryName())
                .asWidget()
                .pos(8, 72));

        Column column = (Column) new Column().coverChildren()
            .childPadding(2)
            .alignX(0.5f)
            .topRel(0.15f);

        column.child(
            new ProgressWidget().value(new DoubleValue((double) this.tank.getStored() / this.tank.getMaxGas()))
                .texture(OKGuiTextures.BASIC_BAR, 64)
                .size(64, 16));

        column.child(
            IKey.dynamic(() -> this.tank.getStored() + "/" + this.tank.getMaxGas())
                .asWidget());

        column.child(
            IKey.dynamic(
                () -> this.tank.getGas() != null ? this.tank.getGas()
                    .getGas()
                    .getLocalizedName() : "")
                .asWidget());

        panel.child(column);

        syncManager.bindPlayerInventory(data.getPlayer());
        panel.bindPlayerInventory();

        return panel;
    }

}
