/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Aug 27, 2014, 8:55:00 PM (GMT)]
 */
package ruiseki.omoshiroikamo.common.util.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.client.IBaubleRender;
import ruiseki.omoshiroikamo.api.client.IItemJSONRender;
import ruiseki.omoshiroikamo.api.client.RenderUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

@EventBusSubscriber
public class ItemRenderHandler {

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Specials.Post event) {
        if (event.entityLiving.getActivePotionEffect(Potion.invisibility) != null) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        InventoryPlayer inv = player.inventory;
        if (ItemConfigs.renderBaubles && (LibMods.BaublesExpanded.isLoaded() || LibMods.Baubles.isLoaded())) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            renderBauble(baubles, event, RenderUtils.RenderType.BODY);
        }

        renderArmor(inv, event, RenderUtils.RenderType.BODY);

        float yaw = player.prevRotationYawHead
            + (player.rotationYawHead - player.prevRotationYawHead) * event.partialRenderTick;
        float yawOffset = player.prevRenderYawOffset
            + (player.renderYawOffset - player.prevRenderYawOffset) * event.partialRenderTick;
        float pitch = player.prevRotationPitch
            + (player.rotationPitch - player.prevRotationPitch) * event.partialRenderTick;

        GL11.glPushMatrix();
        GL11.glRotatef(yawOffset, 0, -1, 0);
        GL11.glRotatef(yaw - 270, 0, 1, 0);
        GL11.glRotatef(pitch, 0, 0, 1);
        renderArmor(inv, event, RenderUtils.RenderType.HEAD);
        if (ItemConfigs.renderBaubles && (LibMods.BaublesExpanded.isLoaded() || LibMods.Baubles.isLoaded())) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            renderBauble(baubles, event, RenderUtils.RenderType.HEAD);
        }
        GL11.glPopMatrix();
    }

    private static void renderBauble(InventoryBaubles inv, RenderPlayerEvent event, RenderUtils.RenderType type) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
                Item item = stack.getItem();

                if (item instanceof IBaubleRender iBaubleRender) {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1F, 1F, 1F, 1F);
                    iBaubleRender.onPlayerBaubleRender(stack, event, type);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    private static void renderArmor(InventoryPlayer inv, RenderPlayerEvent event, RenderUtils.RenderType type) {
        if (!ItemConfigs.renderArmor) {
            return;
        }
        EntityPlayer player = event.entityPlayer;

        for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
            int slot = player.inventory.getSizeInventory() - 1 - armorIndex;
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack == null || !(stack.getItem() instanceof IItemJSONRender renderer)) continue;

            GL11.glPushMatrix();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            renderer.onArmorRender(stack, event, type);
            GL11.glPopMatrix();
        }
    }
}
