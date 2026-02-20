package ruiseki.omoshiroikamo.module.backpack.common.init;

import static ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler.ACCENT_COLOR;
import static ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackHandler.MAIN_COLOR;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.core.recipe.NBTShapedOreRecipe;

public class BackpackDyeRecipes {

    public BackpackDyeRecipes() {}

    public void registerDyeRecipes(ItemStack baseBackpack, String accentOreName, String mainOreName, int accentColor,
        int mainColor) {
        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(baseBackpack, "   ", " BM", "   ", 'B', baseBackpack, 'M', mainOreName)
                .withInt(MAIN_COLOR, mainColor)
                .allowNBTFrom(baseBackpack)
                .allowAllTags());

        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(baseBackpack, "   ", " B ", " A ", 'B', baseBackpack, 'A', accentOreName)
                .withInt(ACCENT_COLOR, accentColor)
                .allowNBTFrom(baseBackpack)
                .allowAllTags());

        GameRegistry.addRecipe(
            new NBTShapedOreRecipe(
                baseBackpack,
                "   ",
                " BM",
                " A ",
                'B',
                baseBackpack,
                'A',
                accentOreName,
                'M',
                mainOreName).withInt(MAIN_COLOR, mainColor)
                    .withInt(ACCENT_COLOR, accentColor)
                    .allowNBTFrom(baseBackpack)
                    .allowAllTags());
    }
}
