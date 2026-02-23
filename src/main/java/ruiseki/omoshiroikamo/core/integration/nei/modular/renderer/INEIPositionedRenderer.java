package ruiseki.omoshiroikamo.core.integration.nei.modular.renderer;

import java.awt.Rectangle;
import java.util.List;

public interface INEIPositionedRenderer {

    void draw();

    void handleTooltip(List<String> currenttip);

    Rectangle getPosition();
}
