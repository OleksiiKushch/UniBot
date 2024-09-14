package com.uni.functions.single;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.functions.MineralLineOptimizer;

public class BackScvToMineralMining {

    public static void back(ObservationInterface observation, ActionInterface actions, Unit unit) {
        MineralLineOptimizer.findNearestMineralPatch(observation, unit.getPosition().toPoint2d(), 1)
                .ifPresent(mineralPath -> actions.unitCommand(unit, Abilities.SMART, mineralPath, false));
    }
}
