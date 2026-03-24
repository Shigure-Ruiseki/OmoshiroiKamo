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
package ruiseki.omoshiroikamo.core.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.core.client.render.player.IPlayerItemRender;
import ruiseki.omoshiroikamo.core.client.render.player.PlayerRenderContext;
import ruiseki.omoshiroikamo.core.helper.RenderHelpers;
import ruiseki.omoshiroikamo.core.lib.LibMods;

@EventBusSubscriber
public class ItemRenderEvent {

    @SubscribeEvent
    public static void onPlayerRender(RenderPlayerEvent.Specials.Post event) {

        if (event.entityLiving.getActivePotionEffect(Potion.invisibility) != null) return;

        EntityPlayer player = event.entityPlayer;
        InventoryPlayer inv = player.inventory;

        // BODY render
        renderArmor(inv, event, RenderHelpers.RenderType.BODY);

        if (LibMods.BaublesExpanded.isLoaded() || LibMods.Baubles.isLoaded()) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            renderBauble(baubles, event, RenderHelpers.RenderType.BODY);
        }

        // HEAD render
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

        renderArmor(inv, event, RenderHelpers.RenderType.HEAD);

        if (LibMods.BaublesExpanded.isLoaded() || LibMods.Baubles.isLoaded()) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
            renderBauble(baubles, event, RenderHelpers.RenderType.HEAD);
        }

        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public static void onRenderPre(RenderPlayerEvent.Specials.Pre event) {
        EntityPlayer player = event.entityPlayer;
        PlayerRenderContext context = collectRenderContext(player);

        event.renderCape = context.renderCape();
        event.renderHelmet = context.renderHelmet();
        event.renderItem = context.renderItem();
    }

    private static PlayerRenderContext collectRenderContext(EntityPlayer player) {
        PlayerRenderContext context = new PlayerRenderContext();
        InventoryPlayer inv = player.inventory;

        // armor
        for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
            int slot = player.inventory.getSizeInventory() - 1 - armorIndex;

            ItemStack stack = inv.getStackInSlot(slot);
            if (stack == null) continue;

            if (stack.getItem() instanceof IPlayerItemRender renderer) {
                renderer.collectContext(stack, player, context);
            }
        }

        // baubles
        if (LibMods.BaublesExpanded.isLoaded() || LibMods.Baubles.isLoaded()) {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);

            for (int i = 0; i < baubles.getSizeInventory(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack == null) continue;

                if (stack.getItem() instanceof IPlayerItemRender renderer) {
                    renderer.collectContext(stack, player, context);
                }
            }
        }

        return context;
    }

    private static void renderBauble(InventoryBaubles inv, RenderPlayerEvent event, RenderHelpers.RenderType type) {
        EntityPlayer player = event.entityPlayer;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null) continue;

            if (stack.getItem() instanceof IPlayerItemRender renderer) {
                GL11.glPushMatrix();
                GL11.glColor4f(1F, 1F, 1F, 1F);
                applyRenderTypeTransform(type, player);
                renderer.render(stack, player, event, type);
                GL11.glPopMatrix();
            }
        }
    }

    private static void renderArmor(InventoryPlayer inv, RenderPlayerEvent event, RenderHelpers.RenderType type) {
        EntityPlayer player = event.entityPlayer;

        for (int armorIndex = 0; armorIndex < 4; armorIndex++) {
            int slot = player.inventory.getSizeInventory() - 1 - armorIndex;

            ItemStack stack = inv.getStackInSlot(slot);
            if (stack == null) continue;

            if (stack.getItem() instanceof IPlayerItemRender renderer) {
                GL11.glPushMatrix();
                GL11.glColor4f(1F, 1F, 1F, 1F);
                applyRenderTypeTransform(type, player);
                renderer.render(stack, player, event, type);
                GL11.glPopMatrix();
            }
        }
    }

    public static void applyRenderTypeTransform(RenderHelpers.RenderType type, EntityPlayer player) {

        switch (type) {

            case BODY:
                RenderHelpers.rotateIfSneaking(player);
                break;

            case HEAD:
                RenderHelpers.translateToHead(player);
                break;
        }
    }
}
