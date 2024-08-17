package com.uni.buildorder;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.uni.utils.UniBotUtils;

public class BuildOrderUtils {

    public static void tryToBuildUnit(ObservationInterface observation, ActionInterface actions,
                                      Units productionUnit, Abilities abilityForBuild) {
        observation.getUnits().stream()
                .map(UnitInPool::unit)
                .filter(u -> u.getType() == productionUnit)
                .filter(UniBotUtils::isNotActive)
                .forEach(u -> actions.unitCommand(u, abilityForBuild, false));
    }
}
