package com.uni.strategies;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Upgrade;

public interface Strategy {

    void onGameStart(ObservationInterface observation, ActionInterface actions);
    void onStep(ObservationInterface observation, ActionInterface actions);
    void onUnitIdle(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool);
    void onUnitCreated(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPoo);
    void onBuildingConstructionComplete(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool);
    void onUpgradeCompleted(ObservationInterface observation, ActionInterface actions, Upgrade upgrade);
}
