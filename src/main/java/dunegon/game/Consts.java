package dunegon.game;

public class Consts {

    public static final int MAP_HEIGHT = 14;
    public static final int MAP_WIDTH = 18;

    public static final int SEA_FLOOR = 7;
    public static final int MAX_Z = 15;

    public static class Skill {
        public static final int FIST = 0;
        public static final int CLUB = 1;
        public static final int SWORD = 2;
        public static final int AXE = 3;
        public static final int DISTANCE = 4;
        public static final int SHIELDING = 5;
        public static final int FISHING = 6;
        public static final int CRITICALCHANCE = 7;
        public static final int CRITICALDAMAGE = 8;
        public static final int LIFELEECHCHANCE = 9;
        public static final int LIFELEECHAMOUT = 10;
        public static final int MANALEECHCHANCE = 11;
        public static final int MANAlEECHMOUNT = 12;
        public static final int LASTSKILL = 13;
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST, INVALID_DIRECTION;

        public static Direction fromInt(int val) {
            switch (val) {
                case 0:
                    return NORTH;
                case 1:
                    return EAST;
                case 2:
                    return SOUTH;
                case 3:
                    return WEST;
                case 4:
                    return NORTH_EAST;
                case 5:
                    return SOUTH_EAST;
                case 6:
                    return SOUTH_WEST;
                case 7:
                    return NORTH_WEST;
                case 8:
                    return INVALID_DIRECTION;
            }
        }
    }
}
