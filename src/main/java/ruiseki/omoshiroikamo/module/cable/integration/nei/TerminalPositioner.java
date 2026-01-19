package ruiseki.omoshiroikamo.module.cable.integration.nei;

import java.util.ArrayList;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IStackPositioner;

public class TerminalPositioner implements IStackPositioner {

    @Override
    public ArrayList<PositionedStack> positionStacks(ArrayList<PositionedStack> ai) {
        return ai;
    }
}
