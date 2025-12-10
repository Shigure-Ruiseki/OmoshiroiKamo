package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Row;

public class BackpackRow extends Row {

    public void moveChild(int prevIndex, int nextIndex) {
        IWidget child = getChildren().get(prevIndex);
        getChildren().remove(prevIndex);
        getChildren().add(nextIndex, child);
    }
}
