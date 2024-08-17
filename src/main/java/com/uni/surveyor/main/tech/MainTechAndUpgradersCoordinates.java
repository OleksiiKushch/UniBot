package com.uni.surveyor.main.tech;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.surveyor.main.supply.MainSupplyCoordinates;

public record MainTechAndUpgradersCoordinates(
        Point2d firstGhostsAcademy,
        Point2d secondGhostsAcademy,
        Point2d thirdGhostsAcademy,
        Point2d firstEngineeringBay,
        Point2d firstArmory,
        Point2d fusionCore,

        MainSupplyCoordinates mainSupplyCoordinates
) {
}
