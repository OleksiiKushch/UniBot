package com.uni.functions.legacy;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.uni.functions.BuildStructure;
import com.uni.utils.UniBotUtils;

public interface BuildFactory extends BuildStructure {

    default void tryBuildFactories(ObservationInterface observation, ActionInterface actions, int limit, boolean asSingle) {
        if (barrackIsPresent(observation) &&
                isUnderLimitOfBarracks(observation, limit)) {
            tryBuildStructure(observation, actions, Abilities.BUILD_FACTORY, asSingle, null);
        }
    }

    private boolean barrackIsPresent(ObservationInterface observation) {
        return UniBotUtils.countMyUnit(observation, Units.TERRAN_BARRACKS) > 0;
    }

    private boolean isUnderLimitOfBarracks(ObservationInterface observation, int limit) {
        return UniBotUtils.countMyUnit(observation, Units.TERRAN_FACTORY) < limit;
    }
}
