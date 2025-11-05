package ruiseki.omoshiroikamo.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

@EventBusSubscriber(side = Side.CLIENT)
@SuppressWarnings("unused")
public class ClientTickHandler {

    public static int pageFlipTicks = 0;
    public static int ticksInGame = 0;
    public static float partialTicks = 0.0F;
    public static float delta = 0.0F;
    public static float total = 0.0F;
    public static float displayedMana = 0.0F;
    public static float displayedCMana = 0.0F;

    private static void calcDelta() {
        float oldTotal = total;
        total = (float) ticksInGame + partialTicks;
        delta = total - oldTotal;
    }

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            partialTicks = event.renderTickTime;
        } else {
            calcDelta();
        }

    }

    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null || !gui.doesGuiPauseGame()) {
                ticksInGame++;
                partialTicks = 0.0F;
            }
            calcDelta();
        }
    }

    public static void notifyPageChange() {
        if (pageFlipTicks == 0) {
            pageFlipTicks = 5;
        }
    }

}
