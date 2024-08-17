package com.uni.surveyor.main.ramp;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.surveyor.FieldAnalizer;
import com.uni.surveyor.FieldType;

public class MainRampPositionAnalizer {

    /**
     * Figures (generally, one figure (position) that can rotate):
     * 2 2 1    1 2 2    2 2 2    2 2 2
     * 2 2 2    2 2 2    2 2 2    2 2 2
     * 2 2 2    2 2 2    1 2 2    2 2 1
     */
    private static final int[][][] FIGURES = {
            {{FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.PATHABLE_AND_PLACABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}},
            {{FieldType.PATHABLE_AND_PLACABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}},
            {{FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.PATHABLE_AND_PLACABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}},
            {{FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue()}, {FieldType.ONLY_PATHABLE.getValue(), FieldType.ONLY_PATHABLE.getValue(), FieldType.PATHABLE_AND_PLACABLE.getValue()}}
    };

    public static Point2d findNearestRampDescent(int[][] matrix, Point2d point) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int centerY = rows / 2;
        int centerX = cols / 2;
        int minDistance = Integer.MAX_VALUE;
        int innerY = centerY;
        int innerX = centerX;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == FieldType.ONLY_PATHABLE.getValue()) {
                    int distance = Math.abs(i - centerY) + Math.abs(j - centerX);
                    if (distance < minDistance) {
                        minDistance = distance;
                        innerY = i;
                        innerX = j;
                    }
                }
            }
        }

        return FieldAnalizer.getExternalCoordinates(matrix.length, innerY, innerX, point.getX(), point.getY());
    }

    public static MainRampWallCoordinates findRampWallPositions(int[][] matrix, Point2d point) {
        int[] center = findFigureCenter(matrix);
        if (center == null) {
            return new MainRampWallCoordinates(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
        }
        int centerY = center[0];
        int centerX = center[1];
        Point2d barackPosition = null;
        Point2d barackDefaultPositionWithoutExt = null;
        Point2d firstSupplyPosition = null;
        Point2d secondSupplyPosition = null;
        Point2d factoryPosition = null;
        Point2d starPortPosition = null;
        Point2d firstDefaultBarackRallyPoint = null;
        Point2d secondDefaultBarackRallyPoint = null;
        if (matrix[centerY - 1][centerX + 1] == FieldType.PATHABLE_AND_PLACABLE.getValue()) {
            barackPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX + 1 + 1, point.getX(), point.getY());
            barackDefaultPositionWithoutExt = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1, centerX + 1 + 1, point.getX(), point.getY());
            FieldAnalizer.markStructureOnMap(matrix, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX + 1 + 1, FieldAnalizer.SIZE_3x3);
            firstSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 + 1, centerX + 1 + 1, point.getX(), point.getY()).add(0.5f, 0.5f);
            secondSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1, centerX + 1 - 1, point.getX(), point.getY()).add(-0.5f, -0.5f);
            factoryPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX + 1 + 1 + FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            starPortPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX + 1 + 1 + FieldAnalizer.SIZE_3x3 + FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            firstDefaultBarackRallyPoint = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION - 1, centerX + 1 + 1 - 3, point.getX(), point.getY());
            secondDefaultBarackRallyPoint = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION + FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION + 3, centerX + 1 + 1 + 3, point.getX(), point.getY());
        } else if (matrix[centerY + 1][centerX - 1] == FieldType.PATHABLE_AND_PLACABLE.getValue()) {
            barackPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX - 1 - 1, point.getX(), point.getY());
            firstSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 - 1, centerX - 1 - 1, point.getX(), point.getY()).add(-0.5f, -0.5f);
            secondSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX - 1 + 1, point.getX(), point.getY()).add(0.5f, 0.5f);
            factoryPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX - 1 - 1 - FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            starPortPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX - 1 - 1 - FieldAnalizer.SIZE_3x3 - FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            firstDefaultBarackRallyPoint = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1 + 3, centerX - 1 - 1 + 3, point.getX(), point.getY());
        } else if (matrix[centerY - 1][centerX - 1] == FieldType.PATHABLE_AND_PLACABLE.getValue()) {
            barackPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX - 1 - 1, point.getX(), point.getY());
            barackDefaultPositionWithoutExt = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1, centerX - 1 - 1, point.getX(), point.getY());
            firstSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1, centerX - 1 + 1, point.getX(), point.getY()).add(-0.5f, 0.5f);
            secondSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 + 1, centerX - 1 - 1, point.getX(), point.getY()).add(0.5f, -0.5f);
            factoryPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX - 1 - 1 - FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            starPortPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION, centerX - 1 - 1 - FieldAnalizer.SIZE_3x3 - FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            firstDefaultBarackRallyPoint = FieldAnalizer.getExternalCoordinates(matrix.length, centerY - 1 - 1 - FieldAnalizer.FOR_TERRAN_PRODUCTION_EXTENSION - 1, centerX - 1 - 1 + 3, point.getX(), point.getY());
        } else if (matrix[centerY + 1][centerX + 1] == FieldType.PATHABLE_AND_PLACABLE.getValue()) {
            barackPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX + 1 + 1, point.getX(), point.getY());
            firstSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX + 1 - 1, point.getX(), point.getY()).add(0.5f, -0.5f);
            secondSupplyPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 - 1, centerX + 1 + 1, point.getX(), point.getY()).add(-0.5f, 0.5f);
            factoryPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX + 1 + 1 + FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            starPortPosition = FieldAnalizer.getExternalCoordinates(matrix.length, centerY + 1 + 1, centerX + 1 + 1 + FieldAnalizer.SIZE_3x3 + FieldAnalizer.SIZE_3x3, point.getX(), point.getY());
            // TODO: process `firstDefaultBarackRallyPoint` and `secondDefaultBarackRallyPoint` if needed
        }
        return new MainRampWallCoordinates(
                firstSupplyPosition,
                secondSupplyPosition,
                barackPosition,
                barackDefaultPositionWithoutExt,
                factoryPosition,
                starPortPosition,
                firstDefaultBarackRallyPoint,
                secondDefaultBarackRallyPoint
        );
    }


    private static int[] findFigureCenter(int[][] matrix) {
        for (int i = 0; i <= matrix.length - 3; i++) {
            for (int j = 0; j <= matrix[i].length - 3; j++) {
                for (int[][] figure : FIGURES) {
                    if (matchesFigure(matrix, i, j, figure)) {
                        return new int[]{i + 1, j + 1};
                    }
                }
            }
        }
        return null;
    }

    private static boolean matchesFigure(int[][] matrix, int startX, int startY, int[][] figure) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (matrix[startX + x][startY + y] != figure[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }
}
