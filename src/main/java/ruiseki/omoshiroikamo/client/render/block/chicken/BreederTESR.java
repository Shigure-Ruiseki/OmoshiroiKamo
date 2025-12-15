package ruiseki.omoshiroikamo.client.render.block.chicken;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.client.models.ModelChickensChicken;
import ruiseki.omoshiroikamo.common.block.chicken.TEBreeder;

@SideOnly(Side.CLIENT)
public class BreederTESR extends TileEntitySpecialRenderer {

    private final ModelChickensChicken modelChicken = new ModelChickensChicken();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        if (!(te instanceof TEBreeder tile)) {
            return;
        }

        // Do not render if the breeder is active (working)
        // Use world metadata directly to avoid TileEntity cache latency
        if (tile.getWorldObj()
            .getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord) == 1) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        // Render first chicken (Left slot)
        renderChicken(tile, 0, -0.25, 0, 0, 270f);

        // Render second chicken (Right slot)
        renderChicken(tile, 1, 0.25, 0, 0, 90f);

        GL11.glPopMatrix();
    }

    private void renderChicken(TEBreeder tile, int slotIndex, double xOffset, double yOffset, double zOffset,
        float rotation) {
        DataChicken chicken = tile.getChickenData(slotIndex);
        if (chicken != null && chicken.getItems() != null) {
            GL11.glPushMatrix();

            // Positioning
            GL11.glTranslated(xOffset, yOffset, zOffset);

            // Scaling (Small chicken)
            float scale = 0.4f;
            GL11.glScalef(scale, scale, scale);

            // Rotation
            GL11.glRotatef(rotation, 0F, 1F, 0F);

            ResourceLocation CHICKEN_TEXTURE = chicken.getItems()
                .getTexture();
            Minecraft.getMinecraft().renderEngine.bindTexture(CHICKEN_TEXTURE);

            GL11.glTranslatef(0F, 1.30F + 0.2F, 0F); // Adjusted Y slightly because scaling works from center
            GL11.glRotatef(180F, 0F, 0F, 1F);

            modelChicken.isChild = false;
            modelChicken.renderRoost(0.0625F);

            GL11.glPopMatrix();
        }
    }
}
