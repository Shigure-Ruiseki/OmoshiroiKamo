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
import ruiseki.omoshiroikamo.common.block.chicken.TERoost;

@SideOnly(Side.CLIENT)
public class RoostTESR extends TileEntitySpecialRenderer {

    private final ModelChickensChicken modelChicken = new ModelChickensChicken();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

        if (!(te instanceof TERoost tile)) {
            return;
        }

        DataChicken chicken = tile.getChickenData(0);
        if (chicken != null && chicken.getItems() != null) {

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y, z + 0.5);

            GL11.glRotatef(getRotationAngle(tile.getFacing()), 0F, 1F, 0F);

            ResourceLocation CHICKEN_TEXTURE = chicken.getItems()
                    .getTexture();
            Minecraft.getMinecraft().renderEngine.bindTexture(CHICKEN_TEXTURE);

            GL11.glTranslatef(0F, 1.30F, 0F);
            GL11.glRotatef(180F, 0F, 0F, 1F);

            modelChicken.isChild = false;
            modelChicken.renderRoost(0.0625F);

            GL11.glPopMatrix();
        }

    }

    private float getRotationAngle(int facing) {
        switch (facing) {
            case 0:
                return 270;
            case 1:
                return 90f;
            case 2:
                return 0f;
            case 3:
                return 180f;
        }
        return 0f;
    }
}
