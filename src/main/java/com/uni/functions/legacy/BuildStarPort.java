package com.uni.functions.legacy;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.uni.functions.BuildStructure;
import com.uni.utils.UniBotUtils;

public interface BuildStarPort extends BuildStructure {

    default void tryBuildStarPort(ObservationInterface observation, ActionInterface actions, int limit, boolean asSingle) {
        if (factoryIsPresent(observation) && isUnderLimitOfBarracks(observation, limit)) {
            tryBuildStructure(observation, actions, Abilities.BUILD_STARPORT, asSingle, null);
        }
    }

    private boolean factoryIsPresent(ObservationInterface observation) {
        return UniBotUtils.countMyUnit(observation, Units.TERRAN_FACTORY) > 0;
    }

    private boolean isUnderLimitOfBarracks(ObservationInterface observation, int limit) {
        return UniBotUtils.countMyUnit(observation, Units.TERRAN_STARPORT) < limit;
    }
}
