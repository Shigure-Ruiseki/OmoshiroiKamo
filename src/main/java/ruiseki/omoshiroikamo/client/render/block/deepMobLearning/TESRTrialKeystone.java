package ruiseki.omoshiroikamo.client.render.block.deepMobLearning;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.common.block.deepMobLearning.trialKeystone.TETrialKeystone;

public class TESRTrialKeystone extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        TETrialKeystone keystone = (TETrialKeystone) tile;

    }
}
