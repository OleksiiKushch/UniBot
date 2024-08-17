package com.uni.functions.legacy;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.surveyor.GameMap;

public interface AttackEnemy {

    default void attackEnemy(ObservationInterface observation, ActionInterface actions, Unit unit) {
        GameMap.findEnemyPosition(observation).ifPresent(point2d ->
                actions.unitCommand(unit, Abilities.ATTACK_ATTACK, point2d, false));
    }
}
