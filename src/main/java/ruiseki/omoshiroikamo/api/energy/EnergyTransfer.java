package ruiseki.omoshiroikamo.api.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.IBlockPos;
import com.gtnewhorizon.gtnhlib.blockpos.IWorldReferent;

import ruiseki.omoshiroikamo.api.energy.capability.EnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.EnergySource;

public class EnergyTransfer {

    protected EnergySource source;
    protected EnergySink sink;

    protected int maxEnergyPerTransfer = Integer.MAX_VALUE;
    protected int maxTotalTransferred = Integer.MAX_VALUE;

    protected int totalEnergyTransferred = 0;
    protected int prevEnergyTransferred = 0;

    // --- Set source ---
    public void source(EnergySource source) {
        this.source = source;
    }

    public void source(Object source, ForgeDirection side) {
        this.source = EnergyUtils.getEnergySource(source, side);
    }

    public void sink(EnergySink sink) {
        this.sink = sink;
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = EnergyUtils.getEnergySink(sink, side);
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

        // 1. Simulate extraction
        int toExtract = Math.min(maxEnergyPerTransfer, maxTotalTransferred);
        int simulatedPull = source.extract(toExtract, true);
        if (simulatedPull <= 0) {
            return 0;
        }

        // 2. Simulate insert
        int simulatedAccepted = sink.insert(simulatedPull, true);
        if (simulatedAccepted <= 0) {
            return 0;
        }

        // 3. Do actual extraction & insertion
        int pulled = source.extract(simulatedAccepted, false);
        int accepted = sink.insert(pulled, false);

        totalEnergyTransferred += accepted;
        prevEnergyTransferred = accepted;

        return accepted;
    }
}
