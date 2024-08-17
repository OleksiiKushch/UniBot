package com.uni.surveyor.main;

import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.uni.surveyor.FieldAnalizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class MainPositionAnalizerLego {

    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final Map<String, int[]> MAIN_DIRECTIONS = new LinkedHashMap<>();

    static {
        MAIN_DIRECTIONS.put(LEFT, new int[]{-1, 0});
        MAIN_DIRECTIONS.put(RIGHT, new int[]{1, 0});
        MAIN_DIRECTIONS.put(UP, new int[]{0, -1});
        MAIN_DIRECTIONS.put(DOWN, new int[]{0, 1});
    }

    private static Point2d findMostDistantPoint(int[][] matrix, Point2d target) {
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }

        int centerX = matrix.length / 2;
        int centerY = matrix[0].length / 2;
        int maxDistance = Integer.MIN_VALUE;

        DistancePoint result = searchMaxDistance(matrix, centerX, centerY, maxDistance);

        System.out.printf("Furthest 1s from Center located at (%d, %d) with distance %d (direction %s)%n",
                result.x, result.y, result.distance, result.direction);

        if (DOWN.equals(result.direction)) {
            int leftDownY = result.y;
            int leftDownX = result.x;
            int leftDownCurrent = matrix[leftDownY + 1][leftDownX - 1];
            while (leftDownCurrent == 1) {
                ++leftDownY;
                --leftDownX;
                leftDownCurrent = matrix[leftDownY + 1][leftDownX - 1];
            }
            System.out.println("Left-Down (" + leftDownX + ", " + leftDownY + ")");

            int rightDownY = result.y;
            int rightDownX = result.x;
            int rightDownCurrent = matrix[rightDownY + 1][rightDownX + 1];
            while (rightDownCurrent == 1) {
                ++rightDownY;
                ++rightDownX;
                rightDownCurrent = matrix[rightDownY + 1][rightDownX + 1];
            }
            System.out.println("Right-Down (" + rightDownX + ", " + rightDownY + ")");

            return clarifyPoint(matrix, target, result, leftDownY, leftDownX, rightDownY, rightDownX, leftDownY > rightDownY, leftDownY < rightDownY);
        } else if (UP.equals(result.direction)) {
            int leftUpY = result.y;
            int leftUpX = result.x;
            int leftUpCurrent = matrix[leftUpY - 1][leftUpX - 1];
            while (leftUpCurrent == 1) {
                --leftUpY;
                --leftUpX;
                leftUpCurrent = matrix[leftUpY - 1][leftUpX - 1];
            }
            System.out.println("Left-Up (" + leftUpX + ", " + leftUpY + ")");

            int rightUpY = result.y;
            int rightUpX = result.x;
            int rightUpCurrent = matrix[rightUpY - 1][rightUpX + 1];
            while (rightUpCurrent == 1) {
                --rightUpY;
                ++rightUpX;
                rightUpCurrent = matrix[rightUpY - 1][rightUpX + 1];
            }
            System.out.println("Right-Up (" + rightUpX + ", " + rightUpY + ")");

            return clarifyPoint(matrix, target, result, leftUpY, leftUpX, rightUpY, rightUpX, leftUpY < rightUpY, leftUpY > rightUpY);
        } else if (LEFT.equals(result.direction)) {
            // ...
        } else if (RIGHT.equals(result.direction)) {
            // ...
        }

        return null;
    }

    private static Point2d clarifyPoint(int[][] matrix, Point2d target, DistancePoint result, int leftDownY, int leftDownX, int rightDownY, int rightDownX, boolean b, boolean b2) {
        if (b) {
            int leftX = leftDownX;
            int leftCurrent = matrix[leftDownY][leftX - 1];
            while (leftCurrent == 1) {
                --leftX;
                leftCurrent = matrix[leftDownY][leftX - 1];
            }
            System.out.println("Result (" + leftX + ", " + leftDownY + ")");
            return FieldAnalizer.getExternalCoordinates(matrix.length, leftDownY, leftX, target.getX(), target.getY());
        } else if (b2) {
            int rightX = rightDownX;
            int rightCurrent = matrix[rightDownY][rightX + 1];
            while (rightCurrent == 1) {
                ++rightX;
                rightCurrent = matrix[rightDownY][rightX + 1];
            }
            System.out.println("Result (" + rightX + ", " + rightDownY + ")");
            return FieldAnalizer.getExternalCoordinates(matrix.length, rightDownY, rightX, target.getX(), target.getY());
        } else { // they equal and most likely equal to the original point
            int leftX = result.x;
            int leftCurrent = matrix[result.y][leftX - 1];
            while (leftCurrent == 1) {
                --leftX;
                leftCurrent = matrix[result.y][leftX - 1];
            }
            System.out.println("Left (" + leftX + ", " + result.y + ")");

            int rightX = result.x;
            int rightCurrent = matrix[result.y][rightX + 1];
            while (rightCurrent == 1) {
                ++rightX;
                rightCurrent = matrix[result.y][rightX + 1];
            }
            System.out.println("Right (" + rightX + ", " + result.y + ")");

            if (Math.abs(result.x - leftX) > Math.abs(result.x - rightX)) {
                System.out.println("Result (" + leftX + ", " + result.y + ")");
                return FieldAnalizer.getExternalCoordinates(matrix.length, result.y, leftX, target.getX(), target.getY());
            } else {
                System.out.println("Result (" + rightX + ", " + result.y + ")");
                return FieldAnalizer.getExternalCoordinates(matrix.length, result.y, rightX, target.getX(), target.getY());
            }
        }
    }

    private static DistancePoint searchMaxDistance(int[][] matrix, int centerX, int centerY, int maxDistance) {
        DistancePoint result = null;
        int maxResultDistance = maxDistance;

        for (Map.Entry<String, int[]> entry : MAIN_DIRECTIONS.entrySet()) {
            String directionName = entry.getKey();
            int[] vector = entry.getValue();
            int dx = vector[0];
            int dy = vector[1];

            DistancePoint tempResult = searchMaxDistance(matrix, centerX, centerY, dx, dy, maxResultDistance, directionName);

            if (tempResult.distance > maxResultDistance) {
                maxResultDistance = tempResult.distance;
                result = tempResult;
            }
        }

        return result;
    }

    private static DistancePoint searchMaxDistance(int[][] matrix, int startX, int startY, int incrementX,
                                                   int incrementY, int maxDistance, String direction) {
        int x = startX;
        int y = startY;
        int currentX = startX;
        int currentY = startY;

        while (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length) {
            if (matrix[y][x] == 1) {
                int distance = Math.abs(x - startX) + Math.abs(y - startY);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    currentX = x;
                    currentY = y;
                }
            } else if (matrix[y][x] == 0 || matrix[y][x] == 2) {
                break;
            }

            x += incrementX;
            y += incrementY;
        }

        return new DistancePoint(maxDistance, currentX, currentY, direction);
    }

    private record DistancePoint(int distance, int x, int y, String direction) {
    }
}
