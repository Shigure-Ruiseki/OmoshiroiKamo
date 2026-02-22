/*
 * This file is part of Applied Energistics 2. Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved. Applied
 * Energistics 2 is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Applied Energistics 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details. You should have received a copy of the GNU Lesser General Public License along with
 * Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package ruiseki.omoshiroikamo.core.client.render.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.client.render.BaseBlockRender;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

@SideOnly(Side.CLIENT)
public class TESRWrapper<T extends TileEntityOK> extends TileEntitySpecialRenderer {

    private final RenderBlocks renderBlocksInstance = new RenderBlocks();

    private final BaseBlockRender blkRender;
    private final double maxDistance;

    public TESRWrapper(final BaseBlockRender render) {
        this.blkRender = render;
        this.maxDistance = this.blkRender.getTesrRenderDistance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        if (!(tile instanceof TileEntityOK)) {
            return;
        }

        T te = (T) tile;
        final Block b = te.getBlockType();

        if (b instanceof BlockOK blockOK && te.requiresTESR()) {
            if (Math.abs(x) > this.maxDistance || Math.abs(y) > this.maxDistance || Math.abs(z) > this.maxDistance) {
                return;
            }

            final Tessellator tess = TessellatorManager.get();

            try {
                GL11.glPushMatrix();

                this.renderBlocksInstance.blockAccess = te.getWorldObj();
                this.blkRender.renderTile(blockOK, te, tess, x, y, z, partialTicks, this.renderBlocksInstance);

                GL11.glPopMatrix();
            } catch (final Throwable t) {
                OmoshiroiKamo.okLog(Level.ERROR, "Hi, Looks like there was a crash while rendering something...");
                t.printStackTrace();
                OmoshiroiKamo.okLog(Level.ERROR, "MC will now crash ( probably )!");
                throw new IllegalStateException(t);
            }
        }
    }
}
