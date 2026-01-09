package ruiseki.omoshiroikamo.api.block;

import net.minecraft.util.Vec3;

public class LookDirection {

    private final Vec3 a;
    private final Vec3 b;

    public LookDirection(final Vec3 a, final Vec3 b) {
        this.a = a;
        this.b = b;
    }

    public Vec3 getA() {
        return this.a;
    }

    public Vec3 getB() {
        return this.b;
    }
}
