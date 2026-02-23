package ruiseki.omoshiroikamo.core.client.util.model;

public class ModelQuad {

    public boolean[] quadsEnabled;

    public ModelQuad() {
        this.quadsEnabled = new boolean[] { true, true, true, true, true, true };
    }

    public ModelQuad(boolean blacklist) {
        if (blacklist) {
            this.quadsEnabled = new boolean[] { false, false, false, false, false, false };
        } else {
            this.quadsEnabled = new boolean[] { true, true, true, true, true, true };
        }
    }

    public ModelQuad setUp(boolean state) {
        this.quadsEnabled[3] = state;
        return this;
    }

    public ModelQuad setDown(boolean state) {
        this.quadsEnabled[2] = state;
        return this;
    }

    public ModelQuad setWest(boolean state) {
        this.quadsEnabled[1] = state;
        return this;
    }

    public ModelQuad setEast(boolean state) {
        this.quadsEnabled[0] = state;
        return this;
    }

    public ModelQuad setNorth(boolean state) {
        this.quadsEnabled[4] = state;
        return this;
    }

    public ModelQuad setSouth(boolean state) {
        this.quadsEnabled[5] = state;
        return this;
    }
}
