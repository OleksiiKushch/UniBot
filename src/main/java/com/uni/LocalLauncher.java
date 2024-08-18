package com.uni;

import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.protocol.game.Difficulty;
import com.github.ocraft.s2client.protocol.game.LocalMap;
import com.github.ocraft.s2client.protocol.game.Race;
import com.uni.strategies.Strategy;
import com.uni.strategies.impl.DoubleNuke5Min;

import java.nio.file.Paths;

public class LocalLauncher {

    /**
     * 2 2 2    2 2 1
     * 2 2 2    2 2 2
     * 1 2 2    2 2 2
     * below    above
     */
    private static final String OCEANBORN_LE = "Oceanborn LE";
    private static final String CRIMSON_COURT_LE = "Crimson Court LE";
    /**
     * 1 2 2
     * 2 2 2
     * 2 2 2
     * below
     */
    private static final String AMPHION_LE = "Amphion LE";
    /**
     * 1 2 2
     * 2 2 2
     * 2 2 2
     * left
     */
    private static final String GHOST_RIVER_LE = "Ghost River LE";

    /**
     * 2 2 2
     * 2 2 2
     * 2 2 1
     * up right
     */
    // full not working
    // check coordinates
    private static final String ALCYONE_LE = "Alcyone LE";

    // full not working
    // for one side, ramp builder not working (higher position)
    // two ramps and one of them in the pocket which is just selected
    // need to improve the ramp detection algorithm
    private static final String DYNASTY_LE = "Dynasty LE";  // ???

    // full not working
    // the closest field type that detects a ramp (`FieldType.ONLY_PATHABLE`) is outside the main
    // need to improve the ramp detection algorithm
    private static final String GOLDENAURA_LE = "Goldenaura LE";

    // full not working (workers not building after some time)
    // for one side, ramp builder not working (higher position)
    // incorrect position
    private static final String POST_YOUTH_LE = "Post-Youth LE";

    // full not working
    private static final String SITE_DELTA_LE = "Site Delta LE";

    public static void main(String[] args) {
        Strategy strategy = new DoubleNuke5Min();
        UniBot bot = new UniBot(strategy);
        S2Coordinator s2Coordinator = S2Coordinator.setup()
                .loadSettings(args)
                .setRealtime(true)
                .setParticipants(
                        S2Coordinator.createParticipant(Race.TERRAN, bot),
                        S2Coordinator.createComputer(Race.ZERG, Difficulty.VERY_EASY))
                .launchStarcraft()
//                .startGame(BattlenetMap.of(OCEANBORN_LE));
                .startGame(LocalMap.of(Paths.get("C:\\Program Files (x86)\\StarCraft II\\Maps\\Oceanborn513AIE.SC2Map")));

        while (s2Coordinator.update()) {
        }

        s2Coordinator.quit();
    }
}
