package rts.core.engine;

import java.awt.Point;
import java.util.ArrayList;

import rts.core.engine.layers.entities.MoveableEntity;
import rts.core.engine.map.Map;

public final class Utils {

	// Directions mapping with sprite sheet
	public static final int UP_LEFT = 0;
	public static final int UP = 1;
	public static final int UP_RIGHT = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int DOWN_LEFT = 5;
	public static final int DOWN = 6;
	public static final int DOWN_RIGHT = 7;

	public static final int[][] DIRECTIONS = new int[][] { { -1, -1 }, { 0, -1 }, { 1, -1 }, { -1, 0 }, { 1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 }, };

	public static final int[][] AXES = new int[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

	public static final int[] ANGLES = new int[] { 315, 0, 45, 270, 90, 225, 180, 135 };

	public static int findDirection(float fromX, float fromY, float toX, float toY) {
		if (fromX == toX) {
			if (fromY < toY) {
				return DOWN;
			} else {
				if (fromY > toY) {
					return UP;
				}
			}
		} else {
			if (fromY == toY) {
				if (fromX < toX) {
					return RIGHT;
				} else {
					if (fromX > toX) {
						return LEFT;
					}
				}
			} else {
				if (fromX < toX && fromY < toY) {
					return DOWN_RIGHT;
				} else {
					if (fromX > toX && fromY < toY) {
						return DOWN_LEFT;
					} else {
						if (fromX > toX && fromY > toY) {
							return UP_LEFT;
						} else {
							if (fromX < toX && fromY > toY) {
								return UP_RIGHT;
							}
						}
					}
				}
			}
		}
		return DOWN;
	}

	public static float getDistanceBetween(float startX, float startY, float endX, float endY) {
		return (float) Math.sqrt((Math.pow((endX - startX), 2)) + (Math.pow((endY - startY), 2)));
	}

	public static float getTargetAngle(float playerX, float playerY, float targetX, float targetY) {
		float dist = getDistanceBetween(playerX, playerY, targetX, targetY);
		float sinNewAng = (playerY - targetY) / dist;
		float cosNewAng = (targetX - playerX) / dist;
		float angle = 0;

		if (sinNewAng > 0) {
			if (cosNewAng > 0) {
				angle = (float) (90 - Math.toDegrees(Math.asin(sinNewAng)));
			} else {
				angle = (float) (Math.toDegrees(Math.asin(sinNewAng)) + 270);
			}
		} else {
			angle = (float) (Math.toDegrees(Math.acos(cosNewAng)) + 90);
		}
		return angle;
	}

	public static Point getCloserPoint(Map map, int mx, int my) {
		int check = 0;
		int depth = 1;
		int x = mx;
		int y = my;
		ArrayList<Point> array = new ArrayList<Point>();
		while (!findPoint(map, array, x, y)) {
			if (check == 8) {
				depth++;
				check = 0;
			}
			x = mx + DIRECTIONS[check][0] * depth;
			y = my + DIRECTIONS[check][1] * depth;
			check++;
		}
		return array.get(0);
	}

    // closer point for single entity
	public static Point getCloserPoint(Map map, MoveableEntity ent, int mx, int my) {
		int check = 0;
		int depth = 1;
		int x = mx;
		int y = my;
		ArrayList<Point> array = new ArrayList<Point>();
		while (!findPoint(ent, map, array, x, y)) {
			if (check == 8) {
				depth++;
				check = 0;
			}
			x = mx + DIRECTIONS[check][0] * depth;
			y = my + DIRECTIONS[check][1] * depth;
            check++;
		}
		return array.get(0);
	}

    // find points for multiple entities
	public static ArrayList<Point> getCloserPoints(Map map, ArrayList<MoveableEntity> ents, int mx, int my) {
		ArrayList<Point> array = new ArrayList<Point>();
		for (int i = 0; i < ents.size(); i++) {
			MoveableEntity me = ents.get(i);
			int check = 0;
			int depth = 1;
            int x = mx;
			int y = my;
			while (!findPoint(me, map, array, x, y)) {
				if (check == 8) {
					depth++;
					check = 0;
				}
                x = mx + DIRECTIONS[check][0] * depth;
				y = my + DIRECTIONS[check][1] * depth;
				check++;
			}
		}
		return array;
	}

    // sees if the x, y location is in our point array
	private static boolean findPoint(MoveableEntity me, Map map, ArrayList<Point> array, int x, int y) {
        // find the first direction from x, y that is not in our array and add it to our array
		for (int j = 0; j < 8; j++) {
            // if the location + direction is not blocked by an obstacle or water or anything
			if (!map.blocked(me, x + DIRECTIONS[j][0], y + DIRECTIONS[j][1])) {
				boolean contain = false;
                // check if the new location is in our array
				for (int k = 0; k < array.size(); k++) {
					if (array.get(k).x == x + DIRECTIONS[j][0] && array.get(k).y == y + DIRECTIONS[j][1]) {
                        // if our array contains the point
						contain = true;
						break;
					}
				}
                // if our point is not in the array, add it
				if (!contain) {
					// find
					array.add(new Point(x + DIRECTIONS[j][0], y + DIRECTIONS[j][1]));
					return true;
				}
			}
            else {
            }
		}
		return false;
	}

	private static boolean findPoint(Map map, ArrayList<Point> array, int x, int y) {
		for (int j = 0; j < 8; j++) {
			if (!map.isEntityBlocked(x + DIRECTIONS[j][0], y + DIRECTIONS[j][1])) {
				boolean contain = false;
				for (int k = 0; k < array.size(); k++) {
					if (array.get(k).x == x + DIRECTIONS[j][0] && array.get(k).y == y + DIRECTIONS[j][1]) {
						contain = true;
						break;
					}
				}
				if (!contain) {
					// find
					array.add(new Point(x + DIRECTIONS[j][0], y + DIRECTIONS[j][1]));
					return true;
				}
			}
		}
		return false;
	}
}
