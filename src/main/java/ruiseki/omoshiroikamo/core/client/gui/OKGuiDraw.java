package ruiseki.omoshiroikamo.core.client.gui;

import com.cleanroommc.modularui.drawable.text.TextRenderer;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.GlStateManager;

public class OKGuiDraw {

    private static final TextRenderer textRenderer = new TextRenderer();

    public static final double PI2 = Math.PI * 2;
    public static final double PI_2 = Math.PI / 2;

    public static void drawCompactAmount(int amount, int x, int y, int width, int height, Alignment alignment) {
        if (amount <= 1) return;

        String text = formatCompactAmount(amount);
        final float SCALE = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.translate(0, 0, 200);

        textRenderer.setShadow(true);
        textRenderer.setScale(SCALE);
        textRenderer.setColor(Color.WHITE.main);

        textRenderer.setAlignment(alignment, width, height);
        textRenderer.setPos(x, y);
        textRenderer.setHardWrapOnBorder(false);

        textRenderer.draw(text);

        textRenderer.setHardWrapOnBorder(true);
        GlStateManager.popMatrix();
    }

    public static void drawCraftable(int x, int y, int width, int height, Alignment alignment) {

        final String text = "Craft";
        final float SCALE = 0.5f;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.translate(0, 0, 200);

        textRenderer.setShadow(true);
        textRenderer.setScale(SCALE);
        textRenderer.setColor(Color.YELLOW.main);

        textRenderer.setAlignment(alignment, width, height);
        textRenderer.setPos(x, y);
        textRenderer.setHardWrapOnBorder(false);

        textRenderer.draw(text);

        textRenderer.setHardWrapOnBorder(true);
        GlStateManager.popMatrix();
    }

    private static String formatCompactAmount(int amount) {
        if (amount < 1000) {
            return String.valueOf(amount);
        }

        if (amount < 1_000_000) {
            int k = amount / 1000;
            int rem = (amount % 1000) / 100;
            return rem > 0 ? k + "k" + rem : k + "k";
        }

        int m = amount / 1_000_000;
        int rem = (amount % 1_000_000) / 100_000;
        return rem > 0 ? m + "M" + rem : m + "M";
    }

}
