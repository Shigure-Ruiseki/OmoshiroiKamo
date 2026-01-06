package ruiseki.omoshiroikamo.module.cable.common.cable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.cable.common.network.CableNetworkRegistry;

public final class CableUtils {

    private CableUtils() {}

    public static void rebuildNetworks(ICable cable) {
        World world = cable.getWorld();
        if (world == null) return;

        CableCluster cluster = collectCluster(world, cable);
        rebuildCluster(cluster);
    }

    public static void joinCables(ICable cable, ForgeDirection dir) {
        TileEntity te = cable.getTileEntity();
        if (te == null) return;

        TileEntity otherTe = te.getWorldObj()
            .getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);

        if (!(otherTe instanceof ICable other)) return;
        if (!cable.canConnect(otherTe, dir) || !other.canConnect(te, dir.getOpposite())) return;

        cable.connect(dir);
        other.connect(dir.getOpposite());

        rebuildNetworks(cable);
    }

    public static void disconnectCable(ICable cable, ForgeDirection dir) {
        cable.disconnect(dir);

        TileEntity te = cable.getTileEntity();
        if (te == null) return;

        TileEntity otherTe = te.getWorldObj()
            .getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);

        if (otherTe instanceof ICable other) {
            other.disconnect(dir.getOpposite());
            rebuildNetworks(other);
        }

        rebuildNetworks(cable);
    }

    private static void rebuildCluster(CableCluster cluster) {

        Set<AbstractCableNetwork<?>> destroyed = new HashSet<>();

        for (ICable cable : cluster.cables) {
            Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> nets = cable.getNetworks();
            if (nets != null) {
                for (AbstractCableNetwork<?> net : nets.values()) {
                    if (net != null && destroyed.add(net)) {
                        net.destroyNetwork();
                    }
                }
            }
            cable.setNetworks(new HashMap<>());
        }

        Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> newNetworks = new HashMap<>();

        for (ICable cable : cluster.cables) {
            for (ICablePart part : cable.getParts()) {
                Class<? extends ICablePart> type = part.getBasePartType();

                AbstractCableNetwork<?> net = newNetworks.computeIfAbsent(type, CableNetworkRegistry::create);

                net.addPart(part);
            }
        }

        for (ICable cable : cluster.cables) {
            Map<Class<? extends ICablePart>, AbstractCableNetwork<?>> map = new HashMap<>();
            map.putAll(newNetworks);
            cable.setNetworks(map);
            cable.dirty();
        }
    }

    private static CableCluster collectCluster(World world, ICable start) {
        CableCluster cluster = new CableCluster();
        Queue<ICable> queue = new ArrayDeque<>();
        Set<ICable> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            ICable cable = queue.poll();
            cluster.cables.add(cable);

            TileEntity te = cable.getTileEntity();
            if (te == null) continue;

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                if (!cable.isConnected(dir)) continue;

                TileEntity otherTe = world
                    .getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);

                if (otherTe instanceof ICable other && visited.add(other)) {
                    queue.add(other);
                }
            }
        }

        return cluster;
    }

    private static final class CableCluster {

        final List<ICable> cables = new ArrayList<>();
    }
}
