package ruiseki.omoshiroikamo.core.event;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.omoshiroikamo.config.GeneralConfig;
import ruiseki.omoshiroikamo.core.update.UpdateChecker;

/**
 * Handles notification display when a player logs in.
 */
public class UpdateNotificationHandler {

    private boolean notified = false;

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!GeneralConfig.enableUpdateNotification || notified || !UpdateChecker.isUpdateAvailable()) {
            return;
        }

        notified = true;

        // [OmoshiroiKamo]
        IChatComponent prefix = new ChatComponentText("[OmoshiroiKamo]");
        prefix.getChatStyle()
            .setColor(EnumChatFormatting.AQUA)
            .setBold(true);

        // New version (v1.6.0) is available!
        IChatComponent mainMessage = new ChatComponentTranslation(
            "omoshiroikamo.update.available",
            UpdateChecker.getLatestVersion());
        mainMessage.getChatStyle()
            .setColor(EnumChatFormatting.WHITE)
            .setBold(false);

        // [Link]
        IChatComponent link = new ChatComponentTranslation("omoshiroikamo.update.link");
        link.getChatStyle()
            .setColor(EnumChatFormatting.GOLD)
            .setUnderlined(true);
        link.getChatStyle()
            .setChatClickEvent(
                new ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    "https://www.curseforge.com/minecraft/mc-mods/omoshiroi-kamo"));
        link.getChatStyle()
            .setChatHoverEvent(
                new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    new ChatComponentTranslation("omoshiroikamo.update.hover")));

        IChatComponent fullMessage = prefix.appendSibling(new ChatComponentText(" "))
            .appendSibling(mainMessage)
            .appendSibling(new ChatComponentText(" "))
            .appendSibling(link);

        event.player.addChatMessage(fullMessage);
    }
}
