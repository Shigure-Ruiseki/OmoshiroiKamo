package ruiseki.omoshiroikamo.core.client.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class IconRegistry {

    private final static Map<String, IIcon> icons = new HashMap<>();

    private IconRegistry() {

    }

    public static void addIcon(String iconName, String iconLocation, IIconRegister ir) {

        icons.put(iconName, ir.registerIcon(iconLocation));
    }

    public static void addIcon(String iconName, IIcon icon) {

        icons.put(iconName, icon);
    }

    public static IIcon getIcon(String iconName) {

        return icons.get(iconName);
    }

    public static IIcon getIcon(String iconName, int iconOffset) {

        return icons.get(iconName + iconOffset);
    }

}
