package ruiseki.omoshiroikamo.module.ids.common.item.logic.node;

import net.minecraft.entity.player.EntityPlayer;

import com.github.bsideup.jabel.Desugar;

import ruiseki.omoshiroikamo.module.ids.common.item.logic.LogicNetwork;

@Desugar
public record EvalContext(LogicNetwork network, EntityPlayer player) {}
