package ruiseki.omoshiroikamo.module.ids.common.cable;

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

import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICableEndpoint;
import ruiseki.omoshiroikamo.api.ids.ICableNode;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractCableNetwork;
import ruiseki.omoshiroikamo.module.ids.common.network.CableNetworkRegistry;

public class CableUtils {

    private CableUtils() {}

    public static void rebuildNetworks(ICable cable) {
        World world = cable.getWorld();
        if (world == null) return;

        CableCluster cluster = collectCluster(world, cable);
        rebuildCluster(cluster);
    }

    private static void rebuildCluster(CableCluster cluster) {
        if (cluster.cables.isEmpty()) return;

        Set<AbstractCableNetwork<?>> destroyed = new HashSet<>();

        for (ICable cable : cluster.cables) {
            Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> nets = cable.getNetworks();
            if (nets != null && !nets.isEmpty()) {
                for (AbstractCableNetwork<?> net : new ArrayList<>(nets.values())) {
                    if (net != null && destroyed.add(net)) {
                        net.destroyNetwork();
                    }
                }
            }
            cable.setNetworks(new HashMap<>());
        }

        Map<Class<? extends ICableNode>, AbstractCableNetwork<?>> newNetworks = new HashMap<>();

        for (ICable cable : cluster.cables) {
            if (!cable.hasCore()) continue;
            for (ICablePart part : cable.getParts()) {
                List<Class<? extends ICableNode>> types = part.getBaseNodeTypes();
                if (types == null || types.isEmpty()) continue;

                for (Class<? extends ICableNode> type : types) {
                    if (type == null) continue;

                    AbstractCableNetwork<?> net = newNetworks.computeIfAbsent(type, CableNetworkRegistry::create);

                    net.addNode(part);
                }
            }
        }

        for (ICable cable : cluster.cables) {
            if (!cable.hasCore()) continue;
            for (ICableEndpoint part : cable.getEndpoints()) {
                List<Class<? extends ICableNode>> types = part.getBaseNodeTypes();
                if (types == null || types.isEmpty()) continue;

                for (Class<? extends ICableNode> type : types) {
                    if (type == null) continue;

                    AbstractCableNetwork<?> net = newNetworks.computeIfAbsent(type, CableNetworkRegistry::create);

                    net.addNode(part);
                }
            }
        }

        for (ICable cable : cluster.cables) {
            cable.setNetworks(new HashMap<>(newNetworks));
            cable.dirty();
        }
    }

    private static CableCluster collectCluster(World world, ICable start) {
        CableCluster cluster = new CableCluster();
        Queue<ICable> queue = new ArrayDeque<>();
        Set<ICable> visited = new HashSet<>();

        if (!start.hasCore()) return cluster;

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

                if (otherTe instanceof ICable other && other.hasCore() && visited.add(other)) {
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
