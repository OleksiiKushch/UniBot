package com.uni.functions.single;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.functions.MineralLineOptimizer;

public class BackScvToMineralMining {

    public static void back(ObservationInterface observation, ActionInterface actions, Unit unit) {
        Unit targetMineral = MineralLineOptimizer.findNearestMineralPatch();
        if (targetMineral == null) {
            targetMineral = MineralLineOptimizer.findNearestMineralPatch(observation, unit.getPosition().toPoint2d(), 1)
                    .orElse(null);
        }
        actions.unitCommand(unit, Abilities.SMART, targetMineral, false);
    }
}
