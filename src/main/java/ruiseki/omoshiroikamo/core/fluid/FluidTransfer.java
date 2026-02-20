package ruiseki.omoshiroikamo.core.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import com.gtnewhorizon.gtnhlib.blockpos.IBlockPos;
import com.gtnewhorizon.gtnhlib.blockpos.IWorldReferent;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.core.fluid.capability.FluidSink;
import ruiseki.omoshiroikamo.core.fluid.capability.FluidSource;

public class FluidTransfer {

    @Getter
    protected FluidSource source;
    @Getter
    protected FluidSink sink;
    @Setter
    protected int maxPerTransfer = Integer.MAX_VALUE;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;
    @Getter
    protected int transferredThisTick = 0;

    // --- Set source ---
    public void source(FluidSource source) {
        this.source = source;
    }

    public void source(Object source, ForgeDirection side) {
        this.source = FluidUtils.getFluidSource(source, side); //
    }

    public void sink(FluidSink sink) {
        this.sink = sink;
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = FluidUtils.getFluidSink(sink, side);
    }

    public <Coord extends IBlockPos & IWorldReferent> void push(Coord pos, ForgeDirection side) {
        TileEntity self = pos.getWorld()
            .getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        TileEntity adj = pos.getWorld()
            .getTileEntity(pos.getX() + side.offsetX, pos.getY() + side.offsetY, pos.getZ() + side.offsetZ);
        push(self, side, adj);
    }

    public void push(Object self, ForgeDirection side, Object target) {
        source(self, side);
        sink(target, side.getOpposite());
    }

    public <Coord extends IBlockPos & IWorldReferent> void pull(Coord pos, ForgeDirection side) {
        TileEntity self = pos.getWorld()
            .getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        TileEntity adj = pos.getWorld()
            .getTileEntity(pos.getX() + side.offsetX, pos.getY() + side.offsetY, pos.getZ() + side.offsetZ);
        pull(self, side, adj);
    }

    public void pull(Object self, ForgeDirection side, Object target) {
        source(target, side.getOpposite());
        sink(self, side);
    }

    public int transfer() {
        if (source == null || sink == null) {
            return 0;
        }

        int maxTransfer = Math.min(maxPerTransfer, maxTotalTransferred - transferredThisTick);
        if (maxTransfer <= 0) {
            return 0;
        }

        FluidTankInfo[] infos = source.getTankInfo();

        if (infos != null) {
            for (FluidTankInfo info : infos) {
                if (info.fluid == null || info.fluid.amount <= 0 || info.fluid.getFluid() == null) {
                    continue;
                }

                FluidStack request = info.fluid.copy();
                request.amount = maxTransfer;

                FluidStack simulatedPulledStack = source.extract(request, false);
                if (simulatedPulledStack == null || simulatedPulledStack.amount <= 0) {
                    continue;
                }

                int pullAmount = simulatedPulledStack.amount;
                FluidStack stackToInsert = simulatedPulledStack.copy();
                stackToInsert.amount = pullAmount;
                int simulatedAccepted = sink.insert(stackToInsert, false);
                if (simulatedAccepted <= 0) {
                    continue;
                }

                int actualTransferAmount = Math.min(pullAmount, simulatedAccepted);

                FluidStack actualTransferStack = simulatedPulledStack.copy();
                actualTransferStack.amount = actualTransferAmount;

                source.extract(actualTransferStack, true);
                int insertedAmount = sink.insert(actualTransferStack, true);

                transferredThisTick += insertedAmount;

                return insertedAmount;
            }
        }

        return 0;
    }
}
