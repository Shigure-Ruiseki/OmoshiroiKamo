package ruiseki.omoshiroikamo.api.io;

import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public enum IoMode {

    NONE(LibMisc.LANG.localize("gui.machine.ioMode.none")),
    INPUT(LibMisc.LANG.localize("gui.machine.ioMode.input")),
    OUTPUT(LibMisc.LANG.localize("gui.machine.ioMode.output")),
    INPUT_OUTPUT(LibMisc.LANG.localize("gui.machine.ioMode.inputOutput")),
    DISABLED(LibMisc.LANG.localize("gui.machine.ioMode.disabled"));

    private final String unlocalisedName;

    IoMode(String unlocalisedName) {
        this.unlocalisedName = unlocalisedName;
    }

    public String getUnlocalisedName() {
        return unlocalisedName;
    }

    public boolean inputs() {
        return this == INPUT || this == INPUT_OUTPUT;
    }

    public boolean outputs() {
        return this == OUTPUT || this == INPUT_OUTPUT;
    }

    public boolean canOutput() {
        return outputs() || this == NONE;
    }

    public boolean canRecieveInput() {
        return inputs() || this == NONE;
    }

    public String getLocalisedName() {
        return LibMisc.LANG.localize(unlocalisedName);
    }

    public String colorLocalisedName() {
        String loc = getLocalisedName();
        switch (this) {
            case DISABLED:
                return EnumChatFormatting.RED + loc;
            case NONE:
                return EnumChatFormatting.GRAY + loc;
            case INPUT:
                return EnumChatFormatting.AQUA + loc;
            case OUTPUT:
                return EnumChatFormatting.GOLD + loc;
            case INPUT_OUTPUT:
                return String.format(
                    LibMisc.LANG.localize(this.getUnlocalisedName() + ".colored"),
                    EnumChatFormatting.GOLD,
                    EnumChatFormatting.WHITE,
                    EnumChatFormatting.AQUA);
            default:
                return loc;
        }
    }

    public IoMode next() {
        int index = ordinal() + 1;
        if (index >= values().length) {
            index = 0;
        }
        return values()[index];
    }
}
