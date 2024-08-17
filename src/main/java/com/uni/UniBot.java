package com.uni;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Upgrade;
import com.uni.strategies.Strategy;


public class UniBot extends S2Agent {

    private final Strategy strategy;

    public UniBot(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void onGameStart() {
        strategy.onGameStart(observation(), actions());
    }

    @Override
    public void onStep() {
        strategy.onStep(observation(), actions());
    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool) {
        strategy.onUnitIdle(observation(), actions(), unitInPool);
    }

    @Override
    public void onUnitCreated(UnitInPool unitInPool) {
        strategy.onUnitCreated(observation(), actions(), unitInPool);
    }

    @Override
    public void onBuildingConstructionComplete(UnitInPool unitInPool) {
        strategy.onBuildingConstructionComplete(observation(), actions(), unitInPool);
    }

    @Override
    public void onUpgradeCompleted(Upgrade upgrade) {
        strategy.onUpgradeCompleted(observation(), actions(), upgrade);
    }
}
