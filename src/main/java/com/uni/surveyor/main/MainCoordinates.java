package com.uni.surveyor.main;

import com.uni.surveyor.main.production.MainProductionCoordinates;
import com.uni.surveyor.main.ramp.MainRampWallCoordinates;
import com.uni.surveyor.main.supply.MainSupplyCoordinates;
import com.uni.surveyor.main.tech.MainTechAndUpgradersCoordinates;
import com.uni.surveyor.main.turret.MainTurretCoordinates;

public record MainCoordinates(MainRampWallCoordinates rampWall,
                              MainProductionCoordinates production,
                              MainSupplyCoordinates supply,
                              MainTechAndUpgradersCoordinates techAndUpgraders,
                              MainTurretCoordinates turrets) {
}
