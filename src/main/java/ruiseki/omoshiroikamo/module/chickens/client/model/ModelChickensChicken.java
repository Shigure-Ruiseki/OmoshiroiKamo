package ruiseki.omoshiroikamo.module.chickens.client.model;

import net.minecraft.client.model.ModelChicken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelChickensChicken extends ModelChicken {

    public void renderRoost(float scale) {
        this.head.render(scale);
        this.bill.render(scale);
        this.chin.render(scale);
        this.body.render(scale);
        this.rightWing.render(scale);
        this.leftWing.render(scale);
    }
}
