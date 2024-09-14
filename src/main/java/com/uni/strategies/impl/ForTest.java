package com.uni.strategies.impl;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Upgrade;
import com.uni.strategies.Strategy;

public class ForTest implements Strategy {

    @Override
    public void onGameStart(ObservationInterface observation, ActionInterface actions) {

    }

    @Override
    public void onStep(ObservationInterface observation, ActionInterface actions) {

    }

    @Override
    public void onUnitIdle(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool) {

    }

    @Override
    public void onUnitCreated(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPoo) {

    }

    @Override
    public void onBuildingConstructionComplete(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool) {

    }

    @Override
    public void onUpgradeCompleted(ObservationInterface observation, ActionInterface actions, Upgrade upgrade) {

    }
}
