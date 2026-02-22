package ruiseki.omoshiroikamo.core.client.util.model;

public class ModelBoxSpecial extends ModelBox {

    public final int iconIndex;

    public ModelBoxSpecial(ModelRenderer renderer, int u, int v, float cx, float cy, float cz, int x, int y, int z,
        float o, int iconIndex) {
        super(renderer, u, v, cx, cy, cz, x, y, z, o);
        this.iconIndex = iconIndex;
    }
}
