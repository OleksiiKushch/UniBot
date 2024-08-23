package com.uni.strategies.impl;

import com.github.ocraft.s2client.bot.gateway.ActionInterface;
import com.github.ocraft.s2client.bot.gateway.ObservationInterface;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.data.Upgrade;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.uni.buildorder.BuildOrder;
import com.uni.buildorder.BuildOrderTag;
import com.uni.buildorder.BuildOrderUtils;
import com.uni.functions.BuildCsv;
import com.uni.functions.BuildStructure;
import com.uni.functions.GasMining;
import com.uni.functions.MineralMining;
import com.uni.functions.BuildSupplyDepot;
import com.uni.functions.unit.Ghost;
import com.uni.strategies.Strategy;
import com.uni.surveyor.GameMap;
import com.uni.functions.SpeedMining;
import com.uni.utils.UniBotUtils;

import java.util.Optional;


public class DoubleNuke5Min implements Strategy,
        BuildCsv,
        BuildStructure,
        BuildSupplyDepot,
        MineralMining,
        GasMining,

        Ghost
{

    private final BuildOrder buildOrder;

    public DoubleNuke5Min() {
        buildOrder = BuildOrder.builder()
                // TODO: add stop scv after move
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 13)
//                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_COMMAND_CENTER).ifPresent(cc ->
//                                act.unitCommand(cc, Abilities.RALLY_COMMAND_CENTER, GameMap.main.rampWall().firstSupplyPosition(), false)),
//                        (obs, act) -> true)
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 14)
//                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_COMMAND_CENTER).ifPresent(cc ->
//                                        findNearestMineralPatch(obs, GameMap.basesCoordinates.get(0), 1).ifPresent(mineral ->
//                                                act.unitCommand(cc, Abilities.RALLY_COMMAND_CENTER, mineral, false))),
//                        (obs, act) -> true)
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_SUPPLY_DEPOT, GameMap.main.rampWall().firstSupplyPosition(), true, null),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT).isPresent())
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 15)
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 16)
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_BARRACKS, GameMap.main.rampWall().barackPosition(), true,
                                u -> !u.getOrders().isEmpty() && u.getOrders().get(0).getAbility() == Abilities.BUILD_SUPPLY_DEPOT),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_BARRACKS).isPresent())
                .inQueue(this::tryBuildGasRefinery,
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_REFINERY).isPresent())
                .inQueue(this::tryBuildGasRefinery,
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_REFINERY) == 2)
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 17)
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 18)
                .inQueue(this::tryToBuildScv,
                        (obs, act) -> obs.getFoodUsed() == 19)
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE),
                        (obs, act) -> obs.getFoodUsed() == 20)
                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_BARRACKS).ifPresent(cc ->
                                act.unitCommand(cc, Abilities.RALLY_BUILDING,
                                        GameMap.main.rampWall().firstDefaultBarackRallyPoint(), false)),
                        (obs, act) -> true)
                .inQueue((obs, act) -> UniBotUtils.startAbility(obs, act, Units.TERRAN_COMMAND_CENTER, Abilities.MORPH_ORBITAL_COMMAND),
                        (obs, act) -> UniBotUtils.isStartAbility(obs, Units.TERRAN_COMMAND_CENTER, Abilities.MORPH_ORBITAL_COMMAND))
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_GHOST_ACADEMY, GameMap.main.techAndUpgraders().secondGhostsAcademy(), true, null),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_GHOST_ACADEMY).isPresent())
                .inQueue((obs, act) -> act.unitCommand(getNearestFreeScv(obs, GameMap.main.rampWall().secondSupplyPosition(), 1, null).get(0),
                                Abilities.MOVE, GameMap.main.rampWall().secondSupplyPosition(), false),
                        (obs, act) -> true)
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_SUPPLY_DEPOT, GameMap.main.rampWall().secondSupplyPosition(), true, u -> !u.getOrders().isEmpty() && u.getOrders().get(0).getAbility() == Abilities.MOVE),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT) + UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 2)
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_MARINE) == 1 &&
                                UniBotUtils.isStartAbility(obs, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE))
                // start build SCVs non-stop ...
                // build scv
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_MARINE) == 2 &&
                                UniBotUtils.isStartAbility(obs, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE))
                // build scv
                .inQueue((obs, act) -> UniBotUtils.startAbility(obs, act, Units.TERRAN_GHOST_ACADEMY, Abilities.RESEARCH_PERSONAL_CLOAKING),
                        (obs, act) -> UniBotUtils.isStartAbility(obs, Units.TERRAN_GHOST_ACADEMY, Abilities.RESEARCH_PERSONAL_CLOAKING))
                // build scv
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_FACTORY, GameMap.main.rampWall().factoryPosition(), true, null),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_FACTORY).isPresent())
                // TODO: make little attack with 3 marines and 1 scv
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_MARINE) == 3 &&
                                UniBotUtils.isStartAbility(obs, Units.TERRAN_BARRACKS, Abilities.TRAIN_MARINE))
                // 3 marine attack with 1 scv and bunker
                // build scv
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_SUPPLY_DEPOT, GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply3(), true, null),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT) + UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 3)
                // build scv
                .inQueue((obs, act) -> UniBotUtils.startAbility(obs, act, Units.TERRAN_BARRACKS, Abilities.BUILD_TECHLAB_BARRACKS),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_BARRACKS_TECHLAB).isPresent())
                // build scv
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_GHOST),
                        (obs, act) -> UniBotUtils.isStartAbility(obs, Units.TERRAN_BARRACKS, Abilities.TRAIN_GHOST))
                // build scv
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_STARPORT,  GameMap.main.rampWall().starPortPosition(), true,
                                u -> !u.getOrders().isEmpty() && u.getOrders().get(0).getAbility() == Abilities.BUILD_FACTORY),
                        (obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_STARPORT).isPresent())
                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_BARRACKS).ifPresent(cc ->
                                act.unitCommand(cc, Abilities.RALLY_BUILDING,
                                        GameMap.main.rampWall().starPortPosition(), false)),
                        (obs, act) -> true)
                // build scv
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_SUPPLY_DEPOT, GameMap.main.techAndUpgraders().mainSupplyCoordinates().supply4(), true, null),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT) + UniBotUtils.countMyUnit(obs, Units.TERRAN_SUPPLY_DEPOT_LOWERED) == 4)
                // start build supplies non-stop ...
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_BARRACKS, Abilities.TRAIN_GHOST),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST) == 1 &&
                                UniBotUtils.isStartAbility(obs, Units.TERRAN_BARRACKS, Abilities.TRAIN_GHOST))
                // start build marines non-stop ...
                // build scv
                .inQueue((obs, act) -> BuildOrderUtils.tryToBuildUnit(obs, act, Units.TERRAN_STARPORT, Abilities.TRAIN_MEDIVAC),
                        (obs, act) -> UniBotUtils.isStartAbility(obs, Units.TERRAN_STARPORT, Abilities.TRAIN_MEDIVAC))
                .inQueue((obs, act) -> tryBuildStructure(obs, act, Abilities.BUILD_GHOST_ACADEMY, GameMap.main.techAndUpgraders().firstGhostsAcademy(), true, null),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST_ACADEMY) == 2)
                // build scv ...

                .inQueue((obs, act) -> {
                            if (UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST) == 2) {
                                obs.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_GHOST)).stream()
                                        .map(UnitInPool::unit)
                                        .forEach(ghost -> act.unitCommand(ghost, Abilities.BEHAVIOR_HOLD_FIRE_ON_GHOST, true));
                            }
                        }, (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST) == 2)

                // move two ghost on enemy main using medivac
                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_MEDIVAC).ifPresent(unit -> {
                        obs.getUnits().stream()
                                .map(UnitInPool::unit)
                                .filter(u -> u.getType() == Units.TERRAN_GHOST)
                                .forEach(ghost -> act.unitCommand(unit, Abilities.LOAD, ghost, true));
                        act.unitCommand(unit, Abilities.MOVE, GameMap.enemyMainMostDistantPoint, true);
                }), (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_MEDIVAC) == 1 && UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST) == 0)

                .inQueue(this::buildNukes,
                        (obs, act) -> areGhostAcademiesNotSleep(obs))

                // drop two ghost on enemy main
                .inQueue((obs, act) -> UniBotUtils.getMyUnit(obs, Units.TERRAN_MEDIVAC).ifPresent(unit ->
                        act.unitCommand(unit, Abilities.UNLOAD_ALL_AT_MEDIVAC, GameMap.enemyMainMostDistantPoint, true)),
                        (obs, act) -> UniBotUtils.countMyUnit(obs, Units.TERRAN_GHOST) == 2)

                .addTag(BuildOrderTag.BUILD_SCV_S, 15)          // after 16 steps
                .addTag(BuildOrderTag.BUILD_SUPPLY_DEPOTS, 24)
                .addTag(BuildOrderTag.BUILD_MARIENS, 26)
                .build();
    }

    @Override
    public void onGameStart(ObservationInterface observation, ActionInterface actions) {
        GameMap.init(observation);
        SpeedMining.calculateSpeedMining(observation);
        SpeedMining.initialSCVsSplit(observation, actions);
    }

    @Override
    public void onStep(ObservationInterface observation, ActionInterface actions) {
        manageSupplyDepodInWall(observation, actions);

        // mining
        SpeedMining.keepLastSCVs(observation, actions); // initial split
        SpeedMining.speedMiningWithSCV(observation, actions);
        dropMule(observation, actions);
        correctCcRallyPoint(observation, actions);

        // units
        ghostHunterMode(observation, actions);

        // build order
        buildOrder.execute(observation, actions);
        if (buildOrder.isReadyFor(BuildOrderTag.BUILD_SCV_S)) {
            tryToBuildScv(observation, actions, 22);
        }
        if (buildOrder.isReadyFor(BuildOrderTag.BUILD_SUPPLY_DEPOTS)) {
            tryToBuildSupply(observation, actions, true);
        }
        if (buildOrder.isReadyFor(BuildOrderTag.BUILD_MARIENS)) {

        }
        if (buildOrder.isBuildOrderFinished()) {

            if (areGhostAcademiesSleep(observation)) {
                buildNukes(observation, actions);
            }

            Optional<Point2d> enemyPosition = GameMap.findEnemyPosition(observation);
            if (enemyPosition.isPresent() &&
                    observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_GHOST_ACADEMY)).size() == 2 &&
                    observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_GHOST_ACADEMY)).stream()
                            .noneMatch(u -> u.unit().getActive().orElse(false))) {
                observation.getUnits().stream()
                        .map(UnitInPool::unit)
                        .filter(u -> u.getType() == Units.TERRAN_GHOST)
                        .forEach(ghost -> {
                            actions.unitCommand(ghost, Abilities.EFFECT_NUKE_CALL_DOWN, enemyPosition.get(), true);
                            actions.unitCommand(ghost, Abilities.MOVE, GameMap.enemyMainMostDistantPoint, true);
                        });
            }
        }
    }

    @Override
    public void onUnitIdle(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool) {
        Unit unit = unitInPool.unit();
        switch ((Units) unit.getType()) {
            case TERRAN_SCV, TERRAN_MULE:
                backToMineralMining(observation, actions, unit);
                break;
            case TERRAN_REFINERY:
                fillGasWithWorkers(observation, actions, unit);
                break;
            default:
                break;
        }
    }

    @Override
    public void onUnitCreated(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool) {
    }

    @Override
    public void onBuildingConstructionComplete(ObservationInterface observation, ActionInterface actions, UnitInPool unitInPool) {
    }

    @Override
    public void onUpgradeCompleted(ObservationInterface observation, ActionInterface actions, Upgrade upgrade) {
    }

    private boolean areGhostAcademiesNotSleep(ObservationInterface observation) {
        return observation.getUnits().stream()
                .map(UnitInPool::unit)
                .filter(u -> u.getType() == Units.TERRAN_GHOST_ACADEMY)
                .noneMatch(UniBotUtils::isNotActive);
    }
    private boolean areGhostAcademiesSleep(ObservationInterface observation) {
        return observation.getUnits().stream()
                .map(UnitInPool::unit)
                .filter(u -> u.getType() == Units.TERRAN_GHOST_ACADEMY)
                .allMatch(UniBotUtils::isNotActive);
    }
    private void buildNukes(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits().stream()
                .map(UnitInPool::unit)
                .filter(u -> u.getType() == Units.TERRAN_GHOST_ACADEMY)
                .forEach(ghostAcademy -> actions.unitCommand(ghostAcademy, Abilities.BUILD_NUKE, false));
    }

    private void manageSupplyDepodInWall(ObservationInterface observation, ActionInterface actions) {
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SUPPLY_DEPOT_LOWERED)).stream()
                .map(UnitInPool::unit)
                .filter(unit -> GameMap.main.rampWall().firstSupplyPosition().equals(unit.getPosition().toPoint2d()))
                .forEach(supply -> {
                    if (UniBotUtils.enemyUnitInVision(observation, supply, 7)) {
                        actions.unitCommand(supply, Abilities.MORPH_SUPPLY_DEPOT_RAISE, false);
                    }
                });
        observation.getUnits(Alliance.SELF, UnitInPool.isUnit(Units.TERRAN_SUPPLY_DEPOT)).stream()
                .map(UnitInPool::unit)
                .filter(unit -> GameMap.main.rampWall().firstSupplyPosition().equals(unit.getPosition().toPoint2d()))
                .forEach(supply -> {
                    if (!UniBotUtils.enemyUnitInVision(observation, supply, 7)) {
                        actions.unitCommand(supply, Abilities.MORPH_SUPPLY_DEPOT_LOWER, false);
                    }
                });
    }
}
