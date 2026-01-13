package ruiseki.omoshiroikamo.module.chickens.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.module.chickens.client.model.ModelChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.common.block.TERoost;

@SideOnly(Side.CLIENT)
public class RoostTESR extends TileEntitySpecialRenderer {

    private final ModelChickensChicken modelChicken = new ModelChickensChicken();

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {

        if (!(te instanceof TERoost tile)) {
            return;
        }

        DataChicken chicken = tile.getChickenData(0);
        if (chicken == null || chicken.getItem() == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        ForgeDirection facing = tile.getFacing();
        GL11.glRotatef(BlockStateUtils.getFacingAngle(facing), 0F, 1F, 0F);

        ResourceLocation texture = chicken.getItem()
            .getTexture();
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        GL11.glTranslatef(0F, 1.30F, 0F);
        GL11.glRotatef(180F, 0F, 0F, 1F);

        modelChicken.isChild = false;
        modelChicken.renderRoost(0.0625F);

        GL11.glPopMatrix();
    }
}
