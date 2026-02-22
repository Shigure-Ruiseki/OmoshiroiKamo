package ruiseki.omoshiroikamo.core.client.render.tileentity;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class TESROK<T extends TileEntity> extends TileEntitySpecialRenderer {

    public EntityItem slotEntity = new EntityItem(null, 0.0D, 0.0D, 0.0D);

    protected RenderManager renderManager = RenderManager.instance;
    protected RenderItem itemRenderer = new RenderItem() {

        public byte getMiniBlockCount(ItemStack stack, byte original) {
            return 1;
        }

        public boolean shouldBob() {
            return false;
        }

        public boolean shouldSpreadItems() {
            return false;
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        T te = (T) tile;
        renderTileEntity(te, x, y, z, partialTicks);
    }

    protected abstract void renderTileEntity(T tile, double x, double y, double z, float partialTicks);

    public void updateItem(ItemStack stack) {
        this.slotEntity.setEntityItemStack(stack);
        this.slotEntity.hoverStart = 0.0F;
    }

    public void renderFrameItem() {
        RenderItem.renderInFrame = true;
        try {
            this.itemRenderer.doRender(slotEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        } catch (RuntimeException ignored) {}
        RenderItem.renderInFrame = false;
    }
}
