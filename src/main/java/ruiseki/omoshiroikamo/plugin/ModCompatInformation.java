package ruiseki.omoshiroikamo.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

public class ModCompatInformation {

    public static HashMap<Integer, ModCompatInformation> TOOLTIP = new HashMap<>();

    /*
     * From Mod
     */
    private String registeredMod;
    /**
     * Creator of item
     */
    private String author;
    /*
     * Chickens mod ID for addon
     */
    private String addon;

    public ModCompatInformation(String registeredMod, String author, String registeredAddon) {
        this.registeredMod = registeredMod;
        this.author = author;
        this.addon = registeredAddon;
    }

    public List<String> getToolTip() {
        List<String> tip = new ArrayList<>();
        tip.add(EnumChatFormatting.BLUE + "" + EnumChatFormatting.ITALIC + this.getRegisteredMod());
        return tip;
    }

    public String getRegisteredMod() {
        return this.registeredMod;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getRegisteredAddon() {
        return this.addon;
    }

    public static void addInformation(int id, ModCompatInformation info) {
        ModCompatInformation.TOOLTIP.put(id, info);
    }

}
