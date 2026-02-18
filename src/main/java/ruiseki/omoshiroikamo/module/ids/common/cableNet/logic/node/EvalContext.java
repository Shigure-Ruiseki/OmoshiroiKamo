package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.node;

import net.minecraft.entity.player.EntityPlayer;

import com.github.bsideup.jabel.Desugar;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.LogicNetwork;

@Desugar
public record EvalContext(LogicNetwork network, EntityPlayer player) {}
