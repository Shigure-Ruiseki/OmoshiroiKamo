package ruiseki.omoshiroikamo.module.ids.common.network.logic.node;

import net.minecraft.entity.player.EntityPlayer;

import com.github.bsideup.jabel.Desugar;

import ruiseki.omoshiroikamo.module.ids.common.network.logic.LogicNetwork;

@Desugar
public record EvalContext(LogicNetwork network, EntityPlayer player) {}
