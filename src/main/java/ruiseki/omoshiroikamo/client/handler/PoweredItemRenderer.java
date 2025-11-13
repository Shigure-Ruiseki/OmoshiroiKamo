package ruiseki.omoshiroikamo.client.handler;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;

import cofh.api.energy.IEnergyContainerItem;
import ruiseki.omoshiroikamo.common.item.upgrade.EnergyUpgrade;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

public class PoweredItemRenderer implements IItemRenderer {

    private static RenderItem ri = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (data != null && data.length > 0) {
            renderToInventory(item, (RenderBlocks) data[0]);
        }
    }

    public static void renderToInventory(ItemStack item, RenderBlocks renderBlocks) {

        Minecraft mc = Minecraft.getMinecraft();
        ri.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), item, 0, 0, true);
        GL11.glDisable(GL11.GL_LIGHTING);

        if (isJustCrafted(item) || (!ItemConfigs.renderChargeBar && !ItemConfigs.renderDurabilityBar)) {
            return;
        }

        boolean hasEnergyUpgrade = EnergyUpgrade.loadFromItem(item) != null;
        int y = (ItemConfigs.renderDurabilityBar ^ ItemConfigs.renderChargeBar) || !hasEnergyUpgrade ? 13 : 12;
        int bgH = (ItemConfigs.renderDurabilityBar ^ ItemConfigs.renderChargeBar) || !hasEnergyUpgrade ? 2 : 4;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.renderQuad2D(2, y, 0, 13, bgH, ColorUtil.getRGB(Color.black));

        double maxDam, dispDamage;
        if (ItemConfigs.renderDurabilityBar) {
            maxDam = item.getMaxDamage();
            dispDamage = item.getItemDamageForDisplay();
            y = ItemConfigs.renderChargeBar && hasEnergyUpgrade ? 14 : 13;
            renderBar(y, maxDam, dispDamage, Color.green, Color.red);
        }

        if (ItemConfigs.renderChargeBar && hasEnergyUpgrade) {
            IEnergyContainerItem armor = (IEnergyContainerItem) item.getItem();
            maxDam = armor.getMaxEnergyStored(item);
            dispDamage = armor.getEnergyStored(item);
            y = ItemConfigs.renderDurabilityBar ? 12 : 13;
            Color color = new Color(
                Color.HSBtoRGB(
                    0.9F,
                    ((float) Math.sin((ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) * 0.2F) + 1F)
                        * 0.3F + 0.4F,
                    1F));

            renderBar2(y, maxDam, maxDam - dispDamage, color, color);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public static boolean isJustCrafted(ItemStack item) {
        return EnergyUpgrade.loadFromItem(item) == null && item.getItemDamageForDisplay() == 0;
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
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fg);
    }

    public static void renderBar(int y, double maxDam, double dispDamage, Color full, Color empty) {
        double ratio = dispDamage / maxDam;
        Vector4f fg = ColorUtil.toFloat(full);
        Vector4f ec = ColorUtil.toFloat(empty);

        fg.interpolate(ec, (float) ratio);

        Vector4f bg = new Vector4f(0.17, 0.3, 0.1, 0);

        int barLength = (int) Math.round(12.0 * (1 - ratio));
        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bg);
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fg);
    }

    private static void renderBar(int y, double maxDam, double dispDamage) {
        int ratio = (int) Math.round(255.0D - dispDamage * 255.0D / maxDam);
        int fgCol = 255 - ratio << 16 | ratio << 8;
        int bgCol = (255 - ratio) / 4 << 16 | 16128;
        int barLength = (int) Math.round(12.0D - dispDamage * 12.0D / maxDam);
        RenderUtil.renderQuad2D(2, y, 0, 12, 1, bgCol);
        RenderUtil.renderQuad2D(2, y, 0, barLength, 1, fgCol);
    }
}
