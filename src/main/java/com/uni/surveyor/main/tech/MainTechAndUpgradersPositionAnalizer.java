package com.uni.surveyor.main.tech;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.surveyor.main.supply.MainSupplyCoordinates;
import com.uni.surveyor.FieldAnalizer;

public class MainTechAndUpgradersPositionAnalizer {

    public static MainTechAndUpgradersCoordinates findAllTechAndUpgradersCoordinates(Point2d startPosition,
                                                                                     Point2d gasAntiRumpPosition, Point2d nearestToAntiRumpGas) {
        Point2d firstGhostsAcademy;
        Point2d secondGhostsAcademy;
        Point2d thirdGhostsAcademy;
        MainSupplyCoordinates mainSupplyCoordinates = null;
        if (Math.abs(startPosition.getX() - gasAntiRumpPosition.getX()) > Math.abs(startPosition.getY() - gasAntiRumpPosition.getY())) {
            if (startPosition.getX() > gasAntiRumpPosition.getX()) {
                firstGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, 0);
                if (nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3)) <
                        nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3))) {
                    secondGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(-0.5f, -2.5f),
                            gasAntiRumpPosition.add(-0.5f, -4.5f),
                            gasAntiRumpPosition.add(-2.5f, -5.5f),
                            gasAntiRumpPosition.add(-0.5f, -6.5f),
                            gasAntiRumpPosition.add(1.5f, -6.5f),
                            gasAntiRumpPosition.add(1.5f, -4.5f)
                    );
                } else {
                    secondGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(-0.5f, 2.5f),
                            gasAntiRumpPosition.add(-0.5f, 4.5f),
                            gasAntiRumpPosition.add(-2.5f, 5.5f),
                            gasAntiRumpPosition.add(-0.5f, 6.5f),
                            gasAntiRumpPosition.add(1.5f, 6.5f),
                            gasAntiRumpPosition.add(1.5f, 4.5f)
                    );
                }

            } else {
                firstGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, 0);
                if (nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3)) <
                        nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3))) {
                    secondGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(0.5f, -2.5f),
                            gasAntiRumpPosition.add(0.5f, -4.5f),
                            gasAntiRumpPosition.add(2.5f, -5.5f),
                            gasAntiRumpPosition.add(0.5f, -6.5f),
                            gasAntiRumpPosition.add(-1.5f, -6.5f),
                            gasAntiRumpPosition.add(-1.5f, -4.5f)
                    );
                } else {
                    secondGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(0.5f, 2.5f),
                            gasAntiRumpPosition.add(0.5f, 4.5f),
                            gasAntiRumpPosition.add(2.5f, 5.5f),
                            gasAntiRumpPosition.add(0.5f, 6.5f),
                            gasAntiRumpPosition.add(-1.5f, 6.5f),
                            gasAntiRumpPosition.add(-1.5f, 4.5f)
                    );
                }
            }
        } else {
            if (startPosition.getY() > gasAntiRumpPosition.getY()) {
                firstGhostsAcademy = gasAntiRumpPosition.add(0, -FieldAnalizer.SIZE_3x3);
                if (nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3)) <
                        nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3))) {
                    secondGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(2.5f, -0.5f),
                            gasAntiRumpPosition.add(4.5f, -0.5f),
                            gasAntiRumpPosition.add(5.5f, -2.5f),
                            gasAntiRumpPosition.add(6.5f, -0.5f),
                            gasAntiRumpPosition.add(6.5f, 1.5f),
                            gasAntiRumpPosition.add(4.5f, 1.5f)
                    );
                } else {
                    secondGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, -FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(-2.5f, -0.5f),
                            gasAntiRumpPosition.add(-4.5f, -0.5f),
                            gasAntiRumpPosition.add(-5.5f, -2.5f),
                            gasAntiRumpPosition.add(-6.5f, -0.5f),
                            gasAntiRumpPosition.add(-6.5f, 1.5f),
                            gasAntiRumpPosition.add(-4.5f, 1.5f)
                    );
                }
            } else {
                firstGhostsAcademy = gasAntiRumpPosition.add(0, FieldAnalizer.SIZE_3x3);
                if (nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3)) <
                        nearestToAntiRumpGas.distance(gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3))) {
                    secondGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(2.5f, 0.5f),
                            gasAntiRumpPosition.add(4.5f, 0.5f),
                            gasAntiRumpPosition.add(5.5f, 2.5f),
                            gasAntiRumpPosition.add(6.5f, 0.5f),
                            gasAntiRumpPosition.add(6.5f, -1.5f),
                            gasAntiRumpPosition.add(4.5f, -1.5f)
                    );
                } else {
                    secondGhostsAcademy = gasAntiRumpPosition.add(FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    thirdGhostsAcademy = gasAntiRumpPosition.add(-FieldAnalizer.SIZE_3x3, FieldAnalizer.SIZE_3x3);
                    mainSupplyCoordinates = new MainSupplyCoordinates(
                            gasAntiRumpPosition.add(-2.5f, 0.5f),
                            gasAntiRumpPosition.add(-4.5f, 0.5f),
                            gasAntiRumpPosition.add(-5.5f, 2.5f),
                            gasAntiRumpPosition.add(-6.5f, 0.5f),
                            gasAntiRumpPosition.add(-6.5f, -1.5f),
                            gasAntiRumpPosition.add(-4.5f, -1.5f)
                    );
                }
            }
        }

        Point2d firstEngineeringBay = null;
        Point2d firstArmory = null;
        Point2d fusionCore = null;



        return new MainTechAndUpgradersCoordinates(
                firstGhostsAcademy,
                secondGhostsAcademy,
                thirdGhostsAcademy,

                firstEngineeringBay,
                firstArmory,
                fusionCore,

                mainSupplyCoordinates
        );
    }
}
