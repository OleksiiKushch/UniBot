package com.uni.functions.unit;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.uni.utils.UniBotUtils;

public class Ghost {

    private static final float CLOACK_ACTIVATION_RAGE = 9;

    public static void hunterMode(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits().stream()
                .map(UnitInPool::unit)
                .filter(u -> u.getType() == Units.TERRAN_GHOST)
                .forEach(ghost -> {
                    if (UniBotUtils.enemyUnitInVision(observation, ghost, CLOACK_ACTIVATION_RAGE)) {
                        actions.unitCommand(ghost, Abilities.BEHAVIOR_CLOAK_ON_GHOST,false);
                    } else {
                        actions.unitCommand(ghost, Abilities.BEHAVIOR_CLOAK_OFF_GHOST,false);
                    }
                });
    }
}
