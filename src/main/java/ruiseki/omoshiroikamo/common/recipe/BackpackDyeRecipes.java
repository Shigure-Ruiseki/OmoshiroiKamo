package ruiseki.omoshiroikamo.common.recipe;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.ACCENT_COLOR;
import static ruiseki.omoshiroikamo.common.block.backpack.BackpackHandler.MAIN_COLOR;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

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
