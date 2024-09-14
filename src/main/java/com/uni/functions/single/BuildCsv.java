package com.uni.functions.single;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.uni.utils.UniBotUtils;

public class BuildCsv {

    /**
     * Useful in build-orders.
     */
    public static void tryToBuildScv(ObservationInterface observation, ActionInterface actions) {
        tryToBuildScv(observation, actions, Integer.MAX_VALUE);
    }

    /**
     * Useful in macro when I need to continuously build SCVs until reaching a certain limit.
     */
    public static void tryToBuildScv(ObservationInterface observation, ActionInterface actions, int limit) {
        if (UniBotUtils.countMyUnit(observation, Units.TERRAN_SCV) <= limit) {
            observation.getUnits().stream()
                    .map(UnitInPool::unit)
                    .filter(u -> u.getType() == Units.TERRAN_COMMAND_CENTER
                            || u.getType() == Units.TERRAN_ORBITAL_COMMAND
                            || u.getType() == Units.TERRAN_PLANETARY_FORTRESS)
                    .filter(UniBotUtils::isNotActive)
                    .forEach(u -> actions.unitCommand(u, Abilities.TRAIN_SCV, false));
        }
    }
}
