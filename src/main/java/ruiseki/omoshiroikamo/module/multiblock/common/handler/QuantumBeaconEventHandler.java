package ruiseki.omoshiroikamo.module.multiblock.common.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.network.PacketClientFlight;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;

/**
 * Event handler for re-syncing flight state after dimension change or respawn
 */
@EventBusSubscriber
public class QuantumBeaconEventHandler {

    /**
     * Flight information granted by a Beacon
     */
    public static class BeaconFlightInfo {

        public final int dimensionId;
        public final int beaconX;
        public final int beaconY;
        public final int beaconZ;

        public BeaconFlightInfo(int dimensionId, int beaconX, int beaconY, int beaconZ) {
            this.dimensionId = dimensionId;
            this.beaconX = beaconX;
            this.beaconY = beaconY;
            this.beaconZ = beaconZ;
        }
    }

    /**
     * Map tracking players with flight granted by a Beacon
     * Key: Player UUID, Value: BeaconFlightInfo
     */
    private static final Map<UUID, BeaconFlightInfo> flightGrantedPlayers = new HashMap<>();

    @EventBusSubscriber.Condition
    public static boolean shouldSubscribe() {
        return BackportConfigs.enableMultiBlock;
    }

    /**
     * Register that a player has been granted flight (with Beacon position info)
     */
    public static void registerFlightGranted(UUID playerId, int dimensionId, int beaconX, int beaconY, int beaconZ) {
        flightGrantedPlayers.put(playerId, new BeaconFlightInfo(dimensionId, beaconX, beaconY, beaconZ));
    }

    /**
     * Unregister a player's flight grant
     */
    public static void unregisterFlightGranted(UUID playerId) {
        flightGrantedPlayers.remove(playerId);
    }

    /**
     * Get a player's flight info
     */
    public static BeaconFlightInfo getFlightInfo(UUID playerId) {
        return flightGrantedPlayers.get(playerId);
    }

    /**
     * Check if a player has been granted flight
     */
    public static boolean isFlightGranted(UUID playerId) {
        return flightGrantedPlayers.containsKey(playerId);
    }

    /**
     * Handle flight state after dimension change
     * Only re-grant flight if returning to the same dimension as the Beacon
     */
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }

        UUID playerId = player.getUniqueID();
        BeaconFlightInfo info = getFlightInfo(playerId);
        if (info != null) {
            // Only re-grant flight if player is in the same dimension as the Beacon
            if (event.toDim == info.dimensionId) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
                PacketHandler.sendToAllAround(new PacketClientFlight(playerId, true), player);
            } else {
                // Disable flight when moving to a different dimension (keep flag for return)
                player.capabilities.allowFlying = false;
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                }
                player.sendPlayerAbilities();
                PacketHandler.sendToAllAround(new PacketClientFlight(playerId, false), player);
            }
        }
    }

    /**
     * Re-grant flight after respawn
     * Only if respawning in the same dimension
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) {
            return;
        }

        UUID playerId = player.getUniqueID();
        BeaconFlightInfo info = getFlightInfo(playerId);
        if (info != null && player.dimension == info.dimensionId) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
            PacketHandler.sendToAllAround(new PacketClientFlight(playerId, true), player);
        }
    }

    /**
     * Handle flight state when joining a world
     */
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityPlayerMP player)) {
            return;
        }

        UUID playerId = player.getUniqueID();
        BeaconFlightInfo info = getFlightInfo(playerId);
        if (info != null) {
            // Only grant flight if in the same dimension as the Beacon
            if (player.dimension == info.dimensionId) {
                if (!player.capabilities.allowFlying) {
                    player.capabilities.allowFlying = true;
                    player.sendPlayerAbilities();
                    PacketHandler.sendToAllAround(new PacketClientFlight(playerId, true), player);
                }
            }
        }
    }
}
