package ruiseki.omoshiroikamo.module.cows.client.util;

import java.util.Collection;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.cows.common.registries.ModCows;

/**
 * Handles dynamic color updates for fluid cows after texture maps are stitched.
 */
public class ClientColorUpdater {

    /**
     * Called after textures are stitched. This is the first time we can safely
     * analyze fluid textures to determine cow colors.
     */
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0) return; // 0 is blocks/fluids

        Logger.info("Texture stitch detected, updating fluid cow colors...");

        Collection<CowsRegistryItem> allCows = CowsRegistry.INSTANCE.getAll();

        int updatedCount = 0;

        for (CowsRegistryItem cow : allCows) {
            String fluidName = cow.getFluidString();
            if (fluidName == null || fluidName.isEmpty()) continue;

            Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid == null) continue;

            // Only update if current color is default white (meaning it needs calculation)
            // or if it's the fallback gray
            if (cow.getBgColor() == 0xFFFFFF || cow.getBgColor() == 0xAAAAAA) {
                int color = FluidColorHelper.getFluidColor(fluid);

                if (color != -1 && color != 0xAAAAAA) {
                    cow.setBgColor(color);
                    // Use a slightly lighter/different version for foreground if needed
                    // For now, let's just use the same or white
                    cow.setFgColor(0xFFFFFF);

                    Logger.debug("Updated cow '{}' with color: 0x{}", cow.getEntityName(), Integer.toHexString(color));
                    updatedCount++;
                }
            }
        }

        if (updatedCount > 0) {
            Logger.info("Updated colors for {} fluid cows. Synchronizing configuration...", updatedCount);
            // Save calculated colors back to JSON files
            ModCows.saveAllConfigs();
        }
    }
}
