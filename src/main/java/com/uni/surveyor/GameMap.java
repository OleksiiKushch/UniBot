package com.uni.surveyor;

import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.game.raw.StartRaw;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.surveyor.main.MainCoordinates;
import com.uni.surveyor.main.MainPositionAnalizer;
import com.uni.surveyor.main.ramp.MainRampPositionAnalizer;
import com.uni.surveyor.main.ramp.MainRampWallCoordinates;
import com.uni.surveyor.main.tech.MainTechAndUpgradersCoordinates;
import com.uni.surveyor.main.tech.MainTechAndUpgradersPositionAnalizer;
import com.uni.utils.UniBotUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GameMap {

    public static final int MAIN_MAP_SIZE = 49;
    public static final int RAMP_MAP_SIZE = 11;


    public static MainCoordinates main;
    public static List<Point2d> basesCoordinates;

    private static int[][] myMainMap;

    private static int[][] enemyMainMap;
    public static Point2d enemyMainMostDistantPoint;


    public static void init(ObservationInterface observation) {
        myMainMap = FieldAnalizer.scan(observation, observation.getStartLocation().toPoint2d(), MAIN_MAP_SIZE);

        enemyMainMap = FieldAnalizer.scan(observation, findEnemyPosition(observation).orElse(null), MAIN_MAP_SIZE);
        enemyMainMostDistantPoint = findEnemyMainMostDistantPoint(observation);

        main = new MainCoordinates(
                getMainRampWallCoordinates(observation),
                null,
                null,
                getTechAndUpgradersCoordinates(observation),
                null
        );
        basesCoordinates = new ArrayList<>();
        basesCoordinates.add(observation.getStartLocation().toPoint2d());
    }

    private static MainRampWallCoordinates getMainRampWallCoordinates(ObservationInterface observation) {
        Point2d rampStartPosition = MainRampPositionAnalizer.findNearestRampDescent(myMainMap, observation.getStartLocation().toPoint2d());
        int[][] myRampScan = FieldAnalizer.scan(observation, rampStartPosition, RAMP_MAP_SIZE);
        return MainRampPositionAnalizer.findRampWallPositions(myRampScan, rampStartPosition);
    }

    private static Point2d findEnemyMainMostDistantPoint(ObservationInterface observation) {
        Point2d enemyStartPosition = findEnemyPosition(observation).orElse(null);
        Point2d enemyRampPosition = MainRampPositionAnalizer.findNearestRampDescent(enemyMainMap, enemyStartPosition);
        List<Unit> gases = UniBotUtils.findNearestUnits(observation, enemyStartPosition, Units.NEUTRAL_VESPENE_GEYSER, Alliance.NEUTRAL, 2, u -> true);
        return MainPositionAnalizer.findMostDistantPoint(enemyMainMap,
                enemyStartPosition,
                gases.get(0).getPosition().toPoint2d(), // first gas
                gases.get(1).getPosition().toPoint2d(), // second gas
                enemyRampPosition);
    }

    public static Optional<Point2d> findEnemyPosition(ObservationInterface observation) {
        Optional<StartRaw> startRaw = observation.getGameInfo().getStartRaw();
        if (startRaw.isPresent()) {
            Set<Point2d> startLocations = new HashSet<>(startRaw.get().getStartLocations());
            startLocations.remove(observation.getStartLocation().toPoint2d());
            if (startLocations.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new ArrayList<>(startLocations)
                    .get(ThreadLocalRandom.current().nextInt(startLocations.size())));
        } else {
            return Optional.empty();
        }
    }

    public static MainTechAndUpgradersCoordinates getTechAndUpgradersCoordinates(ObservationInterface observation) {
        Point2d startPosition = observation.getStartLocation().toPoint2d();
        Point2d rampStartPosition = MainRampPositionAnalizer.findNearestRampDescent(myMainMap, startPosition);
        Point2d gasAntiRampCoordinates = findGasAntiRampPosition(observation,
                startPosition, rampStartPosition);
        return MainTechAndUpgradersPositionAnalizer.findAllTechAndUpgradersCoordinates(startPosition,
                gasAntiRampCoordinates, UniBotUtils.findNearestUnits(observation, gasAntiRampCoordinates,
                        Units.NEUTRAL_MINERAL_FIELD, Alliance.NEUTRAL, 2, u -> true).get(0).getPosition().toPoint2d());
    }

    public static Point2d findGasAntiRampPosition(ObservationInterface observation, Point2d startPosition, Point2d rampPosition) {
        return UniBotUtils.findNearestUnits(observation, startPosition, Units.NEUTRAL_VESPENE_GEYSER, Alliance.NEUTRAL, 2, u -> true)
                .stream()
                .max(Comparator.comparing(u -> u.getPosition().toPoint2d().distance(rampPosition)))
                .map(unit -> unit.getPosition().toPoint2d())
                .orElse(null);
    }
}
