package ruiseki.omoshiroikamo.core.common.achievement;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class AchievementEntry extends Achievement {

    public static List<Achievement> achievements = new ArrayList<>();

    public AchievementEntry(String name, int x, int y, ItemStack icon, Achievement parent) {
        super(LibResources.ACHIEVEMENT + name, LibMisc.MOD_ID + "." + name, x, y, icon, parent);
        achievements.add(this);
        registerStat();
    }

    public AchievementEntry(String name, int x, int y, Item icon, Achievement parent) {
        this(name, x, y, new ItemStack(icon), parent);
    }

    public AchievementEntry(String name, int x, int y, Block icon, Achievement parent) {
        this(name, x, y, new ItemStack(icon), parent);
    }
}
