package ruiseki.omoshiroikamo.client.render.item.backpack;

import java.awt.Color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.client.handler.ClientTickHandler;
import ruiseki.omoshiroikamo.common.item.upgrade.EnergyUpgrade;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

@SideOnly(Side.CLIENT)
public class BackpackRenderer implements IItemRenderer {

    public static final IModelCustom model = AdvancedModelLoader
        .loadModel(new ResourceLocation(LibResources.PREFIX_MODEL + "backpack_base.obj"));

    private static final ResourceLocation border = new ResourceLocation(
        LibResources.PREFIX_ITEM + "backpack_border.png");
    private static final ResourceLocation cloth = new ResourceLocation(LibResources.PREFIX_ITEM + "backpack_cloth.png");

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();

        switch (type) {
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(1.75f, 1.75f, 1.75f);
                GL11.glTranslatef(0.3f, 0f, 0.3f);
                GL11.glRotatef(90f, 0f, 1f, 0f);
                break;
            case INVENTORY:
                GL11.glScalef(1.25f, 1.25f, 1.25f);
                GL11.glTranslatef(0.5f, 0f, 0.5f);
                GL11.glRotatef(-80f, 0F, 1f, 0f);
                break;
            case EQUIPPED:
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                break;

            case ENTITY:
                GL11.glScalef(0.75f, 0.75f, 0.75f);
                GL11.glTranslatef(0.0f, -0.5f, 0.0f);
                GL11.glRotatef(-90, 0f, 1f, 0f);
                break;
            default:
                break;
        }
        renderModel(item);
        if (type == ItemRenderType.INVENTORY) {
            renderBars(item);
        }

        GL11.glPopMatrix();
    }

    public void renderModel(ItemStack item) {
        GL11.glColor3f(0.353f, 0.243f, 0.106f);
        RenderUtil.bindTexture(border);
        model.renderOnly("trim1", "trim2", "trim3", "trim4", "trim5", "padding1");

        int color = EnumDye.BROWN.getColor();
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag != null && tag.hasKey("BackpackColor")) {
                color = tag.getInteger("BackpackColor");
            }
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        float brightnessFactor = 1.18f;
        r = Math.min(1.0f, r * brightnessFactor);
        g = Math.min(1.0f, g * brightnessFactor);
        b = Math.min(1.0f, b * brightnessFactor);

        GL11.glColor3f(r, g, b);
        RenderUtil.bindTexture(cloth);
        model.renderOnly(
            "inner1",
            "inner2",
            "outer1",
            "outer2",
            "left_trim1",
            "right_trim1",
            "bottom_trim1",
            "body",
            "pouch1",
            "pouch2",
            "top1",
            "top2",
            "top3",
            "bottom1",
            "bottom2",
            "bottom3",
            "lip1");

        GL11.glColor3f(1f, 1f, 1f);

        String material;
        switch (item.getItemDamage()) {
            case 1:
                material = "copper";
                break;
            case 2:
                material = "iron";
                break;
            case 3:
                material = "gold";
                break;
            case 4:
                material = "diamond";
                break;
            case 5:
                material = "netherite";
                break;
            default:
                material = "leather";
                break;
        }
        RenderUtil.bindTexture(new ResourceLocation(LibResources.PREFIX_ITEM + material + "_clips.png"));
        model.renderOnly(
            "top4",
            "right1",
            "right2",
            "right_clip1",
            "right_clip2",
            "left1",
            "left2",
            "left_clip1",
            "left_clip2",
            "clip1",
            "clip2",
            "clip3",
            "clip4",
            "opening1",
            "opening2",
            "opening3",
            "opening4",
            "opening5");
    }

    private void renderBars(ItemStack item) {
        if (EnergyUpgrade.loadFromItem(item) == null
            || (!ItemConfigs.renderChargeBar && !ItemConfigs.renderDurabilityBar)) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0f, 0f, -0.8f);
        GL11.glRotatef(-55, 0F, 1f, 0f);
        GL11.glScalef(1f / 12f, 1f / 12f, 1f / 12f);
        GL11.glDisable(GL11.GL_LIGHTING);

        boolean hasEnergyUpgrade = EnergyUpgrade.loadFromItem(item) != null;

        double maxDam, dispDamage;

        if (ItemConfigs.renderChargeBar && hasEnergyUpgrade) {
            IEnergyContainerItem backpack = (IEnergyContainerItem) item.getItem();
            maxDam = backpack.getMaxEnergyStored(item);
            dispDamage = backpack.getEnergyStored(item);
            Color color = new Color(
                Color.HSBtoRGB(
                    0.9F,
                    ((float) Math.sin((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.2F) + 1F)
                        * 0.3F + 0.4F,
                    1F));

            renderBar2(0, maxDam, maxDam - dispDamage, color, color);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public static void renderBar2(int y, double maxDam, double dispDamage, Color full, Color empty) {
        double ratio = dispDamage / maxDam;
        Vector4f fg = ColorUtil.toFloat(full);
        Vector4f ec = ColorUtil.toFloat(empty);
        fg.interpolate(ec, (float) ratio);
        Vector4f bg = ColorUtil.toFloat(Color.black);
        bg.interpolate(fg, 0.15f);

        int barLength = (int) Math.round(12.0 * (1 - ratio));

        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bg);
        RenderUtil.renderQuad2D(2 + (12 - barLength), y, 0, barLength, 1, fg);
    }
}
