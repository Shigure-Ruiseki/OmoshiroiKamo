package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type;

import net.minecraft.client.renderer.texture.IIconRegister;

import org.jetbrains.annotations.NotNull;

public class LogicType<T> {

    private final String id;

    protected LogicType(String id) {
        this.id = id;
    }

    public void registerIcons(IIconRegister register) {}

    public @NotNull String getId() {
        return id;
    }

    public boolean isNumeric() {
        return false;
    }

    @Override
    public String toString() {
        return id;
    }
}
