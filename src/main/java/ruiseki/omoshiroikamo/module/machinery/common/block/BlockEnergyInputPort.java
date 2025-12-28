package ruiseki.omoshiroikamo.module.machinery.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEEnergyInputPort;

/**
 * Energy Input Port - accepts energy (RF) for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 * 
 * TODO List:
 * - Implement RF energy storage and transfer
 * - Add GUI for viewing energy level
 * - Support for different tiers with varying capacity/transfer rate
 * - Add visual indicator for energy level (texture animation or overlay)
 * - Implement BlockColor tinting for machine color customization
 * - Add comparator output for energy monitoring
 * - Support EU (IC2) input mode (configurable)
 * - Add Tesla coil-style wireless energy input
 */
public class BlockEnergyInputPort extends AbstractBlock<TEEnergyInputPort> {

    protected BlockEnergyInputPort() {
        super("modularEnergyInput", TEEnergyInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockEnergyInputPort create() {
        return new BlockEnergyInputPort();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Display current RF stored / max capacity
        // TODO: Show energy transfer rate
        // TODO: Show connected machine name if part of structure
    }
}
