package com.uni;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.protocol.game.Race;
import com.uni.strategies.Strategy;
import com.uni.strategies.impl.DoubleNuke5Min;

public class LadderLauncher {
    public static void main(String[] args) {
        Strategy strategy = new DoubleNuke5Min();
        S2Agent bot = new UniBot(strategy);

        S2Coordinator s2Coordinator = S2Coordinator.setup()
                .setTimeoutMS(300000) // 5min
//                .setRawAffectsSelection(true)
//                .setShowCloaked(true)
//                .setRealtime(false)
                .loadLadderSettings(args)
//                .setStepSize(2)
                .setParticipants(S2Coordinator.createParticipant(Race.TERRAN, bot, "UniBot"))
                .connectToLadder()
                .joinGame();

        while (s2Coordinator.update()) {
        }

        s2Coordinator.quit();
    }
}
