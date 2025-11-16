package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.IBlockPos;
import com.gtnewhorizon.gtnhlib.blockpos.IWorldReferent;

public class EnergyTransfer {

    protected EnergySource source;
    protected EnergySink sink;

    protected int maxEnergyPerTransfer = Integer.MAX_VALUE;
    protected int maxTotalTransferred = Integer.MAX_VALUE;

    protected int totalEnergyTransferred = 0;
    protected int prevEnergyTransferred = 0;

    public void source(EnergySource source) {
        this.source = source;
    }

    public void source(Object source, ForgeDirection side) {
        this.source = EnergyUtil.getEnergySource(source, side);
    }

    public void sink(EnergySink sink) {
        this.sink = sink;
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = EnergyUtil.getEnergySink(sink, side);
    }

    public <Coord extends IBlockPos & IWorldReferent> void push(Coord pos, ForgeDirection side) {
        TileEntity self = pos.getWorld()
            .getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        TileEntity adjacent = pos.getWorld()
            .getTileEntity(pos.getX() + side.offsetX, pos.getY() + side.offsetY, pos.getZ() + side.offsetZ);

        push(self, side, adjacent);
    }

    public void push(Object self, ForgeDirection side, Object target) {
        source(self, side);
        sink(target, side.getOpposite());
    }

    public <Coord extends IBlockPos & IWorldReferent> void pull(Coord pos, ForgeDirection side) {
        TileEntity self = pos.getWorld()
            .getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        TileEntity adjacent = pos.getWorld()
            .getTileEntity(pos.getX() + side.offsetX, pos.getY() + side.offsetY, pos.getZ() + side.offsetZ);

        pull(self, side, adjacent);
    }

    public void pull(Object self, ForgeDirection side, Object target) {
        source(target, side.getOpposite());
        sink(self, side);
    }

    public int transfer() {
        if (source == null || sink == null) {
            return 0;
        }

        source.resetSource();
        sink.resetSink();

        EnergyAccess sourceAcc = source.sourceAccess();
        EnergyAccess sinkAcc = sink.sinkAccess();

        if (sourceAcc == null || sinkAcc == null) {
            return 0;
        }

        int energyTransferred = 0;
        int remaining = maxTotalTransferred;

        while (remaining > 0) {
            int extractAmount = Math.min(maxEnergyPerTransfer, remaining);

            // Lấy điện từ nguồn
            int pulled = sourceAcc.extract(ForgeDirection.UNKNOWN, extractAmount, false);
            if (pulled <= 0) {
                break;
            }

            // Đẩy điện vào sink
            int rejected = sinkAcc.insert(ForgeDirection.UNKNOWN, pulled, false);
            int accepted = pulled - rejected;

            if (accepted <= 0) {
                break;
            }

            energyTransferred += accepted;
            remaining -= accepted;
        }

        totalEnergyTransferred += energyTransferred;
        prevEnergyTransferred = energyTransferred;

        return energyTransferred;
    }
}
