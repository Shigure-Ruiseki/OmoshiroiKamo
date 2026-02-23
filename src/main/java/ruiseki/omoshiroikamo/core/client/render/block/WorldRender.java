/*
 * This file is part of Applied Energistics 2. Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved. Applied
 * Energistics 2 is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Applied Energistics 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details. You should have received a copy of the GNU Lesser General Public License along with
 * Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package ruiseki.omoshiroikamo.core.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.apache.logging.log4j.Level;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.client.render.BaseBlockRender;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = true)
@SuppressWarnings("unchecked")
public final class WorldRender implements ISimpleBlockRenderingHandler {

    public static final WorldRender INSTANCE = new WorldRender();
    private static final int renderID = RenderingRegistry.getNextAvailableRenderId();
    private final RenderBlocks renderer = new RenderBlocks();
    private boolean hasError = false;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block,
        final int modelId, final RenderBlocks renderer) {
        final BlockOK blk = (BlockOK) block;
        renderer.setRenderBoundsFromBlock(block);
        return this.getRender(blk)
            .renderInWorld(blk, world, x, y, z, renderer);
    }

    @Override
    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    private BaseBlockRender getRender(final BlockOK block) {
        return block.getRendererInstance()
            .getRendererInstance();
    }

    public void renderItemBlock(final ItemStack item, final IItemRenderer.ItemRenderType type, final Object[] data) {
        final Block blk = Block.getBlockFromItem(item.getItem());
        if (blk instanceof BlockOK block) {
            this.renderer.setRenderBoundsFromBlock(block);

            this.renderer.uvRotateBottom = this.renderer.uvRotateEast = this.renderer.uvRotateNorth = this.renderer.uvRotateSouth = this.renderer.uvRotateTop = this.renderer.uvRotateWest = 0;
            this.getRender(block)
                .renderInventory(block, item, this.renderer, type, data);
            this.renderer.uvRotateBottom = this.renderer.uvRotateEast = this.renderer.uvRotateNorth = this.renderer.uvRotateSouth = this.renderer.uvRotateTop = this.renderer.uvRotateWest = 0;
        } else {
            if (!this.hasError) {
                this.hasError = true;
                OmoshiroiKamo.okLog(Level.ERROR, "Invalid render - item/block mismatch");
                OmoshiroiKamo.okLog(Level.ERROR, "		item: " + item.getUnlocalizedName());
                OmoshiroiKamo.okLog(Level.ERROR, "		block: " + blk.getUnlocalizedName());
            }
        }
    }
}
